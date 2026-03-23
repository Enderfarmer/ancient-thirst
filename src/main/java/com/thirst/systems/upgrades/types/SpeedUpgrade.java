package com.thirst.systems.upgrades.types;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.thirst.ModEntityTags;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;

public class SpeedUpgrade extends KillBasedUpgrade {
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

    public SpeedUpgrade(int kills) {
        super(kills);
    }

    public static final MapCodec<SpeedUpgrade> CODEC = genCodec(SpeedUpgrade::new);
}
