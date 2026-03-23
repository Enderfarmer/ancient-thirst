package com.thirst.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;

public class SoulScorpion extends MeleeUnit {
    int stingCooldown = 0;

    public SoulScorpion(EntityType<? extends SoulScorpion> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MeleeUnit.createMobAttributes()
                .add(EntityAttributes.MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.MAX_HEALTH, 12.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 2);
    };

    @Override
    public void onAttacking(Entity target) {
        if (this.stingCooldown == 0) {
            this.triggerAnim("Attack", "Sting attack");
            this.stingCooldown = 200;
            if (target.isAlive()) {
                LivingEntity targetMob = (LivingEntity) target;
                if (targetMob.getClass().isAssignableFrom(PassiveEntity.class))
                    targetMob.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,
                            100, 2));
                else
                    targetMob.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,
                            100, 0));
            }
        } else
            this.triggerAnim("Attack", "Normal attack");
        super.onAttacking(target);
    }

    @Override
    public void tick() {
        this.stingCooldown = Math.max(0, this.stingCooldown - 1);
        super.tick();
    }

    public static Hitbox getHitboxDims() {
        return new Hitbox(0.75f, 1.5f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController());
        controllers.add(new AnimationController<>("Attack", test -> PlayState.STOP)
                .triggerableAnim("Normal attack", DefaultAnimations.ATTACK_PUNCH)
                .triggerableAnim("Sting attack",
                        RawAnimation.begin().thenPlay("attack.sting")));
    }
}
