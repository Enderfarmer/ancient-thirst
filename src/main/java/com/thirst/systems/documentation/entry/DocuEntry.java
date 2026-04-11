package com.thirst.systems.documentation.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DocuEntry {
    public String docuKey;
    public RawDocuEntry rawEntry;
    public ProcessedDocuEntry processedEntry;

    public DocuEntry(String docuKey, RawDocuEntry rawEntry, ProcessedDocuEntry processedEntry) {
        this.docuKey = docuKey;
        this.rawEntry = rawEntry;
        this.processedEntry = processedEntry;
    }

    public String getDocuKey() {
        return docuKey;
    }

    public RawDocuEntry getRawDocuEntry() {
        return rawEntry;
    }

    public ProcessedDocuEntry getProcessedDocuEntry() {
        return processedEntry;
    }

    public static final Codec<DocuEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(DocuEntry::getDocuKey),
            RawDocuEntry.CODEC.fieldOf("rawEntry").forGetter(DocuEntry::getRawDocuEntry),
            ProcessedDocuEntry.CODEC.fieldOf("processedEntry").forGetter(DocuEntry::getProcessedDocuEntry))
            .apply(instance, DocuEntry::new));
}
