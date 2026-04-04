package com.thirst.systems.mutations.types;

import java.util.List;
import java.util.function.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.AncientThirst;
import com.thirst.common.entity.Unit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public abstract class KillBasedMutation extends MutationBase {
    /**
     * Tresholds for the values (ascending order)
     */
    protected abstract List<Integer> getTresholds();

    /**
     * Values for the tresholds (ascending order)
     */
    protected abstract List<Double> getValues();

    protected abstract RegistryEntry<EntityAttribute> getAttributeToUpgrade();

    protected int kills = 0;

    @Override
    public void onBlockInfect(BlockState blockState) {
    }

    public void onKill(EntityType entityType, TagKey<EntityType<?>> tag) {
        AncientThirst.LOGGER.info("Calling onKill for KillBasedUpgrade child type");
        if (kills >= getTresholds().getLast())
            return;
        if (entityType.isIn(tag)) {
            kills++;
            AncientThirst.LOGGER.info("Got a kill. Kills: " + kills);
        }
    }

    public KillBasedMutation(int kills) {
        this.kills = kills;
    }

    @Override
    protected int getCounter() {
        return kills;
    }

    @Override
    public void processUnitInternal(Unit mob) {
        upgradeByCounterTresholdsValues(kills, getTresholds(), getValues(), mob,
                getAttributeToUpgrade(), getId());
    }

    protected static final <T extends KillBasedMutation> MapCodec<T> genCodec(
            Function<Integer, T> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("kills").forGetter(u -> u.kills)).apply(instance, constructor));
    }
}
