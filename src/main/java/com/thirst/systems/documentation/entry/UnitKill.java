package com.thirst.systems.documentation.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.common.entity.UnitType;
import com.thirst.systems.documentation.weapon.Weapon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public record UnitKill(float damage, ItemStack weaponItem, Weapon weapon, float totalUnitHealth) {
    public static final Codec<UnitKill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damage").forGetter(UnitKill::damage),
            ItemStack.CODEC.fieldOf("weaponItem").forGetter(UnitKill::weaponItem),
            Weapon.DISPATCH_CODEC.fieldOf("weapon").forGetter(UnitKill::weapon),
            Codec.FLOAT.fieldOf("totalUnitHealth").forGetter(UnitKill::totalUnitHealth))
            .apply(instance, UnitKill::new));
}
