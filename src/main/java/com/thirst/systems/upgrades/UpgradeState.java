package com.thirst.systems.upgrades;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.thirst.AncientThirst;
import com.thirst.ModRegistries;
import com.thirst.entity.Unit;
import com.thirst.systems.upgrades.types.UpgradeBase;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public class UpgradeState extends PersistentState {
    public final Map<Identifier, UpgradeBase> activeUpgrades = new HashMap<>();

    public UpgradeState() {
        fillMissing();
    }

    public UpgradeState(Map<Identifier, UpgradeBase> map) {
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
    public static final Codec<UpgradeState> CODEC = Codec.unboundedMap(
            Identifier.CODEC,
            UpgradeBase.DISPATCH_CODEC).xmap(UpgradeState::new, state -> state.activeUpgrades);
    public static final PersistentStateType<UpgradeState> TYPE = new PersistentStateType<UpgradeState>(
            "upgrades",
            UpgradeState::new,
            CODEC,
            null);

    public static UpgradeState getServerState(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
    }
}