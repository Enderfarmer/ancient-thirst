package com.thirst;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thirst.entity.MinGroundUnitEntity;
import com.thirst.item.LoggerItem;
import com.thirst.item.NavStop;

public class AncientThirst implements ModInitializer {
	public static final String MOD_ID = "ancient-thirst";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier MIN_GROUND_UNIT_ID = Identifier.of(MOD_ID, "min_ground_unit");
	public static final RegistryKey<EntityType<?>> MIN_GROUND_UNIT_KEY = RegistryKey.of(
			RegistryKeys.ENTITY_TYPE,
			MIN_GROUND_UNIT_ID);
	public static final EntityType<MinGroundUnitEntity> MIN_GROUND_UNIT = Registry.register(
			Registries.ENTITY_TYPE,
			MIN_GROUND_UNIT_ID,
			EntityType.Builder.create(MinGroundUnitEntity::new, SpawnGroup.MONSTER)
					.dimensions(0.75f, 0.5f) // The "Hitbox" size
					.build(MIN_GROUND_UNIT_KEY));
	public static final RegistryKey<Item> MIN_GROUND_UNIT_SPAWN_EGG_KEY = RegistryKey.of(
			RegistryKeys.ITEM,
			Identifier.of(MOD_ID, "min_ground_unit_spawn_egg"));
	public static final Item MIN_GROUND_UNIT_SPAWN_EGG = new SpawnEggItem(
			new Item.Settings().spawnEgg(MIN_GROUND_UNIT).registryKey(MIN_GROUND_UNIT_SPAWN_EGG_KEY));

	@SuppressWarnings("null")
	@Override
	public void onInitialize() {
		// Register the attributes we defined in Step 1
		FabricDefaultAttributeRegistry.register(MIN_GROUND_UNIT, MinGroundUnitEntity.createAttributes());
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
			content.add(MIN_GROUND_UNIT_SPAWN_EGG);
		});
		Registry.register(Registries.ITEM, MIN_GROUND_UNIT_SPAWN_EGG_KEY, MIN_GROUND_UNIT_SPAWN_EGG);
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "logger_item"),
				new LoggerItem(new Item.Settings()
						.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "logger_item")))));
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "nav_stop"),
				new NavStop(new Item.Settings()
						.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "nav_stop")))));
	}
}