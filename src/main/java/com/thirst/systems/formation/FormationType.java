package com.thirst.systems.formation;

import com.mojang.serialization.MapCodec;
import com.thirst.systems.formation.types.FormationBase;

public record FormationType<T extends FormationBase>(MapCodec<T> codec) {
    // This record holds the codec that knows how to read/write the formation
}