package com.thirst;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class ModSounds {
    private static SoundEvent create(String name) {
        return SoundEvent.of(ThirstId.id(name));
    }

    private static SoundEvent register(String name) {
        return Registry.register(Registries.SOUND_EVENT, ThirstId.id(name), create(name));
    }

    private static SoundEvent register(SoundEvent sound) {
        return Registry.register(Registries.SOUND_EVENT, sound.id(), sound);
    }

    public static final SoundEvent FLESH_GROWING = register("unit.mutation.heal");
    public static final SoundEvent INFILTRATION_INFECT = register("infiltration.infect");
    public static final SoundEvent INFILTRATION_AMBIENT = register("infiltration.ambient");
    public static final SoundEvent INFILTRATION_AMBUSH = register("infiltration.ambush");

    public static void init() {
        // Force class loading
    }
}
