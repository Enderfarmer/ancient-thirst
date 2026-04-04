package com.thirst.common.entity;

public interface Infectable {
    void setInfected(boolean value);

    boolean isInfected();

    void setInfectedGoals();

    boolean hasInfectedGoals();

    boolean isAmbushing();

    void ambush();
}