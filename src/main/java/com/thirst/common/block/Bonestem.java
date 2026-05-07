package com.thirst.common.block;

import java.util.Iterator;

import com.thirst.AncientThirst;
import com.thirst.Utils;
import com.thirst.common.ModBlockTags;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class Bonestem extends Block {
    public static final BooleanProperty HAS_BLOCKS_TO_INFECT = BooleanProperty.of("hasblockstoinfect");

    public Bonestem(Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(HAS_BLOCKS_TO_INFECT, true));
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity,
            EntityCollisionHandler handler, boolean bl) {
        if (entity instanceof LivingEntity) {
            if (world instanceof ServerWorld serverWorld) {
                Vec3d vec3d = entity.isControlledByPlayer() ? entity.getMovement()
                        : entity.getLastRenderPos().subtract(entity.getEntityPos());
                if (vec3d.horizontalLengthSquared() > 0.0) {
                    double d = Math.abs(vec3d.getX());
                    double e = Math.abs(vec3d.getZ());
                    if (d >= 0.003F || e >= 0.003F) {
                        if (world.getRandom().nextInt(5) == 3) {
                            entity.damage(serverWorld, world.getDamageSources().wither(), 1.0F);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolid();
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return state.get(HAS_BLOCKS_TO_INFECT);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 20 + world.random.nextInt(20));
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(HAS_BLOCKS_TO_INFECT)) {
            world.scheduleBlockTick(pos, this, 20 + world.random.nextInt(20));
            Iterator<BlockPos> iterator = BlockPos.iterateOutwards(pos, 7, 3, 7).iterator();
            while (iterator.hasNext()) {
                BlockPos targetPos = iterator.next();
                if (Utils.isWitherable(targetPos, world) && !Utils.isWithered(targetPos, world)) {
                    Utils.witherBlock(targetPos, world);
                    return;
                }
            }
            AncientThirst.LOGGER.info("No blocks to infect left");
            world.setBlockState(pos, state.with(HAS_BLOCKS_TO_INFECT, false));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_BLOCKS_TO_INFECT);
    }

}
