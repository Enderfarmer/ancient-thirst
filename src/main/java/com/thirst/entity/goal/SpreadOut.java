package com.thirst.entity.goal;

import java.util.EnumSet;
import java.util.List;

import com.thirst.entity.Unit;
import com.thirst.Utils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class SpreadOut extends Goal {
    private final Unit mob;
    private boolean finished = false;
    private Class<? extends Unit> classToAvoid;
    private int minDistance;

    public SpreadOut(Unit mob, Class<? extends Unit> classToAvoid, int minDistance) {
        this.mob = mob;
        this.classToAvoid = classToAvoid;
        this.minDistance = minDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public List<? extends Unit> getNearbyEntities() {
        return mob.getEntityWorld().getEntitiesByClass(classToAvoid, mob.getBoundingBox().expand(minDistance),
                e -> true);
    }

    public boolean check() {
        // Check if there's an entity of the specified class within the minimum distance
        return getNearbyEntities().size() > 0;
    }

    @Override
    public boolean canStart() {
        // Check if there's an entity of the specified class within the minimum distance
        return check();
    }

    @Override
    public void start() {
        Utils.log(
                "SpreadOut goal started. Found " + getNearbyEntities().size() + " entities of class "
                        + classToAvoid.getSimpleName() + " within distance " + minDistance,
                mob.getEntityWorld().getPlayers().get(0));
    }

    @Override
    public void tick() {
        if (check()) {
            // If there is, find a random position away from it and move there
            BlockPos currentPos = mob.getBlockPos().add(0, -1, 0);
            BlockPos targetPos;
            do {
                int xOffset = (int) (Math.random() * 20) - 10; // Random offset between -10 and 10
                int zOffset = (int) (Math.random() * 20) - 10; // Random offset between -10 and 10
                targetPos = currentPos.add(xOffset, 0, zOffset);
            } while (targetPos.isWithinDistance(currentPos, minDistance)); // Ensure the new position is far enough away
            mob.getNavigation().stop();
            mob.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1.0D);
        } else {
            // If not, mark the goal as finished
            this.finished = true;
        }
    }

    @Override
    public boolean shouldContinue() {
        return !this.finished;
    }

}