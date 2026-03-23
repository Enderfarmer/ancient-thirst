package com.thirst;

import com.thirst.systems.formation.FormationType;
import com.thirst.systems.formation.types.CircleFormation;
import com.thirst.systems.mutations.MutationType;
import com.thirst.systems.mutations.types.RegenMutation;
import com.thirst.systems.mutations.types.ShellMutation;
import com.thirst.systems.mutations.types.SpeedMutation;
import com.thirst.systems.mutations.types.StrengthMutation;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModRegistries {
        public static final RegistryKey<Registry<FormationType<?>>> FORMATION_REGISTRY_KEY = RegistryKey
                        .ofRegistry(ThirstId.id("formations"));
        public static final RegistryKey<Registry<MutationType<?>>> UPGRADE_REGISTRY_KEY = RegistryKey
                        .ofRegistry(ThirstId.id("upgrades"));
        public static final Registry<FormationType<?>> FORMATION_REGISTRY = FabricRegistryBuilder
                        .createSimple(FORMATION_REGISTRY_KEY).buildAndRegister();
        public static final Registry<MutationType<?>> UPGRADE_REGISTRY = FabricRegistryBuilder
                        .createSimple(UPGRADE_REGISTRY_KEY).buildAndRegister();

        public static void init() {
                Registry.register(FORMATION_REGISTRY, ThirstId.id("circle"),
                                new FormationType<>(CircleFormation.CODEC));
                Registry.register(UPGRADE_REGISTRY, ThirstId.id("strength"),
                                new MutationType<>(StrengthMutation.CODEC, () -> new StrengthMutation(0)));
                Registry.register(UPGRADE_REGISTRY, ThirstId.id("speed"),
                                new MutationType<>(SpeedMutation.CODEC, () -> new SpeedMutation(0)));
                Registry.register(UPGRADE_REGISTRY, ThirstId.id("shell"),
                                new MutationType<>(ShellMutation.CODEC, () -> new ShellMutation(0)));
                Registry.register(UPGRADE_REGISTRY, ThirstId.id("regen"),
                                new MutationType<>(RegenMutation.CODEC, () -> new RegenMutation(0)));
        }
}
