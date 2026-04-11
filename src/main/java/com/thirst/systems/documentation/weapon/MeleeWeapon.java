package com.thirst.systems.documentation.weapon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.common.entity.Unit;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public record MeleeWeapon(float damage, float attackSpeed) implements Weapon {

    public WeaponType getType() {
        return WeaponType.MELEE;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    public static MeleeWeapon of(Item item, Unit attacked, LivingEntity attacker) {
        float damage = item.getBonusAttackDamage(attacked,
                (float) attacker.getAttributeValue(EntityAttributes.ATTACK_DAMAGE), item.getDamageSource(attacker));
        float attackSpeed = (float) getAttackSpeed(item, attacker);
        return new MeleeWeapon(damage, attackSpeed);
    }

    public static double getAttackSpeed(Item stack, LivingEntity attacker) {
        AttributeModifiersComponent modifiers = stack.getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

        if (modifiers != null) {
            for (AttributeModifiersComponent.Entry entry : modifiers.modifiers()) {
                if (entry.attribute().equals(EntityAttributes.ATTACK_SPEED)) {
                    return attacker.getAttributeValue(EntityAttributes.ATTACK_SPEED) + entry.modifier().value();
                }
            }
        }

        return attacker.getAttributeValue(EntityAttributes.ATTACK_SPEED);
    }

    public static final MapCodec<MeleeWeapon> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("damage").forGetter(MeleeWeapon::getDamage),
            Codec.FLOAT.fieldOf("speed").forGetter(MeleeWeapon::attackSpeed)).apply(inst, MeleeWeapon::new));

    public static MapCodec<MeleeWeapon> getCodec() {
        return CODEC;
    }

    @Override
    public final String toString() {
        return "A melee weapon with the damage " + damage + " and attack speed " + attackSpeed;
    }
}
