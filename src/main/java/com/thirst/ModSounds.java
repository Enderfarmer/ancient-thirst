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

    public static final SoundEvent FLESH_GROWING = create("unit.mutation.heal");

    public static void init() {
        register("unit.mutation.heal");
    }
}
