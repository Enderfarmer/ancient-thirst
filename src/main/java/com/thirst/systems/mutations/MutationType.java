package com.thirst.systems.mutations;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import com.thirst.systems.mutations.types.MutationBase;

public record MutationType<T extends MutationBase>(MapCodec<T> codec, Supplier<T> factory) {
    public static <T extends MutationBase> MutationType<T> create(MapCodec<T> codec, Supplier<T> factory) {
        return new MutationType<>(codec, factory);
    }
}
