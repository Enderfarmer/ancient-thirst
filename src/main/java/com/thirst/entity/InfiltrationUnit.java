package com.thirst.entity;

import java.util.List;

import com.thirst.AmbushConfig;
import com.thirst.AncientThirst;
import com.thirst.common.ModSounds;
import com.thirst.entity.goal.FormationGoal;
import com.thirst.mixin.MobEntityAccessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.constant.DefaultAnimations;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.FlyGoal;

public abstract class InfiltrationUnit extends Unit {
    protected BlockPos spawnPos = null;

    protected abstract Class<? extends Entity> getTargetClass();

    public void setSpawnPos(BlockPos spawnPos) {
        if (this.spawnPos == null)
            this.spawnPos = spawnPos;
    }

    public InfiltrationUnit(
            EntityType<? extends InfiltrationUnit> type,
            World world) {
        super(type, world);

    }

    public void infect(Entity mob) {
        if (mob instanceof Infectable infectable) {
            infectable.setInfected(true);
            this.getEntityWorld().playSound(this, this.getBlockPos(), ModSounds.INFILTRATION_INFECT,
                    SoundCategory.HOSTILE);
        }
    };

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        this.infect(target);
        this.discard();
        return false;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(2, new ActiveTargetGoal(this, getTargetClass(), true, true));

    }

    public static void makeAggressive(Entity mob, PlayerEntity target) {
        if (mob instanceof MobEntity mobEntity) {
            var accessor = (MobEntityAccessor) mobEntity;
            accessor.getGoalSelector().getGoals().clear(); // Absolute reset

            accessor.getGoalSelector().add(1, new AttackGoal(mobEntity));
            accessor.getGoalSelector().add(2, new ActiveTargetGoal<>(mobEntity, PlayerEntity.class, false));
            mobEntity.setTarget(target);
            EntityAttributeInstance attackDamage = mobEntity.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
            if (attackDamage == null) {
                return;
            }
            if (attackDamage.getBaseValue() < 2) {
                attackDamage.setBaseValue(2);
            }
        }
    }

    public static void triggerAmbush(PlayerEntity player) {
        triggerAmbush(player, player.getEntityWorld(), player.getBlockPos());
    }

    public static void triggerAmbush(PlayerEntity player, World world, BlockPos center) {
        AncientThirst.LOGGER.info("Triggering ambush for player {}", player.getName().getString());
        Box ambushZone = new Box(center).expand(AmbushConfig.AMBUSH_CALL_RADIUS);
        List<MobEntity> nearbyInfected = world.getEntitiesByClass(MobEntity.class, ambushZone,
                mob -> mob instanceof Infectable i && i.isInfected());

        for (MobEntity infected : nearbyInfected) {
            makeAggressive(infected, player);
            ((Infectable) infected).ambush();

        }
        world.playSound(null, player.getBlockPos(), ModSounds.INFILTRATION_AMBUSH, SoundCategory.HOSTILE);
    }

    public static List<MobEntity> getNearbyInfiltrated(World world, BlockPos center, double radius) {
        Box searchZone = new Box(center).expand(radius);
        return world.getEntitiesByClass(MobEntity.class, searchZone,
                mob -> mob instanceof Infectable i && i.isInfected());
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25);
    };

}
