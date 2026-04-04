package com.thirst.common;

import javax.swing.text.html.parser.Entity;

import com.thirst.ThirstId;
import com.thirst.common.entity.Hitbox;
import com.thirst.common.entity.MinGroundUnitEntity;
import com.thirst.common.entity.SoulScorpion;
import com.thirst.common.entity.WitherFlea;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
        private static SpawnEggItem registerSpawnEgg(EntityType<? extends LivingEntity> entityType, String name) {
                RegistryKey<Item> registryKey = ThirstId.registryKey(RegistryKeys.ITEM, name + "_spawn_egg");
                SpawnEggItem spawnEgg = new SpawnEggItem(
                                new Item.Settings().spawnEgg(entityType).registryKey(registryKey));
                return Registry.register(Registries.ITEM, registryKey, spawnEgg);
        }

        private static <T extends LivingEntity> EntityType<T> registerEntityType(String name,
                        EntityType.EntityFactory<T> factory, Hitbox hitbox,
                        DefaultAttributeContainer.Builder attributes) {
                Identifier id = ThirstId.id(name);
                RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);

                // .build() takes the ID as a string or a Key depending on the version.
                // Usually, you pass the ID string or nothing to build() and do the KEY work in
                // Registry.register.
                EntityType<T> entityType = EntityType.Builder.create(factory, SpawnGroup.CREATURE)
                                .dimensions(hitbox.width, hitbox.height)
                                .build(key); // Ensure 'key' here matches the one in Registry.register
                Registry.register(Registries.ENTITY_TYPE, key, entityType);
                FabricDefaultAttributeRegistry.register(entityType, attributes);
                return entityType;
        }

        public static final EntityType<MinGroundUnitEntity> MIN_GROUND_UNIT = registerEntityType("min_ground_unit",
                        MinGroundUnitEntity::new, MinGroundUnitEntity.getHitboxDims(),
                        MinGroundUnitEntity.createAttributes());
        public static final SpawnEggItem MIN_GROUND_UNIT_SPAWN_EGG = registerSpawnEgg(MIN_GROUND_UNIT,
                        "min_ground_unit");

        public static final EntityType<SoulScorpion> SOUL_SCORPION = registerEntityType("soul_scorpion",
                        SoulScorpion::new,
                        SoulScorpion.getHitboxDims(), SoulScorpion.createAttributes());
        public static final SpawnEggItem SOUL_SCORPION_SPAWN_EGG = registerSpawnEgg(SOUL_SCORPION, "soul_scorpion");

        public static final EntityType<WitherFlea> WITHER_FLEA = registerEntityType("wither_flea", WitherFlea::new,
                        WitherFlea.getHitboxDims(), WitherFlea.createAttributes());
        public static final SpawnEggItem WITHER_FLEA_SPAWN_EGG = registerSpawnEgg(WITHER_FLEA, "wither_flea");

        public static void init() {
                ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
                        content.add(MIN_GROUND_UNIT_SPAWN_EGG);
                        content.add(SOUL_SCORPION_SPAWN_EGG);
                        content.add(WITHER_FLEA_SPAWN_EGG);
                });
                // This method is intentionally left blank. Its purpose is to ensure that the
                // class is loaded and static initializers are run.
        }
}
