package com.thirst.common;

import com.thirst.ThirstId;
import com.thirst.common.block.Bonestem;
import com.thirst.common.block.CorruptedSoil;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {
    public static Block register(String name, Block block) {
        Registry.register(Registries.BLOCK, ThirstId.id(name), block);
        ModItems.registerBlockItem(name, block);
        return block;
    }

    public static Block registerWoodenBlock(String name) {
        return register(name,
                new PillarBlock(createSettings(name).mapColor(MapColor.CYAN).sounds(BlockSoundGroup.NETHER_WOOD)
                        .strength(2.0f)));
    }

    public static Block.Settings createSettings(String name) {
        return Block.Settings.create().registryKey(ThirstId.registryKey(RegistryKeys.BLOCK, name));
    }

    public static final Block CORRUPTED_SOIL = register("corrupted_soil",
            new CorruptedSoil(createSettings("corrupted_soil").strength(.5f).sounds(BlockSoundGroup.SOUL_SAND)));
    public static final Block SOULWOOD_LOG = registerWoodenBlock("soulwood_log");
    public static final Block SOULWOOD_PLANKS = registerWoodenBlock("soulwood_planks");
    public static final Block STRIPPED_SOULWOOD_LOG = registerWoodenBlock("stripped_soulwood_log");
    public static final Block SOULWOOD = registerWoodenBlock("soulwood");
    public static final Block STRIPPED_SOULWOOD = registerWoodenBlock("stripped_soulwood");
    public static final Block LIVING_SOULWOOD = register("living_soulwood",
            new Block(createSettings("living_soulwood").mapColor(MapColor.CYAN).sounds(BlockSoundGroup.NETHER_WOOD)
                    .strength(2.0f)));
    public static final Block BONESTEM = register("bonestem",
            new Bonestem(createSettings("bonestem").noCollision().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.BONE)
                    .breakInstantly().strength(0).pistonBehavior(PistonBehavior.DESTROY)));

    public static void init() {
        // This method is intentionally left empty. It serves as a trigger for class
        // loading and block registration.
        StrippableBlockRegistry.register(SOULWOOD_LOG, STRIPPED_SOULWOOD_LOG);
        StrippableBlockRegistry.register(SOULWOOD, STRIPPED_SOULWOOD);
    }
}
