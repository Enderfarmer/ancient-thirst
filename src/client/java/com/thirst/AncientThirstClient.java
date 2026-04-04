package com.thirst;

import com.thirst.common.ModEntities;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AncientThirstClient implements ClientModInitializer {
	public static void registerRenderer(EntityType<? extends PathAwareEntity> entityType) {
		EntityRendererFactories.register(entityType, context -> new GeoEntityRenderer(context, entityType));
	}

	@Override
	public void onInitializeClient() {
		registerRenderer(ModEntities.MIN_GROUND_UNIT);
		registerRenderer(ModEntities.SOUL_SCORPION);
		registerRenderer(ModEntities.WITHER_FLEA);
	}
}