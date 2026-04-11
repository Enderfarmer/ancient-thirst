package com.thirst.systems.documentation.weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class KineticWeapon implements Weapon {
    float minSpeed;

    public float getMinSpeed() {
        return minSpeed;
    }

    float baseDamage;

    public float getBaseDamage() {
        return baseDamage;
    }

    @Override
    public WeaponType getType() {
        return WeaponType.KINETIC;
    }

    @Override
    public float getDamage() {
        return minSpeed * baseDamage;
    }

    public KineticWeapon(float minSpeed, float baseDamage) {
        this.minSpeed = minSpeed;
        this.baseDamage = baseDamage;
    }

    public static final MapCodec<KineticWeapon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("minSpeed").forGetter(KineticWeapon::getMinSpeed),
            Codec.FLOAT.fieldOf("baseDamage").forGetter(KineticWeapon::getBaseDamage))
            .apply(instance, KineticWeapon::new));
}
