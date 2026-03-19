package com.thirst.systems.formation.types;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.Utils;
import com.thirst.entity.UnitType;
import com.thirst.systems.formation.FormationState;
import com.thirst.systems.formation.FormationedAttackState;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CircleFormation extends FormationBase {
    String type = "circle";
    public static final MapCodec<FormationBase> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPos.CODEC.fieldOf("target").forGetter(f -> f.targetLocation),
            Codec.list(Uuids.CODEC).fieldOf("members").forGetter(f -> f.members),
            Codec.STRING.fieldOf("state").forGetter(f -> f.state.toString()),
            Codec.BOOL.fieldOf("initialized").forGetter(f -> f.initialized)).apply(instance, CircleFormation::new));

    @Override
    protected void electNewLeader(MinecraftServer server) {
        this.leaderUuid = members.getFirst();

    }

    @Override
    public String getType() {
        return "circle";
    }

    public CircleFormation(BlockPos target, List<UUID> members, String state, boolean initialized) {
        super(target, members, state, initialized);
    }

    public static void startAttack(MinecraftServer server, BlockPos targetLocation) {
        FormationedAttackState state = FormationedAttackState.getServerState(server);
        CircleFormation formation = new CircleFormation(targetLocation, null, null, false);
        UUID uuid = UUID.randomUUID();
        formation.uuid = uuid;
        state.activeAttacks.put(uuid.toString(), formation);
        state.markDirty();
        Utils.log("Created a formation targeting " + targetLocation, server.getOverworld().getPlayers().get(0));
    }

    @Override
    protected Vec3d calculateSlot(Entity leader, int indexInGroup, int groupUnitCount, UnitType type) {
        Vec3d rawPos = getCirclePosition(indexInGroup, groupUnitCount);
        Vec3d realPosWithoutY = new Vec3d(rawPos.x, 0, rawPos.z);
        Function<Integer, Vec3d> genPos = translateToRealPosWithConstantPosAndWorld(realPosWithoutY,
                leader.getEntityWorld(), leader.getY());
        if (this.state == FormationState.BUILDING) {
            switch (type) {
                case MELEE:
                    return genPos.apply(4);
                case RANGED:
                    return genPos.apply(6);
                case GROUND:
                    return genPos.apply(8);
                case PSYCHEDELIC:
                    return genPos.apply(10);
            }
        } else if (this.state == FormationState.ATTACKING) {
            switch (type) {
                case MELEE:
                    return genPos.apply(2);
                case RANGED:
                    return genPos.apply(5);
                case GROUND:
                    return genPos.apply(8);
                case PSYCHEDELIC:
                    return genPos.apply(3);
            }
        }
        return null;
    }

    protected Vec3d getCirclePosition(int index, int maxCount) {
        // 360° / maxCount is the angle between the points
        // And * index to get the angle
        double angle = Math.PI * 2 / maxCount * index;

        // Unit circle: the cosine is the x value
        double x = Math.cos(angle);

        // Unit circle: the sine is the y value (z value in our case)
        double z = Math.sin(angle);
        Vec3d vec3d = new Vec3d(x, 0, z);
        return vec3d;
    }

    protected Vec3d translateToRealPos(Vec3d calculated, int radius, World world, double shouldBeCloseToY) {
        Vec3d withoutY = new Vec3d(calculated.x * radius, 0, calculated.z * radius);
        Vec3d result = new Vec3d(withoutY.x + targetLocation.getX(), Utils.findValidY(world, withoutY.x
                + targetLocation.getX(), withoutY.z + targetLocation.getZ(), shouldBeCloseToY), withoutY.z
                        + targetLocation.getZ());
        return result;
    }

    protected Function<Integer, Vec3d> translateToRealPosWithConstantPosAndWorld(Vec3d calculated, World world,
            double leaderY) {
        return (radius) -> translateToRealPos(calculated, radius, world, leaderY);
    }
}
