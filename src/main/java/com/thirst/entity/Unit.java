package com.thirst.entity;

import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.NonNull;

import com.thirst.systems.formation.FormationState;
import com.thirst.systems.formation.types.FormationBase;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.PathAwareEntity;
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
        this.inPosition = this.isInFormation && this.inPosition;
        super.tick();
    }
}
