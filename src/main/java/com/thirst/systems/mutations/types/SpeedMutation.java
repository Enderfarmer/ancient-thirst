package com.thirst.systems.mutations.types;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.thirst.common.ModEntityTags;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;

public class SpeedMutation extends KillBasedMutation {
    @Override
    public String getType() {
        return "speed";
    }

    @Override
    protected List<Integer> getTresholds() {
        return List.of(30, 70);
    }

    @Override
    protected List<Double> getValues() {
        return List.of(0.05, 0.1);
    }

    @Override
    public void onKill(EntityType entityType) {
        super.onKill(entityType, ModEntityTags.SPEED_BOOST);
    }

    @Override
    protected RegistryEntry<EntityAttribute> getAttributeToUpgrade() {
        return EntityAttributes.MOVEMENT_SPEED;
    }

    public SpeedMutation(int kills) {
        super(kills);
    }

    public static final MapCodec<SpeedMutation> CODEC = genCodec(SpeedMutation::new);
}
