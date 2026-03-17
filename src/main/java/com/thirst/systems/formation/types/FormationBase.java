package com.thirst.systems.formation.types;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.thirst.AncientThirst;
import com.thirst.ModRegistries;
import com.thirst.ThirstId;
import com.thirst.entity.MinGroundUnitEntity;
import com.thirst.entity.Unit;
import com.thirst.entity.UnitType;
import com.thirst.systems.formation.FormationState;
import com.thirst.systems.formation.FormationedAttackState;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public abstract class FormationBase {
    protected UUID leaderUuid;
    public List<UUID> members = new ArrayList<>();
    protected final List<UUID> melees = new ArrayList<>();
    protected final List<UUID> rangers = new ArrayList<>();
    protected final List<UUID> grounds = new ArrayList<>();
    protected final List<UUID> psychedelics = new ArrayList<>();
    protected boolean initialized = false;
    public UUID uuid;

    public String toString() {
        return this.getClass() + ". Type: " + this.getType() + ".";
    }

    public abstract String getType();

    protected BlockPos targetLocation;

    public void setTargetLocation(BlockPos targetLocation) {
        this.targetLocation = targetLocation;
    }

    public BlockPos getTargetLocation() {
        return targetLocation;
    }

    protected FormationState state = FormationState.BUILDING;

    public FormationState getState() {
        return state;
    }

    protected int meleeNumber = 0;
    protected int rangedNumber = 0;
    protected int groundNumber = 0;
    protected int psychedelicNumber = 0;
    protected boolean allInPosition = false;
    public static final Codec<FormationBase> DISPATCH_CODEC = Codec.STRING
            .dispatch(
                    FormationBase::getType,
                    id -> {
                        return ModRegistries.FORMATION_REGISTRY
                                .get(ThirstId.registryKey(ModRegistries.FORMATION_REGISTRY_KEY, id)).codec();
                    });

    public FormationBase(BlockPos target, List<UUID> members, String state, boolean initialized) {
        this.targetLocation = target;
        this.members = members != null ? new ArrayList(members) : new ArrayList<>();
        this.state = state != null ? FormationState.valueOf(state) : FormationState.BUILDING;
        this.initialized = initialized;
    }

    public boolean areAllInPosition() {
        return allInPosition;
    }

    protected boolean positionCheck = true;

    public boolean getPositionCheck() {
        return positionCheck;
    }

    protected void spawnInitialMembers(ServerWorld world, BlockPos target, int count) {
        for (int i = 0; i < count; i++) {
            // 1. Create your Unit (replace with your actual EntityType)
            ;

            // 2. Spread them out slightly so they don't 'explode' outwards from collision
            double offsetX = (world.random.nextDouble() - 0.5) * 4.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 4.0;

            // 3. Find a valid Y (preventing spawning in the floor/ceiling)
            double spawnX = target.getX() + offsetX;
            double spawnZ = target.getZ() + offsetZ;
            double spawnY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) spawnX, (int) spawnZ);
            MinGroundUnitEntity unit = AncientThirst.MIN_GROUND_UNIT.create(world, null,
                    BlockPos.ofFloored(spawnX, spawnY, spawnZ), SpawnReason.TRIGGERED, false, false);
            unit.refreshPositionAndAngles(spawnX, spawnY, spawnZ, world.random.nextFloat() * 360F, 0);

            // 4. Critical: Assign the formation before spawning
            this.members.add(unit.getUuid());

            // 5. Actually spawn the entity into the world
            world.spawnEntity(unit);

        }
    }

    protected abstract Vec3d calculateSlot(Entity leader, int indexInGroup, int groupUnitCount, UnitType type);

    protected static ServerWorld getServerWorld(MinecraftServer server) {
        return server.getOverworld();
    }

    protected abstract void electNewLeader(MinecraftServer server);

    protected void updateUnitNumbersAndLists(ServerWorld world) {
        meleeNumber = 0;
        rangedNumber = 0;
        groundNumber = 0;
        psychedelicNumber = 0;
        melees.clear();
        grounds.clear();
        rangers.clear();
        psychedelics.clear();
        ArrayList<UUID> removeList = new ArrayList<>();
        members.forEach(memberId -> {

            Entity member = world.getEntity(memberId);
            if (member != null && member.isAlive()) {
                Unit unit = (Unit) member;
                switch (unit.getUnitType()) {
                    case MELEE:
                        melees.add(memberId);
                        meleeNumber++;
                        break;
                    case RANGED:
                        rangers.add(memberId);
                        rangedNumber++;
                        break;
                    case GROUND:
                        grounds.add(memberId);
                        groundNumber++;
                        break;
                    case PSYCHEDELIC:
                        psychedelicNumber++;
                        psychedelics.add(memberId);
                        break;
                }
            } else {
                removeList.add(memberId);
            }

        });
        members.removeAll(removeList);
    }

    public int getIdInGroup(Unit unit) {
        UUID uuid = unit.getUuid();
        switch (unit.getUnitType()) {
            case MELEE:
                return melees.indexOf(uuid);
            case RANGED:
                return rangers.indexOf(uuid);
            case GROUND:
                return grounds.indexOf(uuid);
            case PSYCHEDELIC:
                return psychedelics.indexOf(uuid);
        }
        // Will never happen
        return -2;
    }

    protected int getCountByType(UnitType type) {
        switch (type) {
            case MELEE:
                return melees.size();
            case RANGED:
                return rangers.size();
            case GROUND:
                return grounds.size();
            case PSYCHEDELIC:
                return psychedelics.size();
        }
        return -1;
    }

    protected void checkInPosition(Unit unit) {
        positionCheck &= unit.inPosition;
    }

    public void stopAttack(MinecraftServer server) {
        FormationedAttackState state = FormationedAttackState.getServerState(server);
        state.activeAttacks.remove(uuid.toString());
        state.markDirty();
    }

    public void update(MinecraftServer server) {
        ServerWorld world = getServerWorld(server);
        Entity leader = world.getEntity(leaderUuid);
        if (!initialized) {
            spawnInitialMembers(world, targetLocation, 10);
            initialized = true;
            return;
        }
        if (leader == null) {
            electNewLeader(server);
            return;
        }
        positionCheck = true;
        updateUnitNumbersAndLists(world);
        // Calculate and "beam" instructions to members
        for (int i = 0; i < members.size(); i++) {

            Entity member = world.getEntity(members.get(i));
            if (member instanceof Unit unit) {
                unit.formation = this;
                Vec3d slotPos = calculateSlot(leader, getIdInGroup(unit), getCountByType(unit.getUnitType()),
                        unit.getUnitType());
                unit.setFormationSlot(BlockPos.ofFloored(slotPos)); // The "Control Chip" instruction
                unit.formationState = state;
                unit.isInFormation = true;
                if (this.state == FormationState.BUILDING)
                    checkInPosition(unit);
            } else {
                members.remove(i);
            }
        }
        if (this.state == FormationState.BUILDING && positionCheck) {
            this.allInPosition = true;
            this.state = FormationState.ATTACKING;
        }
    }

    protected double findValidY(World world, double x, double z, double referenceY) {
        // 1. Get the highest block at this X/Z (The Surface)
        int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z);

        // 2. If the surface is too far away vertically (e.g., a cliff),
        // we want to check blocks closer to the Leader's height first.
        // This prevents units from snapping to the roof of a house when the leader is
        // inside.
        BlockPos referencePos = new BlockPos((int) x, (int) referenceY, (int) z);

        // Check if there is a floor near the reference height (within 3 blocks up or
        // down)
        for (int offset : new int[] { 0, 1, -1, 2, -2, 3, -3 }) {
            BlockPos checkPos = referencePos.up(offset);
            if (world.getBlockState(checkPos).isSolidBlock(world, checkPos) &&
                    world.isAir(checkPos.up())) {
                return checkPos.getY() + 1.0; // Return top of the solid block
            }
        }

        // 3. Fallback: If no local floor is found, use the surface heightmap
        return topY;
    }

    // Inside FormationBase or its subclasses
}