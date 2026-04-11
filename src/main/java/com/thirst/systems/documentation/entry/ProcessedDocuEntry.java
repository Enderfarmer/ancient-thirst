package com.thirst.systems.documentation.entry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.AncientThirst;
import com.thirst.systems.documentation.entry.calculations.CalculateThreat;
import com.thirst.systems.documentation.entry.calculations.Lethalities;
import com.thirst.systems.documentation.entry.calculations.ProcessingSettings;

import net.minecraft.enchantment.Enchantments;

public class ProcessedDocuEntry {
    float meleeLethality = 0;
    float rangedLethality = 0;
    float movementLethality = 0;
    float magicLethality = 0;
    double totalThreat = 0;
    boolean reliesOnMobility = false;
    boolean usesSweepAttacks = false;
    boolean isOneShotting = false;
    boolean unknown = true;

    public float getMeleeLethality() {
        return meleeLethality;
    }

    public float getRangedLethality() {
        return rangedLethality;
    }

    public float getMovementLethality() {
        return movementLethality;
    }

    public float getMagicLethality() {
        return magicLethality;
    }

    public double getTotalThreat() {
        return totalThreat;
    }

    public boolean reliesOnMobility() {
        return reliesOnMobility;
    }

    public boolean usesSweepAttacks() {
        return usesSweepAttacks;
    }

    public boolean isOneShotting() {
        return isOneShotting;
    }

    public boolean isUnknown() {
        return unknown;
    }

    public ProcessedDocuEntry(float meleeLethality, float rangedLethality, float maceLethality, float magicLethality,
            double totalThreat, boolean reliesOnMobility, boolean usesSweepAttacks, boolean isOneShotting,
            boolean unknown) {
        this.meleeLethality = meleeLethality;
        this.rangedLethality = rangedLethality;
        this.movementLethality = maceLethality;
        this.magicLethality = magicLethality;
        this.totalThreat = totalThreat;
        this.reliesOnMobility = reliesOnMobility;
        this.usesSweepAttacks = usesSweepAttacks;
        this.isOneShotting = isOneShotting;
        this.unknown = unknown;
    }

    public ProcessedDocuEntry() {
    }

    public static Lethalities lethalitiesAnalysis(List<UnitKill> kills) {
        float DAMAGE_MULTIPLIER = .1f;
        float meleeLethality = 0;
        float rangedLethality = 0;
        float magicLethality = 0;
        float movementLethality = 0;

        for (UnitKill kill : kills) {
            float damage = kill.damage();
            damage = new BigDecimal(damage).setScale(1, RoundingMode.HALF_UP).floatValue();
            AncientThirst.LOGGER.info("The weapon type: " + kill.weapon().getType());
            switch (kill.weapon().getType()) {
                case MELEE, HAND:
                    meleeLethality += DAMAGE_MULTIPLIER * damage;
                    break;
                case RANGED:
                    rangedLethality += DAMAGE_MULTIPLIER * damage;
                    break;
                case MAGIC:
                    magicLethality += DAMAGE_MULTIPLIER * damage;
                    break;
                case KINETIC:
                    movementLethality += DAMAGE_MULTIPLIER * kill.weapon().getDamage();
            }
        }
        return new Lethalities(meleeLethality, rangedLethality, magicLethality, movementLethality);
    }

    public static List<Integer> oneShotAndSweepKillsAnalysis(List<UnitKill> kills) {
        int oneShotKills = 0;
        int sweepKills = 0;
        for (UnitKill kill : kills) {
            if (kill.damage() >= kill.totalUnitHealth() * 0.8f) {
                oneShotKills++;
            }
            if (kill.weaponItem().getEnchantments().getEnchantmentEntries().stream()
                    .anyMatch(enchantment -> enchantment.getKey().getKey().get().equals(Enchantments.SWEEPING_EDGE))) {
                sweepKills++;
            }
        }
        return List.of(oneShotKills, sweepKills);
    }

    public static ProcessedDocuEntry analyzeRaw(RawDocuEntry raw) {
        List<Integer> oneShotAndSweepKills = oneShotAndSweepKillsAnalysis(raw.getUnitKills());
        int oneShotKills = oneShotAndSweepKills.get(0);
        int sweepKills = oneShotAndSweepKills.get(1);
        Lethalities lethalities = lethalitiesAnalysis(raw.getUnitKills());
        float meleeLethality = lethalities.melee();
        float rangedLethality = lethalities.ranged();
        float magicLethality = lethalities.magic();
        float movementLethality = lethalities.movement();
        switch (raw.getAdvancedAttributes().movement) {
            case NONE:
                movementLethality *= 0.5;
                break;
            case ALITTLE:
                movementLethality *= 1.1;
                break;
            case ALOT:
                movementLethality *= 1.5;
        }
        boolean isOneShotting = oneShotKills > ProcessingSettings.PropertyTresholds.ONE_SHOTTING;
        boolean usesSweepAttacks = sweepKills > ProcessingSettings.PropertyTresholds.IS_SWEEPING;

        CalculateThreat threatCalculator = new CalculateThreat(raw.getBasicAttributes(), raw.getAdvancedAttributes(),
                new Lethalities(meleeLethality, rangedLethality, magicLethality, movementLethality));
        boolean reliesOnMobility = raw.getAdvancedAttributes().movement == Amplifier.ALOT || movementLethality > 30;
        double totalThreat = threatCalculator.calculate();
        AncientThirst.LOGGER.info("Melee lethality: " + meleeLethality, "Total threat: " + totalThreat);
        return new ProcessedDocuEntry(meleeLethality, rangedLethality, movementLethality, magicLethality, totalThreat,
                reliesOnMobility, usesSweepAttacks, isOneShotting, false);
    }

    public void reevaluate(RawDocuEntry raw) {
        // TODO: Implement a reevaluation for adding data to the entry while keeping it
        // flexible if the player changes tactics
    }

    public static final Codec<ProcessedDocuEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("meleeLethality").forGetter(ProcessedDocuEntry::getMeleeLethality),
            Codec.FLOAT.fieldOf("rangedLethality").forGetter(ProcessedDocuEntry::getRangedLethality),
            Codec.FLOAT.fieldOf("movementLethality").forGetter(ProcessedDocuEntry::getMovementLethality),
            Codec.FLOAT.fieldOf("magicLethality").forGetter(ProcessedDocuEntry::getMagicLethality),
            Codec.DOUBLE.fieldOf("totalLethality").forGetter(ProcessedDocuEntry::getTotalThreat),
            Codec.BOOL.fieldOf("reliesOnMobility").forGetter(ProcessedDocuEntry::reliesOnMobility),
            Codec.BOOL.fieldOf("usesSweepAttacks").forGetter(ProcessedDocuEntry::usesSweepAttacks),
            Codec.BOOL.fieldOf("oneShotting").forGetter(ProcessedDocuEntry::isOneShotting),
            Codec.BOOL.fieldOf("unknown").forGetter(ProcessedDocuEntry::isUnknown))
            .apply(instance, ProcessedDocuEntry::new));

}
