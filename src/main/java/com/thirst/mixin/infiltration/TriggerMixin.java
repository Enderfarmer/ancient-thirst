package com.thirst.mixin.infiltration;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thirst.entity.Infectable;
import com.thirst.entity.InfiltrationUnit;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import com.thirst.AmbushConfig;
import com.thirst.AncientThirst;
import com.thirst.FactoredDecision;
import com.thirst.FactoredDecision.Factor;

@Mixin(PlayerEntity.class)
public class TriggerMixin {
    private int lastAmbushTime = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickTrigger(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getEntityWorld().isClient() || !player.getGameMode().isSurvivalLike() || player.age % 20 != 0)
            return;
        if ((lastAmbushTime == 0 || player.age - lastAmbushTime > AmbushConfig.AMBUSH_INTERVAL)
                && AmbushConfig.DECISION.evaluate(player)) {
            InfiltrationUnit.triggerAmbush(player);
            lastAmbushTime = player.age;
        }
    }
}
