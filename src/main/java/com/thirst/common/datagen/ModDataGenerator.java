package com.thirst.common.datagen;

import com.thirst.AncientThirst;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    static {
        AncientThirst.LOGGER.info("Data Generator class loaded!");
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        // throw new RuntimeException("I was triggered!");
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // 1. MUST BE HERE
        pack.addProvider(ModRecipeProvider::new);

        // 2. This must also be here for your custom tag to exist
        pack.addProvider(ModItemTagProvider::new);
    }
}