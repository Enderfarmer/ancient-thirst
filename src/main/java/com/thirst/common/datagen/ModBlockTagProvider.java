package com.thirst.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import java.util.concurrent.CompletableFuture;

import com.thirst.common.ModBlockTags;
import com.thirst.common.ModBlocks;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    public TagEntry toEntry(Item item) {
        return TagEntry.create(Registries.ITEM.getId(item));
    }

    public Identifier toEntry(TagKey<Block> tag) {
        return tag.id();
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        // Add items directly to your custom tag
        getTagBuilder(ModBlockTags.WITHERABLE)
                .addOptionalTag(toEntry(BlockTags.LOGS))
                .addOptionalTag(toEntry(BlockTags.BASE_STONE_OVERWORLD))
                .addOptionalTag(toEntry(BlockTags.BAMBOO_BLOCKS))
                .addOptionalTag(toEntry(BlockTags.MOSS_REPLACEABLE))
                .addOptionalTag(toEntry(BlockTags.WOODEN_TRAPDOORS))
                .addOptionalTag(toEntry(BlockTags.WOODEN_FENCES))
                .addOptionalTag(toEntry(BlockTags.WOODEN_SHELVES))
                .addOptionalTag(toEntry(BlockTags.WOODEN_SLABS))
                .addOptionalTag(toEntry(BlockTags.PLANKS));
    }
}
