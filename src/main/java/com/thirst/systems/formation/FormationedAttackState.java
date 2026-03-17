package com.thirst.systems.formation;

import com.mojang.serialization.Codec;
import com.thirst.AncientThirst;
import com.thirst.systems.formation.types.FormationBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.Map;

public class FormationedAttackState extends PersistentState {

    public Map<String, FormationBase> activeAttacks = new HashMap<>();

    public static final Codec<FormationedAttackState> STATE_CODEC = Codec.unboundedMap(
            Codec.STRING,
            FormationBase.DISPATCH_CODEC).xmap(
                    FormationedAttackState::new,
                    state -> state.activeAttacks);
    // Define the TYPE explicitly with the generic <FormationedAttackState>
    public static final PersistentStateType<FormationedAttackState> TYPE = new PersistentStateType<FormationedAttackState>(
            "infection_plans",
            FormationedAttackState::new,
            STATE_CODEC,
            null);

    // Default constructor for the factory
    public FormationedAttackState() {
        this.activeAttacks = new HashMap<>();
    }

    public FormationedAttackState(Map<String, FormationBase> activeAttacks) {
        this.activeAttacks = activeAttacks;
    }

    public void tick(MinecraftServer server) {
        try {
            this.activeAttacks.entrySet().removeIf(entry -> {
                FormationBase formation = entry.getValue();
                formation.update(server);
                boolean remove = formation.members.isEmpty() || formation.getState() == FormationState.BREAKING;
                if (remove) {
                    AncientThirst.LOGGER.info("Removing formation: " + formation.uuid);
                }
                return remove;
            });
        } catch (Exception e) {
            // The server is trying to be funny with a hilarious
            // UnsupportedOperationException
            this.activeAttacks = new HashMap<>(this.activeAttacks);
        }
    }

    // 6. Accessor
    public static FormationedAttackState getServerState(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(
                TYPE);
    }
}