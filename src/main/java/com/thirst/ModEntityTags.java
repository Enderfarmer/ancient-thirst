package com.thirst;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModEntityTags {
        private static TagKey<EntityType<?>> create(String name) {
                return TagKey.of(RegistryKeys.ENTITY_TYPE, ThirstId.id(name));
        }

        public static final TagKey<EntityType<?>> MINOR_GROWTH = create("minor_growth");
        public static final TagKey<EntityType<?>> PASSIVE_SOURCE = create("passive_sources");
        public static final TagKey<EntityType<?>> HOSTILE_TARGET = create("hostile_target");
        public static final TagKey<EntityType<?>> RANGED_ONLY = create("ranged_only");
        public static final TagKey<EntityType<?>> ABILITY = create("ability");
        public static final TagKey<EntityType<?>> STRENGTH_BOOST = create("boosts/strength");
        public static final TagKey<EntityType<?>> SPEED_BOOST = create("boosts/speed");
        public static final TagKey<EntityType<?>> SHELL_BOOST = create("boosts/shell");
}
