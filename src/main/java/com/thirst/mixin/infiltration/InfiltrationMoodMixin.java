package com.thirst.mixin.infiltration;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thirst.AmbushConfig;
import com.thirst.AncientThirst;
import com.thirst.common.ModSounds;
import com.thirst.common.entity.Infectable;
import com.thirst.common.entity.InfiltrationUnit;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

@Mixin(PlayerEntity.class)
public abstract class InfiltrationMoodMixin {
    private float infectedMood = 0.0f;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickInfectedMood(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getEntityWorld().isClient())
            return;

        List<MobEntity> spies = InfiltrationUnit.getNearbyInfiltrated(player.getEntityWorld(), player.getBlockPos(),
                AmbushConfig.AMBUSH_RADIUS);

        if (!spies.isEmpty()) {
            this.infectedMood += (spies.size() * 0.1f);

            if (this.infectedMood >= 100.0f && (player.age < 6000 || player.age % 6000 == 0)) {
                triggerInfectedAmbience(player);
                this.infectedMood -= 100.0f;
            }
        } else {
            this.infectedMood = Math.max(0, this.infectedMood - 0.1f);

        }
    }

    private void triggerInfectedAmbience(PlayerEntity player) {
        player.getEntityWorld().playSound(null, player.getBlockPos(),
                ModSounds.INFILTRATION_AMBIENT, SoundCategory.AMBIENT);
    }
}
