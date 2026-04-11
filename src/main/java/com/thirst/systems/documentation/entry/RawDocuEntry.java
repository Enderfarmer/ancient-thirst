package com.thirst.systems.documentation.entry;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.systems.documentation.attributes.AdvancedAttrs;
import com.thirst.systems.documentation.attributes.BasicAttrs;

public class RawDocuEntry {
    private String docuKey;

    public String getDocuKey() {
        return docuKey;
    }

    private List<UnitKill> unitKills;

    public List<UnitKill> getUnitKills() {
        return unitKills;
    }

    public void addKill(UnitKill kill) {
        unitKills.add(kill);
    }

    private BasicAttrs basicAttributes = new BasicAttrs();

    public void setBasicAttributes(BasicAttrs basicAttributes) {
        this.basicAttributes = basicAttributes;
    }

    public BasicAttrs getBasicAttributes() {
        return basicAttributes;
    }

    private AdvancedAttrs advancedAttributes = new AdvancedAttrs();

    public void setAdvancedAttributes(AdvancedAttrs advancedAttributes) {
        this.advancedAttributes = advancedAttributes;
    }

    public AdvancedAttrs getAdvancedAttributes() {
        return advancedAttributes;
    }

    public RawDocuEntry(String docuKey, List<UnitKill> unitKills, BasicAttrs basicAttributes,
            AdvancedAttrs advancedAttributes) {
        this.docuKey = docuKey;
        this.unitKills = new ArrayList<>(unitKills);
        this.basicAttributes = basicAttributes;
        this.advancedAttributes = advancedAttributes;
    }

    public RawDocuEntry(String docuKey) {
        this.docuKey = docuKey;
        this.unitKills = new ArrayList<>();
        this.basicAttributes = new BasicAttrs();
        this.advancedAttributes = new AdvancedAttrs();
    }

    public static final Codec<RawDocuEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("docuKey").forGetter(RawDocuEntry::getDocuKey),
            UnitKill.CODEC.listOf().fieldOf("unitKills").forGetter(RawDocuEntry::getUnitKills),
            BasicAttrs.CODEC.fieldOf("basicAttributes").forGetter(RawDocuEntry::getBasicAttributes),
            AdvancedAttrs.CODEC.fieldOf("advancedAttributes").forGetter(RawDocuEntry::getAdvancedAttributes))
            .apply(instance, RawDocuEntry::new));
}
