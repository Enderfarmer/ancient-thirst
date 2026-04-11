package com.thirst.systems.documentation.weapon;

import com.mojang.serialization.Codec;

public interface Weapon {
    // The base "Lethality" for the rule-based model
    float getDamage();

    WeaponType getType();

    enum WeaponType {
        MELEE, RANGED, MAGIC, KINETIC, HAND
    }

    public static final Codec<Weapon> DISPATCH_CODEC = Codec.STRING.dispatch(
            "weapon_type",
            w -> w.getType().name().toLowerCase(),
            type -> switch (type) {
                case "melee" -> MeleeWeapon.CODEC;
                case "ranged" -> RangedWeapon.CODEC;
                case "magic" -> MagicWeapon.CODEC;
                case "kinetic" -> KineticWeapon.CODEC;
                case "hand" -> HandWeapon.CODEC;
                default -> throw new IllegalArgumentException("Unknown Hive threat: " + type);
            });
}
