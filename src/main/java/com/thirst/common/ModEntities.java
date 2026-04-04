package com.thirst.common;

import javax.swing.text.html.parser.Entity;

import com.thirst.ThirstId;
import com.thirst.common.entity.Hitbox;
import com.thirst.common.entity.MinGroundUnitEntity;
import com.thirst.common.entity.SoulScorpion;
import com.thirst.common.entity.WitherFlea;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    private static SpawnEggItem registerSpawnEgg(EntityType<? extends LivingEntity> entityType, String name) {
        RegistryKey<Item> registryKey = ThirstId.registryKey(RegistryKeys.ITEM, name + "_spawn_egg");
        SpawnEggItem spawnEgg = new SpawnEggItem(new Item.Settings().spawnEgg(entityType).registryKey(registryKey));
        return Registry.register(Registries.ITEM, registryKey, spawnEgg);
    }

    private static <T extends LivingEntity> EntityType<T> registerEntityType(String name,
            EntityType.EntityFactory<T> factory, Hitbox hitbox) {
        Identifier id = ThirstId.id(name);
        RegistryKey<EntityType<?>> key = ThirstId.registryKey(RegistryKeys.ENTITY_TYPE, name);
        EntityType<T> entityType = EntityType.Builder.create(factory, SpawnGroup.CREATURE)
                .dimensions(hitbox.width, hitbox.height)
                .build(key);
        return Registry.register(Registries.ENTITY_TYPE, id,
                entityType);
    }

    public static final EntityType<MinGroundUnitEntity> MIN_GROUND_UNIT = registerEntityType("min_ground_unit",
            MinGroundUnitEntity::new, MinGroundUnitEntity.getHitboxDims());
    public static final SpawnEggItem MIN_GROUND_UNIT_SPAWN_EGG = registerSpawnEgg(MIN_GROUND_UNIT, "min_ground_unit");

    public static final EntityType<SoulScorpion> SOUL_SCORPION = registerEntityType("soul_scorpion", SoulScorpion::new,
            SoulScorpion.getHitboxDims());
    public static final SpawnEggItem SOUL_SCORPION_SPAWN_EGG = registerSpawnEgg(SOUL_SCORPION, "soul_scorpion");

    public static final EntityType<WitherFlea> WITHER_FLEA = registerEntityType("wither_flea", WitherFlea::new,
            WitherFlea.getHitboxDims());
    public static final SpawnEggItem WITHER_FLEA_SPAWN_EGG = registerSpawnEgg(WITHER_FLEA, "wither_flea");
}
