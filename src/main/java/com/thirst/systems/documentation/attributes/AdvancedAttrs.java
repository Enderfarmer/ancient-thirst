package com.thirst.systems.documentation.attributes;

import com.mojang.serialization.Codec;
import com.thirst.systems.documentation.entry.Amplifier;

public class AdvancedAttrs {
    public Amplifier movement = Amplifier.NONE;
    public boolean usesMagic = false;
    public boolean hasShield = false;
    public boolean highManeuverability = false;

    public static final Codec<AdvancedAttrs> CODEC = Codec.STRING.xmap(str -> {
        AdvancedAttrs attrs = new AdvancedAttrs();
        String[] parts = str.split(",");
        attrs.movement = Amplifier.valueOf(parts[0]);
        attrs.usesMagic = Boolean.parseBoolean(parts[1]);
        attrs.hasShield = Boolean.parseBoolean(parts[2]);
        attrs.highManeuverability = Boolean.parseBoolean(parts[3]);
        return attrs;
    }, attrs -> String.join(",",
            attrs.movement.name(),
            Boolean.toString(attrs.usesMagic),
            Boolean.toString(attrs.hasShield),
            Boolean.toString(attrs.highManeuverability)));
}
