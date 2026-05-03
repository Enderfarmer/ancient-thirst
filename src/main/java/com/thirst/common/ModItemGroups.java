package com.thirst.common;

import com.thirst.ThirstId;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class ModItemGroups {
    public static final RegistryKey<ItemGroup> THIRST_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP,
            ThirstId.id("thirst"));

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, THIRST_GROUP, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.ancient-thirst.thirst"))
                .icon(() -> new ItemStack(ModItems.CREATE_FORMATION_ITEM))
                .build());

        // Add items to the custom tab

    }
}