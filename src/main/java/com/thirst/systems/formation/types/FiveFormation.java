package com.thirst.systems.formation.types;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.AncientThirst;
import com.thirst.Utils;
import com.thirst.common.entity.Unit;
import com.thirst.common.entity.UnitType;
import com.thirst.systems.formation.FormationState;
import com.thirst.systems.formation.FormationedAttackState;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FiveFormation extends FormationBase {
    public static final float DIST_FROM_CENTER = 2.2f;
    public static final MapCodec<FiveFormation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPos.CODEC.fieldOf("target").forGetter(f -> f.targetLocation),
            Codec.list(Uuids.CODEC).fieldOf("members").forGetter(f -> f.members),
            Codec.STRING.fieldOf("state").forGetter(f -> f.state.toString()),
            Codec.BOOL.fieldOf("initialized").forGetter(f -> f.initialized)).apply(instance, FiveFormation::new));

    protected static Vec3d transformToValidPos(Entity leader, Vec3d rawPos) {
        Vec3d validPos = new Vec3d(rawPos.x,
                Utils.findValidY(leader.getEntityWorld(), rawPos.x, rawPos.z, rawPos.getY()), rawPos.z);
        return validPos;
    }

    @Override
    protected Vec3d calculateSlot(Entity leader, int indexInGroup, int groupUnitCount, UnitType type, UUID memberId) {
        int index = members.indexOf(memberId);
        AncientThirst.LOGGER.info("Calculating slot for member with index " + index);
        Vec3d rawPos = new Vec3d(targetLocation);
        switch (index) {
            case 1:
                rawPos = rawPos.add(DIST_FROM_CENTER, 0, DIST_FROM_CENTER);
                break;
            case 2:
                rawPos = rawPos.add(-DIST_FROM_CENTER, 0, DIST_FROM_CENTER);
                break;
            case 3:
                rawPos = rawPos.add(DIST_FROM_CENTER, 0, -DIST_FROM_CENTER);
                break;
            case 4:
                rawPos = rawPos.add(-DIST_FROM_CENTER, 0, -DIST_FROM_CENTER);
                break;
            default:
                return rawPos;
        }
        return transformToValidPos(leader, rawPos);
    }

    @Override
    public String getType() {
        return "five";
    }

    @Override
    public void update(MinecraftServer server) {
        super.update(server);
        state = FormationState.BUILDING;
        if (members.size() > 5) {
            members = members.subList(0, 5);
        }
        members.forEach(uuid -> {
            Unit entity = (Unit) server.getOverworld().getEntity(uuid);
            entity.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE,
                    2,
                    3,
                    false,
                    false,
                    false));
            AncientThirst.LOGGER.info("My formation slot: " + calculateSlot(entity, 0, 0, entity.getUnitType(), uuid));
        });
    }

    @Override
    public void acceptMember(Unit memberToBe) {
        if (members.size() >= 5) {
            return;
        }
        if (memberToBe.getUnitType() == UnitType.GROUND || memberToBe.getUnitType() == UnitType.PSYCHEDELIC) {
            return;
        }
        super.acceptMember(memberToBe);
    }

    public static void startAttack(MinecraftServer server, BlockPos target) {
        FiveFormation formation = new FiveFormation(target, null, null, false);
        UUID uuid = UUID.randomUUID();
        formation.initialized = true;
        formation.uuid = uuid;
        formation.spawnInitialMembers(server.getOverworld(), target, 5, true);
        formation.electNewLeader(server);
        FormationedAttackState.getServerState(server).addFormation(formation);
    }

    @Override
    protected void electNewLeader(MinecraftServer server) {
        leaderUuid = members.get(0);
    }

    public FiveFormation(BlockPos target, List<UUID> members, String state, boolean initialized) {
        super(target, members, state, initialized);
    }

}