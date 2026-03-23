package com.thirst;

import com.thirst.systems.formation.FormationType;
import com.thirst.systems.formation.types.CircleFormation;
import com.thirst.systems.upgrades.UpgradeType;
import com.thirst.systems.upgrades.types.SpeedUpgrade;
import com.thirst.systems.upgrades.types.StrengthUpgrade;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModRegistries {
        public static final RegistryKey<Registry<FormationType<?>>> FORMATION_REGISTRY_KEY = RegistryKey
                        .ofRegistry(ThirstId.id("formations"));
        public static final RegistryKey<Registry<UpgradeType<?>>> UPGRADE_REGISTRY_KEY = RegistryKey
                        .ofRegistry(ThirstId.id("upgrades"));
        public static final Registry<FormationType<?>> FORMATION_REGISTRY = FabricRegistryBuilder
                        .createSimple(FORMATION_REGISTRY_KEY).buildAndRegister();
        public static final Registry<UpgradeType<?>> UPGRADE_REGISTRY = FabricRegistryBuilder
                        .createSimple(UPGRADE_REGISTRY_KEY).buildAndRegister();

        public static void init() {
                Registry.register(FORMATION_REGISTRY, ThirstId.id("circle"),
                                new FormationType<>(CircleFormation.CODEC));
                Registry.register(UPGRADE_REGISTRY, ThirstId.id("strength"),
                                new UpgradeType<>(StrengthUpgrade.CODEC, () -> new StrengthUpgrade(0)));
                Registry.register(UPGRADE_REGISTRY, ThirstId.id("speed"),
                                new UpgradeType<>(SpeedUpgrade.CODEC, () -> new SpeedUpgrade(0)));
        }
}
