package com.thirst.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import java.util.concurrent.CompletableFuture;

import com.thirst.common.ModBlocks;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    public TagEntry toEntry(Item item) {
        return TagEntry.create(Registries.ITEM.getId(item));
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        // Add items directly to your custom tag
        getTagBuilder(ItemTags.LOGS)
                .add(toEntry(ModBlocks.SOULWOOD_LOG.asItem()))
                .add(toEntry(ModBlocks.LIVING_SOULWOOD.asItem()))
                .add(toEntry(ModBlocks.STRIPPED_SOULWOOD_LOG.asItem()))
                .add(toEntry(ModBlocks.SOULWOOD.asItem()))
                .add(toEntry(ModBlocks.STRIPPED_SOULWOOD.asItem()));
        getTagBuilder(ItemTags.PLANKS)
                .add(toEntry(ModBlocks.SOULWOOD_PLANKS.asItem()));
    }
}
