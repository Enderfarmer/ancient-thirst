package com.thirst.common;

import com.thirst.ThirstId;
import com.thirst.common.effect.ParalysisEffect;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class ModEffects {
    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, ThirstId.id(id), statusEffect);
    }

    public static final RegistryEntry<StatusEffect> PARALYSIS = register("paralysis", new ParalysisEffect());

    public static void init() {
        // This method is intentionally left blank. It is used to trigger the static
        // initializers.
    }
}
