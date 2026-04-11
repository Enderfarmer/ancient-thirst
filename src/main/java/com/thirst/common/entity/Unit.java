package com.thirst.common.entity;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.thirst.AncientThirst;
import com.thirst.common.ModEntities;
import com.thirst.mass.MassState;
import com.thirst.systems.documentation.gathering.Events;
import com.thirst.systems.formation.FormationState;
import com.thirst.systems.formation.types.FormationBase;
import com.thirst.systems.mutations.MutationState;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Unit extends PathAwareEntity implements GeoEntity {
    protected final @NonNull AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public double distSq;
    public boolean isInFormation;
    protected BlockPos formationSlot;
    public FormationState formationState;
    public boolean inPosition;
    public FormationBase formation;
    float lastDamage = 0;

    public UnitType getUnitType() {
        return null;
    }

    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean setFormationSlot(BlockPos slot) {
        if (slot != null) {
            this.formationSlot = slot;
            return true;
        }
        return false;
    }

    public BlockPos getFormationSlot() {
        return this.formationSlot;
    }

    protected Unit(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @SuppressWarnings("null")
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        throw new NotImplementedException("U have to define da animation controllers in ur child class!");
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        throw new NotImplementedException("U have to define da attributes in ur child class!");
    };

    public static Hitbox getHitboxDims() {
        throw new NotImplementedException("U have to define da hitbox dimensions in ur child class!");
    };

    @Override
    public void tick() {
        if (!this.getEntityWorld().isClient())
            MutationState.getServerState(this.getEntityWorld().getServer()).processUnit(this);
        this.inPosition = this.isInFormation && this.inPosition;
        super.tick();
    }

    @Override
    public boolean onKilledOther(ServerWorld world, LivingEntity other, DamageSource damageSource) {
        MassState.getServerState(world.getServer()).onKill(other);
        MutationState.getServerState(world.getServer()).onKill(other.getType());
        // if (this.isInFormation) {
        world.spawnEntity(ModEntities.SOUL_SCORPION.create(world, null, other.getBlockPos().add(0, -1, 0),
                SpawnReason.CONVERSION, true, false));
        other.discard();
        return false;
        // }
        // return true;
    }

    @Override
    public void applyDamage(ServerWorld world, DamageSource source, float amount) {
        if (source.isOf(DamageTypes.WITHER)) {
            this.heal(amount);

            return;
        }
        this.lastDamage = amount;
        super.applyDamage(world, source, amount);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        if (source.isOf(DamageTypes.WITHER)) {
            return SoundEvents.BLOCK_AMETHYST_BLOCK_STEP;
        }
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    public void onDamaged(DamageSource damageSource) {
        if (damageSource.isOf(DamageTypes.WITHER)) {

            this.getEntityWorld().addParticleClient(ParticleTypes.SOUL, this.getX(), this.getEyeY(), this.getZ(), 0,
                    0.1, 0);
            return;
        }
        super.onDamaged(damageSource);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (damageSource.getAttacker() != null) {
            AncientThirst.LOGGER.info("Last damage taken: " + lastDamage);
            Events.onDeath(this, damageSource, lastDamage);
        }
        super.onDeath(damageSource);
    }
}
