package com.thirst;

import java.io.ObjectInputFilter.Status;
import java.util.List;

import com.thirst.FactoredDecision.Factor;
import com.thirst.common.entity.Infectable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;

public class AmbushConfig {
        public static final float BASE_CHANCE = 0.2f;
        public static final int MIN_INFECTED_NEARBY = 5;
        public static final int AMBUSH_RADIUS = 12;
        public static final int AMBUSH_CALL_RADIUS = 32;
        public static final int AMBUSH_INTERVAL = 20 * 60 * 5;
        public static final float MAX_PLAYER_HEALTH_PERCENT = 0.5f;
        public static final List<RegistryEntry<StatusEffect>> POTS = List.of(
                        StatusEffects.STRENGTH,
                        StatusEffects.SPEED,
                        StatusEffects.REGENERATION);

        public static List<LivingEntity> getSpies(PlayerEntity player) {
                return player.getEntityWorld().getEntitiesByClass(LivingEntity.class,
                                player.getBoundingBox().expand(AMBUSH_RADIUS),
                                e -> e instanceof Infectable && ((Infectable) e).isInfected());
        }

        public static final Factor<PlayerEntity> IS_POTTED_FACTOR = new Factor<>(
                        p -> !(POTS.stream().anyMatch(e -> p.hasStatusEffect(e))), 0.9f);

        public static final Factor<PlayerEntity> HEALTH_FACTOR = new Factor<>(
                        p -> p.getHealth() < p.getMaxHealth() * MAX_PLAYER_HEALTH_PERCENT,
                        0.6f);

        public static final Factor<PlayerEntity> COUNT_FACTOR = new Factor<>(
                        player -> getSpies(player).size() > MIN_INFECTED_NEARBY, 0.4f);
        public static final Factor<PlayerEntity> DARKNESS_FACTOR = new Factor<PlayerEntity>(
                        p -> p.getEntityWorld().getLightLevel(p.getBlockPos()) < 7, 0.2f);
        public static final Factor<PlayerEntity> OPEN_SKY_FACTOR = new Factor<PlayerEntity>(
                        p -> !p.getEntityWorld().isSkyVisible(p.getBlockPos()), 0.3f);
        public static final Factor<PlayerEntity> TIME_FACTOR = new Factor<PlayerEntity>(
                        p -> p.getEntityWorld().getTimeOfDay() > 13000, 0.2f);
        public static final Factor<PlayerEntity> IS_USING_ITEM_FACTOR = new Factor<PlayerEntity>(
                        PlayerEntity::isUsingItem, 0.2f);
        public static final FactoredDecision<PlayerEntity> DECISION = new FactoredDecision<PlayerEntity>(BASE_CHANCE,
                        COUNT_FACTOR,
                        HEALTH_FACTOR,
                        IS_POTTED_FACTOR,
                        DARKNESS_FACTOR,
                        OPEN_SKY_FACTOR) {
                public boolean evaluate(PlayerEntity context) {
                        if (getSpies(context).isEmpty()) {
                                return false;
                        }
                        return super.evaluate(context);
                };
        };
}
