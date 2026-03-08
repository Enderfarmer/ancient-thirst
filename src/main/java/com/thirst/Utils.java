package com.thirst;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class Utils {
    public static void log(String text, PlayerEntity player) {
        if (player != null) {
            player.sendMessage(Text.literal(text), false);
        }
    }
}
