package com.thirst;

import java.util.function.Predicate;

import com.thirst.entity.Unit;

import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;

public class AnimationControllers {
    static AnimationController<? extends Unit> WalkFuncIdle(String functionName, Predicate<Void> playFunc) {
        return new AnimationController<Unit>("Function/Walk/Idle", state -> {
            // controllers.add(DefaultAnimations.genericWalkIdleController());
            if (playFunc.test(null)) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("func." + functionName));
            }
            if (state.isMoving()) {
                return state.setAndContinue(DefaultAnimations.WALK);
            }
            return state.setAndContinue(DefaultAnimations.IDLE);
        });
    };
}
