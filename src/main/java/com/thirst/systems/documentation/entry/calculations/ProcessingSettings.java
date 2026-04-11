package com.thirst.systems.documentation.entry.calculations;

public class ProcessingSettings {
    public class PropertyTresholds {
        public static final int ONE_SHOTTING = 10;
        public static final int IS_SWEEPING = 6;
    }

    public class TotalThreatCalculation {
        public class Coefficients {
            public static final float HEALTH = 0.1f;
        }

        public class Weights {
            public static final float ARMOR = 0.2f;
            public static final float MELEE_LETHALITY = 1.2f;
            public static final float RANGED_LETHALITY = 1.4f;
            public static final float MAGIC_LETHALITY = 1.5f;
            public static final float KINETIC_LETHALITY = 1.6f;
        }

        public class Additionals {
            public static final int HIGH_REACH = 7;
            public static final int SHIELD = 5;
            public static final int HIGH_MANEUVERABILITY = 10;
        }
    }
}
