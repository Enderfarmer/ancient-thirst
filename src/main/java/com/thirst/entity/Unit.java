package com.thirst.entity;

import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.NonNull;

import com.thirst.AncientThirst;
import com.thirst.mass.MassState;
import com.thirst.systems.formation.FormationState;
import com.thirst.systems.formation.types.FormationBase;
import com.thirst.systems.upgrades.UpgradeState;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
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
            UpgradeState.getServerState(this.getEntityWorld().getServer()).processUnit(this);
        this.inPosition = this.isInFormation && this.inPosition;
        super.tick();
    }

    @Override
    public boolean onKilledOther(ServerWorld world, LivingEntity other, DamageSource damageSource) {
        MassState.getServerState(world.getServer()).onKill(other);
        UpgradeState.getServerState(world.getServer()).onKill(other.getType());
        // if (this.isInFormation) {
        world.spawnEntity(AncientThirst.SOUL_SCORPION.create(world, null, other.getBlockPos().add(0, -1, 0),
                SpawnReason.CONVERSION, true, false));
        other.discard();
        return false;
        // }
        // return true;
    }
}
