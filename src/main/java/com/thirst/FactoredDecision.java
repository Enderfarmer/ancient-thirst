package com.thirst;

import java.util.List;
import java.util.function.Function;

public class FactoredDecision<T> {
    float chance;
    List<Factor<T>> factors;

    public static record Factor<T>(Function<T, Boolean> condition, float weight) {
    }

    public FactoredDecision(float chance, Factor<T>... factors) {
        this.factors = List.of(factors);
        this.chance = chance;
    }

    public boolean evaluate(T context) {
        float totalWeight = 0;
        float maxWeight = 0;
        for (Factor<T> factor : factors) {
            if (factor.condition.apply(context)) {
                totalWeight += factor.weight;
            }
            maxWeight += factor.weight;
        }
        AncientThirst.LOGGER.debug("Running evaluate with chance being " + totalWeight / maxWeight);
        return Math.random() < (totalWeight / maxWeight) && Math.random() < chance;
    }
}