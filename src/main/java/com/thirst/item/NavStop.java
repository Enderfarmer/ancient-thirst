package com.thirst.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import com.thirst.Utils;
import com.thirst.systems.formation.FormationedAttackState;

public class NavStop extends Item {
    public NavStop(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            Utils.log("Active formations: " + FormationedAttackState.getServerState(world.getServer()).activeAttacks,
                    world.getPlayers().get(0));

        }
        return ActionResult.SUCCESS;
    };

}
