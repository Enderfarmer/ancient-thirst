package com.thirst.systems.documentation;

import com.thirst.systems.documentation.entry.DocuEntry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;

public class DocuStorage {
    public static String assignKey(Entity entity) {
        if (entity.isPlayer())
            return "player_" + entity.getName().toString();
        return "entity_" + EntityType.getId(entity.getType()).toString().split(":")[1];
    }

    public static DocuEntry loadFromKey(String key, MinecraftServer server) {
        return DocumentationState.getServerState(server).getEntry(key.substring(key.indexOf("_") + 1));
    }

    public static DocuEntry loadFromEntity(Entity entity, MinecraftServer server) {
        return loadFromKey(assignKey(entity), server);
    }

    public static DocuEntry loadFromEntity(Entity entity) {
        return loadFromEntity(entity, entity.getEntityWorld().getServer());
    }

    public static void saveToKey(String key, DocuEntry data, MinecraftServer server) {
        DocumentationState.getServerState(server).saveEntry(key.substring(key.indexOf("_") + 1), data);
    }
}
