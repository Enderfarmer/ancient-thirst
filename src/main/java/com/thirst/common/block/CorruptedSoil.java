package com.thirst.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class CorruptedSoil extends Block {
    public CorruptedSoil(Settings settings) {
        super(settings);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        world.addParticleClient(ParticleTypes.SOUL, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.2D, 0.0D);
    }
}
