package com.thirst.common;

import com.thirst.ThirstId;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModBlockTags {
    private static TagKey<Block> create(String name) {
        return TagKey.of(RegistryKeys.BLOCK, ThirstId.id(name));
    }

    public static final TagKey<Block> WITHERABLE = create("witherable");
    public static final TagKey<Block> DEPTH_BOOST = create("depth_upgrade");

}
