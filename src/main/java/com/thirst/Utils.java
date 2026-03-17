package com.thirst;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class Utils {
    public static void log(String text, PlayerEntity player) {
        if (player != null) {
            player.sendMessage(Text.literal(text), false);
        }
    }

    public static void logAList(List<String> texts, PlayerEntity player) {
        if (player != null) {
            for (String msg : texts) {
                player.sendMessage(Text.literal(msg), false);
            }
        }
    }
}
