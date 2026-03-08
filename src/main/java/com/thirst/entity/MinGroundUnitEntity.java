package com.thirst.entity;

import org.jspecify.annotations.NonNull;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
// import net.minecraft.entity.ai.goal.EscapeDangerGoal;
// import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MinGroundUnitEntity extends GroundUnit {
    private final @NonNull AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public MinGroundUnitEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void witherGround() {
        BlockPos positionTarget = this.getPositionTarget();
        getEntityWorld().setBlockState(positionTarget,
                Blocks.SOUL_SOIL.getDefaultState());
        getEntityWorld().playSound(null, positionTarget,
                SoundEvents.BLOCK_CHERRY_SAPLING_BREAK,
                SoundCategory.BLOCKS, 1.0f, 0.5f);
    }

}