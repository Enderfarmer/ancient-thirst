package com.thirst;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModBlockTags {
    // This creates a tag reference for "ancient_thirst:witherable"
    public static final TagKey<Block> WITHERABLE = TagKey.of(
            RegistryKeys.BLOCK,
            ThirstId.id("witherable"));

}
