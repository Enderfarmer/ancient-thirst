package com.thirst.mixin.infiltration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thirst.entity.Infectable;
import com.thirst.mixin.MobEntityAccessor;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(MobEntity.class)
public class IsInfiltratedMixin implements Infectable {
    @Unique // Prevents collisions with other mods
    private static final TrackedData<Boolean> INFECTED = DataTracker.registerData(MobEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> INFECTED_GOALS = DataTracker.registerData(MobEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> AMBUSHING = DataTracker.registerData(MobEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public void setInfected(boolean value) {
        // Explicitly cast 'this' to MobEntity to hit the actual DataTracker
        ((MobEntity) (Object) this).getDataTracker().set(INFECTED, value);
    }

    public boolean isInfected() {
        // Explicitly cast 'this' to MobEntity to read the actual DataTracker
        return ((MobEntity) (Object) this).getDataTracker().get(INFECTED);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initInfectionTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(INFECTED, false);
        builder.add(INFECTED_GOALS, false);
        builder.add(AMBUSHING, false);
    }

    public void setInfectedGoals() {
        if (!hasInfectedGoals()) {
            ((MobEntity) (Object) this).getDataTracker().set(INFECTED_GOALS, true);
            MobEntity self = (MobEntity) (Object) this;
            MobEntityAccessor accessor = (MobEntityAccessor) (Object) this;
            GoalSelector selector = accessor.getGoalSelector();
            selector.clear(goal -> true); // Clear existing goals
            selector.add(1, new LookAtEntityGoal(self, PlayerEntity.class, 10));
        }
    }

    @Override
    public boolean hasInfectedGoals() {
        return ((MobEntity) (Object) this).getDataTracker().get(INFECTED_GOALS);
    }

    @Override
    public boolean isAmbushing() {
        return ((MobEntity) (Object) this).getDataTracker().get(AMBUSHING);
    }

    public void ambush() {
        ((MobEntity) (Object) this).getDataTracker().set(AMBUSHING, true);
    }
}
