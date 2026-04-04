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
import com.thirst.common.ModEntities;
import com.thirst.common.ModItems;
import com.thirst.common.ModSounds;
import com.thirst.common.entity.Hitbox;
import com.thirst.common.entity.MinGroundUnitEntity;
import com.thirst.common.entity.SoulScorpion;
import com.thirst.common.entity.WitherFlea;
import com.thirst.common.item.CleanUpItem;
import com.thirst.common.item.CreateFormationItem;
import com.thirst.systems.formation.FormationedAttackState;

public class AncientThirst implements ModInitializer {
	public static final String MOD_ID = "ancient-thirst";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.init();
		ModEntities.init();
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			FormationedAttackState.getServerState(server).tick(server);
		});
		ModRegistries.init();
		ModSounds.init();
		ModEffects.init();
	}
}