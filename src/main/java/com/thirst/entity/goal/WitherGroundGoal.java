package com.thirst.entity.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.thirst.ModBlockTags;
import com.thirst.entity.GroundUnit;
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
        BlockPos found = findLushBlock(this.mob.getEntityWorld(), this.mob.getBlockPos().add(0, -1, 0), 10);
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

    private boolean isLush(World world, BlockPos.Mutable mutable) {
        return world.getBlockState(mutable).isIn(ModBlockTags.WITHERABLE);
    }

    private BlockPos findLushBlock(World world, BlockPos center, int range) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -2; y <= 2; y++) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);

                    if (isLush(world, mutable)) {
                        // Add a copy of the position to our list
                        candidates.add(mutable.toImmutable());
                    }
                }
            }
        }

        if (candidates.isEmpty())
            return null;

        // Pick a random block from the entire area found
        return candidates.get(world.random.nextInt(candidates.size()));
    }
}