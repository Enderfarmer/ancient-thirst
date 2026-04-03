package com.thirst.effect;

import com.thirst.ThirstId;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class ParalysisEffect extends StatusEffect {
    public ParalysisEffect() {
        super(StatusEffectCategory.HARMFUL, 0x98D982);
        this.addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, ThirstId.id("effect.paralysis"), -1.0d,
                Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(EntityAttributes.JUMP_STRENGTH, ThirstId.id("effect.paralysis"), -1.0d,
                Operation.ADD_MULTIPLIED_TOTAL);
        // StatusEffects.SLOWNESS
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        if (duration > 0) {
            return false;
        }
        return true;
    }
}
