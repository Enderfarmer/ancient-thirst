package com.thirst.systems.mutations;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.thirst.AncientThirst;
import com.thirst.ModRegistries;
import com.thirst.entity.Unit;
import com.thirst.systems.mutations.types.MutationBase;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public class MutationState extends PersistentState {
    public final Map<Identifier, MutationBase> activeUpgrades = new HashMap<>();

    public MutationState() {
        fillMissing();
    }

    public MutationState(Map<Identifier, MutationBase> map) {
        this.activeUpgrades.putAll(map);
        fillMissing();
    }

    private void fillMissing() {
        // Automatically populates the map with EVERY upgrade in your registry
        for (Identifier id : ModRegistries.UPGRADE_REGISTRY.getIds()) {
            activeUpgrades.computeIfAbsent(id, key -> ModRegistries.UPGRADE_REGISTRY.get(key).factory().get());
        }
    }

    public void processUnit(Unit mob) {
        activeUpgrades.forEach((id, upgrade) -> upgrade.processUnit(mob));
    }

    public void onKill(EntityType type) {
        AncientThirst.LOGGER.info("Calling onKill for UpgradeState with activeUpgrades being: " + activeUpgrades);
        activeUpgrades.forEach((id, upgrade) -> upgrade.onKill(type));
        this.markDirty();
    }

    public void onBlockInfect(BlockState state) {
        activeUpgrades.forEach((id, upgrade) -> upgrade.onBlockInfect(state));
        this.markDirty();
    }

    // Standard Codec logic for the Map
    public static final Codec<MutationState> CODEC = Codec.unboundedMap(
            Identifier.CODEC,
            MutationBase.DISPATCH_CODEC).xmap(MutationState::new, state -> state.activeUpgrades);
    public static final PersistentStateType<MutationState> TYPE = new PersistentStateType<MutationState>(
            "upgrades",
            MutationState::new,
            CODEC,
            null);

    public static MutationState getServerState(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
    }
}