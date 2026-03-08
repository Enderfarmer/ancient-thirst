package com.thirst;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.entity.EntityRendererFactories;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AncientThirstClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererFactories.register(AncientThirst.MIN_GROUND_UNIT,
				context -> new GeoEntityRenderer<>(context, AncientThirst.MIN_GROUND_UNIT));
	}
}