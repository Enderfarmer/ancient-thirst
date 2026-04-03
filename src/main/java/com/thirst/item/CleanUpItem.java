package com.thirst.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CleanUpItem extends Item {
    public CleanUpItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            int radius = 5;
            HitResult hit = player.raycast(100.0D, 0.0F, false);
            BlockPos center = BlockPos.ofFloored(hit.getPos());
            BlockPos start = center.add(-radius, -radius, -radius);
            BlockPos end = center.add(radius, radius, radius);

            // Iterates through every block in the 3D box
            for (BlockPos pos : BlockPos.iterate(start, end)) {
                BlockState currentState = world.getBlockState(pos);

                if (currentState.isOf(Blocks.SOUL_SOIL)) {
                    world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
                }
            }

        }
        return ActionResult.SUCCESS;
    };

}
