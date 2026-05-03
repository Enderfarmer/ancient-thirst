package com.thirst.common;

import javax.swing.text.html.HTML.Tag;

import com.thirst.ThirstId;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModItemTags {
    public static TagKey<Item> create(String name) {
        return TagKey.of(RegistryKeys.ITEM, ThirstId.id(name));
    }

    public static final TagKey<Item> SOULWOOD_LOGS = create("soulwood_logs");

    public static void init() {
        // This method is intentionally left empty. It serves as a trigger for class
        // loading and item tag registration.
    }
}
