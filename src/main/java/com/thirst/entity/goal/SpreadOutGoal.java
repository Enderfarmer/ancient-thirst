package com.thirst.entity.goal;

import java.util.EnumSet;
import java.util.List;

import com.thirst.entity.Unit;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class SpreadOutGoal extends Goal {
    private final Unit mob;
    private Class<? extends LivingEntity> classToAvoid;
    private int minDistance;

    public SpreadOutGoal(Unit mob, Class<? extends LivingEntity> classToAvoid, int minDistance) {
        this.mob = mob;
        this.classToAvoid = classToAvoid;
        this.minDistance = minDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public List<? extends LivingEntity> getNearbyEntities() {
        return mob.getEntityWorld().getEntitiesByClass(classToAvoid, mob.getBoundingBox().expand(minDistance),
                e -> true);
    }

    public boolean check() {
        // Check if there's an entity of the specified class within the minimum distance
        return getNearbyEntities().size() > 1; // >1 because the mob itself will be included in the list
    }

    @Override
    public boolean canStart() {
        // Check if there's an entity of the specified class within the minimum distance
        return check();
    }

    @Override
    public void tick() {
        if (check() && this.mob.getNavigation().isIdle()) {

            // Find the closest neighbor (excluding self)
            LivingEntity closest = getNearbyEntities().stream()
                    .filter(e -> e != mob) // CRITICAL: Don't avoid yourself!
                    .min((e1, e2) -> Double.compare(e1.squaredDistanceTo(mob), e2.squaredDistanceTo(mob)))
                    .get();

            if (closest != null) {
                double dx = mob.getX() - closest.getX();
                double dz = mob.getZ() - closest.getZ();

                // If they are exactly on top of each other, dx and dz are 0.
                // We need to pick a random direction so they don't stay stuck.
                if (dx == 0 && dz == 0) {
                    dx = mob.getRandom().nextGaussian();
                    dz = mob.getRandom().nextGaussian();
                }

                // Normalize and multiply by a "Push Factor" (e.g., 3-5 blocks)
                double distance = Math.sqrt(dx * dx + dz * dz);
                int pushDist = 4;
                if (dx == 0 && dz == 0) {
                    // If they are exactly on top of each other, just pick a random direction
                    dx = mob.getRandom().nextGaussian();
                    dz = mob.getRandom().nextGaussian();
                    distance = Math.sqrt(dx * dx + dz * dz);
                }
                double targetX = mob.getX() + (dx / distance) * pushDist;
                double targetZ = mob.getZ() + (dz / distance) * pushDist;
                double targetY = mob.getY();

                this.mob.getNavigation().startMovingTo(targetX, targetY, targetZ, 1.2D);
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        return check();
    }

}