package com.thirst.systems.documentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.thirst.systems.documentation.entry.DocuEntry;
import com.thirst.systems.documentation.entry.ProcessedDocuEntry;
import com.thirst.systems.documentation.entry.RawDocuEntry;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public class DocumentationState extends PersistentState {
    private HashMap<String, DocuEntry> docuData = new HashMap<>();

    public DocumentationState() {
    }

    public DocumentationState(Map<String, DocuEntry> docuData) {
        this.docuData = new HashMap<>(docuData);
    }

    public HashMap<String, DocuEntry> getDocuData() {
        return docuData;
    }

    public DocuEntry getEntry(String key) {
        DocuEntry entry = docuData.get(key);
        if (entry != null)
            return entry;
        DocuEntry newEntry = new DocuEntry(key, new RawDocuEntry(key), new ProcessedDocuEntry());
        docuData.put(key, newEntry);
        return newEntry;
    }

    public void saveEntry(String key, DocuEntry entry) {
        docuData.put(key, entry);
        this.markDirty();
    }

    public static final Codec<DocumentationState> CODEC = Codec.unboundedMap(Codec.STRING, DocuEntry.CODEC)
            .xmap(DocumentationState::new, DocumentationState::getDocuData);
    public static final PersistentStateType<DocumentationState> TYPE = new PersistentStateType<DocumentationState>(
            "documentation", DocumentationState::new, CODEC, null);

    public static DocumentationState getServerState(net.minecraft.server.MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(
                TYPE);
    }
}
