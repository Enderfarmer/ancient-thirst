package com.thirst.common;

import com.thirst.ThirstId;
import com.thirst.common.item.CleanUpItem;
import com.thirst.common.item.CreateFormationItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModItems {
    private static Item register(String name, ItemConvertible item) {
        return Registry.register(Registries.ITEM, ThirstId.id(name),
                item.asItem());
    }

    public static final Item CREATE_FORMATION_ITEM = register("create_formation", new CreateFormationItem(
            new Item.Settings().maxCount(1).registryKey(ThirstId.registryKey(RegistryKeys.ITEM, "create_formation"))));
    public static final Item CLEAN_UP_ITEM = register("clean_up", new CleanUpItem(
            new Item.Settings().maxCount(1).registryKey(ThirstId.registryKey(RegistryKeys.ITEM, "clean_up"))));

    public static void init() {
        // This method is intentionally left blank. Its purpose is to ensure that the
        // class is loaded and static initializers are run.
    }
}
