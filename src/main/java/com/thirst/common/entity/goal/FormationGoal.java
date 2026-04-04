package com.thirst.common.entity.goal;

import java.util.EnumSet;

import com.thirst.AncientThirst;
import com.thirst.common.entity.Unit;
import com.thirst.common.entity.UnitType;
import com.thirst.systems.formation.FormationState;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class FormationGoal extends Goal {
    protected Unit mob;

    public FormationGoal(Unit mob) {
        // We still need MOVE control so we can navigate
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (!this.mob.isInFormation || this.mob.getFormationSlot() == null) {
            return false;
        }

        double sqDist = this.mob.getBlockPos().getSquaredDistance(this.mob.getFormationSlot());
        this.mob.distSq = sqDist;
        // BUILDING: Always try to get to the spot
        if (this.mob.formationState == FormationState.BUILDING) {
            return true;
        }
        if (this.mob.getUnitType() == UnitType.GROUND)
            return false;

        // ATTACKING: Only take control if they drift more than 6 blocks away
        // This is the "Release" point. If dist < 6, this goal returns FALSE,
        // letting Priority 2 (MeleeAttackGoal) take over!
        return sqDist >= 6.0;
    }

    @Override
    public boolean shouldContinue() {
        // Continue until they are within the "comfortable" zone
        double stopDistance = (this.mob.formationState == FormationState.BUILDING) ? 1.5 : 6.0;
        return canStart() && this.mob.getBlockPos().getSquaredDistance(this.mob.getFormationSlot()) > stopDistance;
    }

    @Override
    public void tick() {
        BlockPos slot = this.mob.getFormationSlot();
        if (slot != null) {
            this.mob.getNavigation().startMovingTo(slot.getX(), slot.getY(), slot.getZ(), 1.2D);

            // If we are close enough in BUILDING state, toggle the flag for the
            // FormationBase
            if (this.mob.formationState == FormationState.BUILDING &&
                    this.mob.getBlockPos().getSquaredDistance(slot) < 1.5) {
                this.mob.inPosition = true;
            }
        }
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }
}