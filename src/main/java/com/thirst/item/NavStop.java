package com.thirst.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.function.Predicate;

import com.thirst.Utils;
import com.thirst.entity.MinGroundUnitEntity;

public class NavStop extends Item {
    public NavStop(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            Utils.log("NavStop: Stopping navigation", player);
            world.getEntitiesByClass(MinGroundUnitEntity.class, new Box(-200, -60, -200, 60, 200, 60),
                    new Predicate<MinGroundUnitEntity>() {
                        public boolean test(MinGroundUnitEntity entity) {
                            return true; // You can add additional filtering logic here if needed
                        }
                    })
                    .forEach(entity -> {
                        Utils.log("NavStop: Stopping navigation for: " + entity.getBlockPos(), player);
                        entity.getNavigation().stop();
                    });

        }
        return ActionResult.SUCCESS;
    };

}
