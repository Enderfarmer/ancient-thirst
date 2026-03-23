package com.thirst.mass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.ModEntityTags;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public class MassState extends PersistentState {
    private int mass = 0;
    private Stage stage = Stage.AWAKENING;

    // Stage tresholds
    private int BUILDING_TRESHOLD = 100;
    private int DEVOURING_TRESHOLD = 10000;
    private int DOMINATING_TRESHOLD = 100000;

    private void updateStage(int mass) {
        if (mass > DOMINATING_TRESHOLD) {
            stage = Stage.DOMINATING;
        } else if (mass > DEVOURING_TRESHOLD) {
            stage = Stage.DEVOURING;
        } else if (mass > BUILDING_TRESHOLD) {
            stage = Stage.BUILDING;
        }
    }

    public int getMass() {
        return mass;
    }

    public Stage getStage() {
        return stage;
    }

    public void onKillAssignMass(EntityType type) {
        if (type.isIn(ModEntityTags.MINOR_GROWTH)) {
            mass += MassMappings.MINOR;
        } else if (type.isIn(ModEntityTags.PASSIVE_SOURCE)) {
            mass += MassMappings.SLAUGHTER;
        } else if (type.isIn(ModEntityTags.HOSTILE_TARGET)) {
            mass += MassMappings.AGGRESSIVE;
        } else if (type.isIn(EntityTypeTags.RAIDERS)) {
            mass += MassMappings.ILLAGER;
        }
    }

    public void onKill(Entity killed) {
        onKillAssignMass(killed.getType());
        this.markDirty();
    }

    public void onInfectBlockAssignMass(BlockState blockState) {
        mass += MassMappings.BLOCK;
    }

    public void onInfectBlock(BlockState blockState) {
        onInfectBlockAssignMass(blockState);
        this.markDirty();
    }

    public MassState(int mass) {
        this.mass = mass;
        updateStage(mass);
    }

    public MassState() {
    }

    public static Codec<MassState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("mass").forGetter(s -> s.mass)).apply(instance, MassState::new));
    public static final PersistentStateType<MassState> TYPE = new PersistentStateType<MassState>(
            "mass",
            MassState::new,
            CODEC,
            null);

    public static MassState getServerState(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
    }
}
