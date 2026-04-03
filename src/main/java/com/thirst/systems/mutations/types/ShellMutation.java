package com.thirst.systems.mutations.types;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.thirst.common.ModEntityTags;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;

public class ShellMutation extends KillBasedMutation {
    @Override
    protected RegistryEntry<EntityAttribute> getAttributeToUpgrade() {
        return EntityAttributes.ARMOR;
    }

    @Override
    protected List<Integer> getTresholds() {
        return List.of(40, 80);
    }

    @Override
    protected List<Double> getValues() {
        return List.of(4.0, 8.0);
    }

    @Override
    public String getType() {
        return "shell";
    }

    @Override
    public void onKill(EntityType entityType) {
        super.onKill(entityType, ModEntityTags.SHELL_BOOST);
    }

    public ShellMutation(int kills) {
        super(kills);
    }

    public static final MapCodec<ShellMutation> CODEC = genCodec(ShellMutation::new);
}
