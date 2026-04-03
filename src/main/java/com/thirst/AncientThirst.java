package com.thirst;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.PathAwareEntity;
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

import com.thirst.common.ModEffects;
import com.thirst.common.ModSounds;
import com.thirst.entity.Hitbox;
import com.thirst.entity.MinGroundUnitEntity;
import com.thirst.entity.SoulScorpion;
import com.thirst.entity.WitherFlea;
import com.thirst.item.CreateFormationItem;
import com.thirst.item.CleanUpItem;
import com.thirst.systems.formation.FormationedAttackState;

public class AncientThirst implements ModInitializer {

	public static <T extends PathAwareEntity> EntityType<T> registerEntityType(String name,
			EntityType.EntityFactory<T> factory, Hitbox hitbox) {
		Identifier id = ThirstId.id(name);
		RegistryKey<EntityType<?>> key = ThirstId.registryKey(RegistryKeys.ENTITY_TYPE, name);
		EntityType<T> entityType = EntityType.Builder.create(factory, SpawnGroup.CREATURE)
				.dimensions(hitbox.width, hitbox.height) // The "Hitbox" size
				.build(key);
		return Registry.register(Registries.ENTITY_TYPE, id,
				entityType);
	}

	public static SpawnEggItem registerSpawnEgg(EntityType<? extends PathAwareEntity> entityType, String name) {
		RegistryKey<Item> registryKey = ThirstId.registryKey(RegistryKeys.ITEM, name + "_spawn_egg");
		SpawnEggItem spawnEgg = new SpawnEggItem(new Item.Settings().spawnEgg(entityType).registryKey(registryKey));
		return Registry.register(Registries.ITEM, registryKey, spawnEgg);
	}

	public static final String MOD_ID = "ancient-thirst";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static EntityType<MinGroundUnitEntity> MIN_GROUND_UNIT = registerEntityType("min_ground_unit",
			MinGroundUnitEntity::new,
			MinGroundUnitEntity.getHitboxDims());
	public static EntityType<SoulScorpion> SOUL_SCORPION = registerEntityType("soul_scorpion", SoulScorpion::new,
			SoulScorpion.getHitboxDims());
	public static EntityType<WitherFlea> WITHER_FLEA = registerEntityType("wither_flea", WitherFlea::new,
			WitherFlea.getHitboxDims());

	@Override
	public void onInitialize() {
		SpawnEggItem MIN_GROUND_UNIT_SPAWN_EGG = registerSpawnEgg(MIN_GROUND_UNIT, "min_ground_unit");
		SpawnEggItem SOUL_SCORPION_SPAWN_EGG = registerSpawnEgg(SOUL_SCORPION, "soul_scorpion");
		SpawnEggItem WITHER_FLEA_SPAWN_EGG = registerSpawnEgg(WITHER_FLEA, "wither_flea");
		FabricDefaultAttributeRegistry.register(MIN_GROUND_UNIT, MinGroundUnitEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(SOUL_SCORPION, SoulScorpion.createAttributes());
		FabricDefaultAttributeRegistry.register(WITHER_FLEA, WitherFlea.createAttributes());
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
			content.add(MIN_GROUND_UNIT_SPAWN_EGG);
			content.add(SOUL_SCORPION_SPAWN_EGG);
			content.add(WITHER_FLEA_SPAWN_EGG);
		});
		Registry.register(Registries.ITEM, ThirstId.id("create_formation"),
				new CreateFormationItem(new Item.Settings()
						.registryKey(ThirstId.registryKey(RegistryKeys.ITEM, "create_formation"))));
		Registry.register(Registries.ITEM, ThirstId.id("cleanup"),
				new CleanUpItem(new Item.Settings()
						.registryKey(ThirstId.registryKey(RegistryKeys.ITEM, "cleanup"))));
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			FormationedAttackState.getServerState(server).tick(server);
		});
		ModRegistries.init();
		ModSounds.init();
		ModEffects.init();
	}
}