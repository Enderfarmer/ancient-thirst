package com.thirst.systems.upgrades;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import com.thirst.systems.upgrades.types.UpgradeBase;

public record UpgradeType<T extends UpgradeBase>(MapCodec<T> codec, Supplier<T> factory) {
    public static <T extends UpgradeBase> UpgradeType<T> create(MapCodec<T> codec, Supplier<T> factory) {
        return new UpgradeType<>(codec, factory);
    }
}
