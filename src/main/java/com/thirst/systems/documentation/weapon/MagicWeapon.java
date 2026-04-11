package com.thirst.systems.documentation.weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MagicWeapon(float damage) implements Weapon {
    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public WeaponType getType() {
        return WeaponType.MAGIC;
    }

    public static final MapCodec<MagicWeapon> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("damage").forGetter(MagicWeapon::damage)).apply(inst, MagicWeapon::new));
}