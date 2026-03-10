package com.thirst.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MinGroundUnitEntity extends GroundUnit {
    public static int cooldown = 20;

    public MinGroundUnitEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 6.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.2);
    };

    @Override
    public void witherGround() {
        BlockPos positionTarget = this.getPositionTarget();
        getEntityWorld().setBlockState(positionTarget,
                Blocks.SOUL_SOIL.getDefaultState());
        getEntityWorld().playSound(null, positionTarget,
                SoundEvents.BLOCK_CHERRY_SAPLING_BREAK,
                SoundCategory.BLOCKS, 1.0f, 0.5f);
    }

    public static Hitbox getHitboxDims() {
        return new Hitbox(0.75f, 0.5f);
    }

}