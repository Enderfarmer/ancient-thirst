package com.thirst.entity.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.thirst.ModBlockTags;
import com.thirst.Utils;
import com.thirst.entity.GroundUnit;
import com.thirst.systems.formation.FormationState;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WitherGroundGoal extends Goal {
    private final GroundUnit mob;
    private int timer = 0;
    private int maxCd = 20;
    private boolean isFinished = false;
    // CRASH RISK: Ensure this is ONLY updated via the mob's data
    private BlockPos positionTarget = null;
    private BlockPos lastWitheredBlockPos = null;
    private int cd = 0; // Cooldown to prevent spamming the same block if something goes wrong

    public WitherGroundGoal(GroundUnit mob, int maxCd) {
        this.mob = mob;
        this.maxCd = maxCd;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.mob.formationState == FormationState.BUILDING) {
            return false;
        }
        BlockPos found = findLushBlock(this.mob.getEntityWorld(), this.mob.getBlockPos().add(0, -1, 0), 1);
        if (found != null) {
            // FIX: You MUST set both the mob's target AND this local variable
            this.positionTarget = found;
            this.mob.setPositionTarget(found, 10);
            this.isFinished = false;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.timer = 0;
        this.cd = this.maxCd;
        // CRASH FIX: If start() is called but positionTarget is somehow null, abort.
        if (this.positionTarget != null) {
            this.mob.getNavigation().stop();
            this.mob.getNavigation().startMovingTo(
                    positionTarget.getX(), positionTarget.getY(), positionTarget.getZ(), 1.0D);
        }
    }

    @Override
    public void tick() {

        this.cd = Math.max(0, this.cd - 1);
        if (this.positionTarget != null && cd == 0) {
            this.positionTarget = this.mob.getPositionTarget();
            // Use a distance check (1.5 - 2.0 blocks) instead of .equals()
            double distSq = this.mob.getBlockPos().add(0, -1, 0).getSquaredDistance(this.positionTarget);
            if (distSq <= 1) { // We have arrived
                this.mob.getNavigation().stop();
                this.mob.getLookControl().lookAt(positionTarget.getX() + 0.5, positionTarget.getY(),
                        positionTarget.getZ() + 0.5);

                this.mob.setSiphoning(true);
                this.timer++;

                if (this.timer >= 20) {
                    // Wither the block
                    this.mob.setSiphoning(false);
                    this.mob.witherGround();
                    this.mob.clearPositionTarget();
                    this.lastWitheredBlockPos = positionTarget;
                    this.positionTarget = null;
                    this.timer = 0;

                    this.mob.getNavigation().stop();
                    this.isFinished = true;
                }
            } else {
                // WE ARE NOT THERE YET - KEEP WALKING!
                if (this.mob.getEntityWorld().getBlockState(this.positionTarget).isOf(Blocks.SOUL_SOIL)) {
                    this.isFinished = true;
                }
                ;
                if (this.mob.getNavigation().isIdle()) {
                    this.mob.getNavigation().startMovingTo(
                            positionTarget.getX(), positionTarget.getY(), positionTarget.getZ(), 1.0D);
                }
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.positionTarget == null || !isLush(this.mob.getEntityWorld(), this.positionTarget.mutableCopy()))
            return false;
        ;
        return !this.isFinished;
    }

    private boolean isLush(World world, BlockPos.Mutable mutable) {
        return world.getBlockState(mutable).isIn(ModBlockTags.WITHERABLE);
    }

    private BlockPos findLushBlock(World world, BlockPos center, int range) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                // 1. Calculate the ACTUAL world coordinates
                double targetX = center.getX() + x;
                double targetZ = center.getZ() + z;

                // 2. Find the floor at that SPECIFIC world coordinate
                double targetY = Utils.findValidY(world, targetX, targetZ, center.getY()) - 1;
                mutable.set(targetX, targetY, targetZ);
                // 3. IMPORTANT: Check reachability only after the Y is found
                if (!Utils.isPosReachable(this.mob, mutable.toImmutable())) {
                    continue;
                }

                if (isLush(world, mutable)) {
                    candidates.add(mutable.toImmutable());
                }
            }
        }

        if (candidates.isEmpty())
            return null;
        // Pick a random block from the entire area found
        return candidates.get(world.random.nextInt(candidates.size()));
    }
}