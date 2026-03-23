package com.thirst.systems.mutations.types;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.thirst.ModEntityTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;

public class StrengthMutation extends KillBasedMutation {
    @Override
    public String getType() {
        return "strength";
    }

    @Override
    protected List<Double> getValues() {
        return List.of(2.0, 4.0);
    }

    @Override
    protected List<Integer> getTresholds() {
        return List.of(50, 80);
    }

    @Override
    protected RegistryEntry<EntityAttribute> getAttributeToUpgrade() {
        return EntityAttributes.ATTACK_DAMAGE;
    }

    @Override
    public void onKill(EntityType entityType) {
        super.onKill(entityType, ModEntityTags.STRENGTH_BOOST);
    }

    public StrengthMutation(int kills) {
        super(kills);
        this.kills = kills;
    }

    public static final MapCodec<StrengthMutation> CODEC = genCodec(StrengthMutation::new);
}
