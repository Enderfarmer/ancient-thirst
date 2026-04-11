package com.thirst.systems.documentation.weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HandWeapon(float baseDamage) implements Weapon {
    public static final MapCodec<HandWeapon> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(Codec.FLOAT.fieldOf("baseDamage").forGetter(HandWeapon::baseDamage))
                    .apply(instance, HandWeapon::new));

    @Override
    public WeaponType getType() {
        return WeaponType.HAND;
    }

    @Override
    public float getDamage() {
        return 1;
    }

    @Override
    public final String toString() {
        return "A hand with the baseDamage " + baseDamage;
    }
}
