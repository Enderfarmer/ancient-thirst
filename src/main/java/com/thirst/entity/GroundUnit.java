package com.thirst.entity;

import org.apache.commons.lang3.NotImplementedException;

import com.thirst.AnimationControllers;
import com.thirst.Utils;
import com.thirst.entity.goal.FormationGoal;
import com.thirst.entity.goal.SpreadOutGoal;
import com.thirst.entity.goal.WitherGroundGoal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
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
import software.bernie.geckolib.animation.state.AnimationTest;

public abstract class GroundUnit extends Unit {
    protected GroundUnit(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    public UnitType getUnitType() {
        return UnitType.GROUND;
    }

    public static int cooldown;

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    };

    public void witherGround() {
        throw new NotImplementedException("U have to define da withering behavior in ur child class!");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(AnimationControllers.WalkFuncIdle("wither", new Predicate<AnimationTest<Unit>>() {
            @Override
            public boolean test(AnimationTest<Unit> state) {
                return isSiphoning();
            }
        }));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, net.minecraft.util.Hand hand) {
        // For testing: Right-click to trigger the siphoning animation
        if (!this.getEntityWorld().isClient() && hand == net.minecraft.util.Hand.MAIN_HAND) {
            List<String> logList = List.of("My pos: " + this.getBlockPos(),
                    "My formation target: " + this.formation.getTargetLocation(),
                    "My formation slot: " + this.formationSlot, "My id in group: " + this.formation.getIdInGroup(this),
                    "I am in position: " + this.inPosition, "Dist sq to my slot: " + this.distSq,
                    "Formation state: " + this.formation.getState());
            Utils.logAList(logList, player);
        }
        return ActionResult.SUCCESS;
    }

    protected void initGoals() {
        // Priority 0: High-level survival (Don't drown, look at things)
        this.goalSelector.add(0, new SwimGoal(this));
        // Priority 1: Participate in formations
        this.goalSelector.add(1, new FormationGoal(this));
        // Priority 2: Spread out if too close to others of the same class
        this.goalSelector.add(2, new SpreadOutGoal(this, this.getClass(), 1));
        // Priority 2: Flee from players if they get too close (still giving them a
        // chance to kill the mob)
        this.goalSelector.add(2, new SpreadOutGoal(this, PlayerEntity.class, 2));
        // Priority 3: The Purpose: Withering the ground beneath them
        this.goalSelector.add(3, new WitherGroundGoal(this, cooldown));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.8D));
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