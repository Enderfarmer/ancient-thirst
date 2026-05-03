package com.thirst;

import java.util.List;
import java.util.Properties;

import com.thirst.common.ModBlockTags;
import com.thirst.common.ModBlocks;
import com.thirst.mass.MassState;
import com.thirst.systems.mutations.MutationState;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class Utils {
    public static void log(String text, PlayerEntity player) {
        if (player != null) {
            player.sendMessage(Text.literal(text), false);
        }
    }

    public static void logAList(List<String> texts, PlayerEntity player) {
        if (player != null) {
            for (String msg : texts) {
                player.sendMessage(Text.literal(msg), false);
            }
        }
    }

    public static boolean isWitherable(BlockPos pos, World world) {
        return world.getBlockState(pos).isIn(ModBlockTags.WITHERABLE);
    }

    public static boolean isWithered(BlockPos pos, World world) {
        return world.getBlockState(pos).isIn(ModBlockTags.WITHERED);
    }

    public static void witherBlock(BlockPos pos, World world) {
        MassState.getServerState(world.getServer())
                .onInfectBlock(world.getBlockState(pos));
        MutationState.getServerState(world.getServer())
                .onBlockInfect(world.getBlockState(pos));
        BlockState state = world.getBlockState(pos);
        if (state.isIn(BlockTags.LOGS)) {
            world.setBlockState(pos, ModBlocks.SOULWOOD_LOG.getDefaultState());
        } else {
            world.setBlockState(pos, ModBlocks.CORRUPTED_SOIL.getDefaultState());
        }
    }

    public static double findValidY(World world, double x, double z, double referenceY) {
        // 1. Get the highest block at this X/Z (The Surface)
        int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z);

        // 2. If the surface is too far away vertically (e.g., a cliff),
        // we want to check blocks closer to the Leader's height first.
        // This prevents units from snapping to the roof of a house when the leader is
        // inside.
        BlockPos referencePos = new BlockPos((int) x, (int) referenceY, (int) z);

        // Check if there is a floor near the reference height (within 3 blocks up or
        // down)
        for (int offset : new int[] { 0, 1, -1, 2, -2, 3, -3 }) {
            BlockPos checkPos = referencePos.up(offset);
            if (world.getBlockState(checkPos).isSolidBlock(world, checkPos) &&
                    world.isAir(checkPos.up())) {
                return checkPos.getY() + 1.0; // Return top of the solid block
            }
        }

        // 3. Fallback: If no local floor is found, use the surface heightmap
        return topY;
    }

    public static boolean isPosReachable(PathAwareEntity mob, BlockPos target) {
        if (!mob.getEntityWorld().getBlockState(target.up()).isAir()) {
            return false;
        }

        Path path = mob.getNavigation().findPathTo(target, 1);
        return path != null && (path.reachesTarget() || path.isFinished());
    }

}
