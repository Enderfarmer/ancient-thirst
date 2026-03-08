package com.thirst.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.function.Predicate;

import com.thirst.Utils;
import com.thirst.entity.MinGroundUnitEntity;

public class LoggerItem extends Item {
    public LoggerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            Utils.log("LoggerItem: Using logger item", user);
            HitResult hit = user.raycast(100.0D, 0.0F, false);
            world.getEntitiesByClass(MinGroundUnitEntity.class, new Box(-200, -60, -200, 600, 200, 600),
                    new Predicate<MinGroundUnitEntity>() {
                        public boolean test(MinGroundUnitEntity entity) {
                            return true; // You can add additional filtering logic here if needed
                        }
                    })
                    .forEach(entity -> {
                        Utils.log("LoggerItem: Starting navigation to: " + hit.getPos(), user);
                        entity.getNavigation().stop();
                        entity.getNavigation().startMovingTo(hit.getPos().getX(), hit.getPos().getY(),
                                hit.getPos().getZ(), 1.0D);
                    });
            return ActionResult.SUCCESS;
        }
        ;
        return ActionResult.PASS;

    }
}