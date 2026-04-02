package com.thirst.mixin.infiltration;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.thirst.AmbushConfig;
import com.thirst.AncientThirst;
import com.thirst.entity.Infectable;
import com.thirst.entity.InfiltrationUnit;
import com.thirst.mixin.MobEntityAccessor;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

@Mixin(MobEntity.class)
public class InfiltratedMobsMixin {
    @Inject(method = "playAmbientSound", at = @At("HEAD"), cancellable = true)
    private void silenceInfected(CallbackInfo ci) {
        if (((Infectable) this).isInfected()) {
            // Stop the sound before it even starts
            ci.cancel();
        }
    }

    @Inject(method = "createMobAttributes", at = @At("TAIL"), cancellable = true)
    private static void addInfectedAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> ci) {
        // Add attributes to all mobs, but only use them if infected
        ((DefaultAttributeContainer.Builder) ci.getReturnValue()).add(EntityAttributes.ATTACK_DAMAGE, 2.0);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void addInfectedGoals(CallbackInfo ci) {
        if (((Infectable) this).isInfected()) {
            MobEntity self = (MobEntity) (Object) this;
            if (self.age % 20 == 0)
                ((Infectable) this).setInfectedGoals();
            if ((self.age + self.getId()) % 20 == 0 && self.getTarget() != null && ((Infectable) this).isAmbushing()) {
                List<MobEntity> nearbyInfected = InfiltrationUnit.getNearbyInfiltrated(self.getEntityWorld(),
                        self.getBlockPos(), AmbushConfig.AMBUSH_CALL_RADIUS / 4);

                for (MobEntity infected : nearbyInfected) {
                    if (infected != self && infected.getTarget() == null) {
                        InfiltrationUnit.makeAggressive(infected,
                                (PlayerEntity) self.getTarget());
                    }
                }
            }
        }
    }
}
