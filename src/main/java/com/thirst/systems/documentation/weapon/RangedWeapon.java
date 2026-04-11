package com.thirst.systems.documentation.weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RangedWeapon(float damage) implements Weapon {
    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public WeaponType getType() {
        return WeaponType.RANGED;
    }

    public static final MapCodec<RangedWeapon> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("damage").forGetter(RangedWeapon::damage))
            .apply(inst, RangedWeapon::new));
}