package com.thirst.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.thirst.Utils;
import com.thirst.systems.formation.types.CircleFormation;

public class CreateFormationItem extends Item {
    public CreateFormationItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            Utils.log("LoggerItem: Using logger item", user);
            HitResult hit = user.raycast(100.0D, 0.0F, false);
            CircleFormation.startAttack(world.getServer(), BlockPos.ofFloored(hit.getPos()));
            return ActionResult.SUCCESS;
        }
        ;
        return ActionResult.PASS;

    }
}