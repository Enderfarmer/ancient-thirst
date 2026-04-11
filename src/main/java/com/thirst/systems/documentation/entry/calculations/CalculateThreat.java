package com.thirst.systems.documentation.entry.calculations;

import com.thirst.systems.documentation.attributes.AdvancedAttrs;
import com.thirst.systems.documentation.attributes.BasicAttrs;
import com.thirst.systems.documentation.entry.calculations.ProcessingSettings.TotalThreatCalculation.Additionals;
import com.thirst.systems.documentation.entry.calculations.ProcessingSettings.TotalThreatCalculation.Weights;
import com.thirst.systems.documentation.entry.calculations.ProcessingSettings.TotalThreatCalculation.Coefficients;
import com.thirst.systems.documentation.entry.calculations.ProcessingSettings.TotalThreatCalculation.Weights;

public class CalculateThreat {
    BasicAttrs basicAttrs;
    AdvancedAttrs advancedAttrs;
    Lethalities lethalities;

    public CalculateThreat(BasicAttrs basicAttrs, AdvancedAttrs advancedAttrs, Lethalities lethalities) {
        this.advancedAttrs = advancedAttrs;
        this.basicAttrs = basicAttrs;
        this.lethalities = lethalities;
    }

    public double calculate() {
        double HEALTH = Coefficients.HEALTH * basicAttrs.health;
        double ARMOR = Weights.ARMOR * basicAttrs.armor;
        double MELEE_LETHALITY = Weights.MELEE_LETHALITY * lethalities.melee();
        double RANGED_LETHALITY = Weights.RANGED_LETHALITY * lethalities.ranged();
        double MAGIC_LETHALITY = Weights.MAGIC_LETHALITY * lethalities.magic();
        double KINETIC_LETHALITY = Weights.KINETIC_LETHALITY * lethalities.movement();
        int HIGH_REACH = basicAttrs.hasHighReach ? Additionals.HIGH_REACH : 0;
        int HIGH_MANEUVERABILITY = advancedAttrs.highManeuverability ? Additionals.HIGH_MANEUVERABILITY : 0;
        int SHIELD = advancedAttrs.hasShield ? Additionals.SHIELD : 0;

        return HEALTH * (MELEE_LETHALITY + RANGED_LETHALITY + MAGIC_LETHALITY + KINETIC_LETHALITY) + ARMOR
                + HIGH_MANEUVERABILITY + HIGH_REACH + SHIELD;
    }
}
