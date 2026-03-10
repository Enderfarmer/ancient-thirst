package com.thirst.entity.goal;

import java.util.EnumSet;

import com.thirst.entity.GroundUnit;
import com.thirst.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

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
        BlockPos found = findLushBlock();
        if (found != null) {
            // FIX: You MUST set both the mob's target AND this local variable
            this.positionTarget = found;
            this.mob.setPositionTarget(found, 10);
            this.isFinished = false;
            return Math.random() > 0.2; // 80% chance to start when a target is found, to allow the mob to wander around
                                        // a bit instead of immediately targeting the first block it sees
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
            Utils.log("WitherGroundGoal started. Target: " + positionTarget,
                    this.mob.getEntityWorld().getPlayers().get(0));
        }
    }

    @Override
    public void tick() {
        this.positionTarget = this.mob.getPositionTarget();
        this.cd = Math.max(0, this.cd - 1);
        if (this.positionTarget != null && cd == 0) {
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
                    this.mob.setPositionTarget(null, 10);
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
        return !this.isFinished;
    }

    private BlockPos findLushBlock() {
        // Search a 5x3x5 area for Grass
        Iterable<BlockPos> iterable = BlockPos.iterateOutwards(this.mob.getBlockPos(), 10, 2, 10);
        for (BlockPos pos : iterable) {
            if (this.lastWitheredBlockPos != null && pos.equals(this.lastWitheredBlockPos.add(0, -1, 0))) {
                continue; // Skip the last withered block to prevent immediate re-targeting
            }
            if (this.mob.getEntityWorld().getBlockState(pos).isOf(Blocks.GRASS_BLOCK)) {
                return pos.toImmutable();
            }
        }
        return null;
    }
}