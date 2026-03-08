package com.thirst.entity;

import org.apache.commons.lang3.NotImplementedException;

import com.thirst.AnimationControllers;
import com.thirst.Utils;
import com.thirst.entity.goal.SpreadOut;
import com.thirst.entity.goal.WitherGroundGoal;

import net.minecraft.entity.EntityType;
// import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;

public abstract class GroundUnit extends Unit {
    public GroundUnit(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    };

    public void witherGround() {
        // throw new NotImplementedException("U have to define da withering behavior in
        // ur child class!");
        throw new NotImplementedException("U have to define da withering behavior in ur child class!");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(AnimationControllers.WalkFuncIdle("wither"));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, net.minecraft.util.Hand hand) {
        // For testing: Right-click to trigger the siphoning animation
        if (!this.getEntityWorld().isClient() && hand == net.minecraft.util.Hand.MAIN_HAND) {
            Utils.log("Is withering: " + this.isSiphoning(), player);
            Utils.log("My pos: " + this.getBlockPos().add(0, -1, 0), player);
            Utils.log("My target pos: " + this.getPositionTarget(), player);
            Utils.log("My nav target pos: " + this.getNavigation().getTargetPos(), player);
            Utils.log("My nav idle: " + this.getNavigation().isIdle(), player);
            Utils.log("My path: " + this.getNavigation().getCurrentPath(), player);
            Utils.log("Target block type: " + this.getEntityWorld().getBlockState(this.getPositionTarget()).getBlock(),
                    player);
        }
        return ActionResult.SUCCESS;
    }

    protected void initGoals() {
        // Priority 0: High-level survival (Don't drown, look at things)
        this.goalSelector.add(0, new SwimGoal(this));
        // Priority 1: Spread out if too close to others of the same class
        this.goalSelector.add(1, new SpreadOut(this, this.getClass(), 5));
        // Priority 2: The Purpose: Withering the ground beneath them
        this.goalSelector.add(2, new WitherGroundGoal(this));
    }

    // 1. Define the "Radio Channel"
    private static final TrackedData<Boolean> SIPHONING = DataTracker.registerData(MinGroundUnitEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    // private static final TrackedData<Boolean> STOP_SIPHONING =
    // DataTracker.registerData(MinGroundUnitEntity.class,
    // TrackedDataHandlerRegistry.BOOLEAN);

    // 2. Register it in the DataTracker
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SIPHONING, false);
        // builder.add(STOP_SIPHONING, false);
    }

    // 3. Helper methods to flip the switch
    public void setSiphoning(boolean siphoning) {
        this.dataTracker.set(SIPHONING, siphoning);
    }

    // public void setStopSiphoning(boolean stop) {
    // // this.dataTracker.set(STOP_SIPHONING, stop);
    // }

    public boolean isSiphoning() {
        return this.dataTracker.get(SIPHONING);
    }

    // public boolean isStopSiphoning() {
    // return this.dataTracker.get(STOP_SIPHONING);
    // }

    public BlockPos getTargetPos() {
        return this.getPositionTarget();
    }

}