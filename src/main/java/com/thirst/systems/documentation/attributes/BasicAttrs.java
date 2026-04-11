package com.thirst.systems.documentation.attributes;

import java.util.List;

import com.mojang.serialization.Codec;
import com.thirst.mixin.MobEntityAccessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;

public class BasicAttrs {
        public double speed;
        public double baseDamage;
        public double health;
        public double armor;
        public boolean hasHighReach;

        public static boolean isRanged(LivingEntity mob) {
                if (mob instanceof RangedAttackMob)
                        return true;
                MobEntityAccessor accessor = (MobEntityAccessor) mob;
                return accessor.getGoalSelector().getGoals().stream()
                                .anyMatch(prioritizedGoal -> prioritizedGoal.getGoal() instanceof ProjectileAttackGoal
                                                ||
                                                prioritizedGoal.getGoal().getClass().getSimpleName().toLowerCase()
                                                                .contains("shoot")
                                                ||
                                                prioritizedGoal.getGoal().getClass().getSimpleName().toLowerCase()
                                                                .contains("ranged"));
        }

        public static BasicAttrs scan(LivingEntity entity) {
                BasicAttrs attributes = new BasicAttrs();
                attributes.speed = entity
                                .getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.MOVEMENT_SPEED);
                attributes.baseDamage = entity
                                .getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE);
                attributes.health = entity
                                .getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.MAX_HEALTH);
                attributes.armor = entity.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.ARMOR);
                attributes.hasHighReach = entity.isPlayer() || isRanged(entity);
                return attributes;
        }

        public static final Codec<BasicAttrs> CODEC = Codec.FLOAT.listOf().xmap(list -> {
                BasicAttrs attributes = new BasicAttrs();
                attributes.speed = list.get(0);
                attributes.baseDamage = list.get(1);
                attributes.health = list.get(2);
                attributes.armor = list.get(3);
                attributes.hasHighReach = list.get(4) > 0;
                return attributes;
        }, attributes -> List.of((float) attributes.speed, (float) attributes.baseDamage, (float) attributes.health,
                        (float) attributes.armor, attributes.hasHighReach ? 1f : 0f));
}
