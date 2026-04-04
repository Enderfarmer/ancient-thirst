package com.thirst.common.entity;

import com.thirst.AncientThirst;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.constant.DefaultAnimations;

public class WitherFlea extends InfiltrationUnit {
    @Override
    protected Class<? extends Entity> getTargetClass() {
        return PassiveEntity.class;
    }

    public WitherFlea(EntityType<WitherFlea> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 10, true);
    }

    public static Hitbox getHitboxDims() {
        return new Hitbox(0.2f, 0.4f);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 2.0)
                .add(EntityAttributes.FLYING_SPEED, 1.0);
    }

    @Override
    protected void initGoals() {

        super.initGoals();
        // this.goalSelector.add(3, new FlyGoal(this, 0.8D));
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericFlyController());
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanOpenDoors(false);
        birdNavigation.setCanSwim(true);
        return birdNavigation;
    }

    @Override
    public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
        return false; // Wither Fleas are immune to fall damage
    }

}
