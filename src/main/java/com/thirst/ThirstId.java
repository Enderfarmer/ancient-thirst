package com.thirst;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ThirstId {
    public static Identifier id(String name) {
        return Identifier.of(AncientThirst.MOD_ID, name);
    }

    public static RegistryKey registryKey(RegistryKey reg, String name) {
        return RegistryKey.of(reg, id(name));
    }
}
