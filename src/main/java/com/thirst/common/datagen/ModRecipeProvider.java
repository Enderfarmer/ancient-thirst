package com.thirst.common.datagen;

import java.util.concurrent.CompletableFuture;

import com.thirst.AncientThirst;
import com.thirst.common.ModBlocks;
import com.thirst.common.ModItemTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryEntryLookup.RegistryLookup;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    public String getName() {
        return "Ancient Thirst Recipes";
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(WrapperLookup registryLookup, RecipeExporter exporter) {
        return new Generator(registryLookup, exporter);
    }

    public class Generator extends RecipeGenerator {
        public Generator(WrapperLookup registryLookup, RecipeExporter exporter) {
            super(registryLookup, exporter);
        }

        @Override
        public void generate() {
            offerPlanksRecipe(ModBlocks.SOULWOOD_PLANKS, ModItemTags.SOULWOOD_LOGS, 4);
            AncientThirst.LOGGER.info("Generated recipes for Ancient Thirst");
        }
    }
}