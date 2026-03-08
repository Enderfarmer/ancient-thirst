package com.thirst.entity;

import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.NonNull;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Unit extends PathAwareEntity implements GeoEntity {
    protected final @NonNull AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public Unit(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @SuppressWarnings("null")
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        throw new NotImplementedException("U have to define da animation controllers in ur child class!");
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        throw new NotImplementedException("U have to define da attributes in ur child class!");
    };
}
