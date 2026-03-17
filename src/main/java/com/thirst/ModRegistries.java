package com.thirst;

import com.thirst.systems.formation.FormationType;
import com.thirst.systems.formation.types.CircleFormation;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModRegistries {
    // 1. Define the Registry Key
    public static final RegistryKey<Registry<FormationType<?>>> FORMATION_REGISTRY_KEY = RegistryKey
            .ofRegistry(ThirstId.id("formations"));

    // 2. Initialize the Registry
    public static final Registry<FormationType<?>> FORMATION_REGISTRY = FabricRegistryBuilder
            .createSimple(FORMATION_REGISTRY_KEY).buildAndRegister();

    public static void init() {
        // 3. Register entries
        Registry.register(FORMATION_REGISTRY, ThirstId.id("circle"), new FormationType<>(CircleFormation.CODEC));
    }
}
