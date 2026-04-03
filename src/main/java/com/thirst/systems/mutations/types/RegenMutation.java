package com.thirst.systems.mutations.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thirst.common.ModEntityTags;
import com.thirst.common.ModSounds;
import com.thirst.entity.Unit;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RegenMutation extends MutationBase {
    private double MAX_HEAL_AMOUNT = 6.0;
    private int MAX_KILLS = 60;
    private int kills = 0;

    @Override
    protected int getCounter() {
        return kills;
    }

    @Override
    public String getType() {
        return "regen";
    }

    @Override
    public void onBlockInfect(BlockState blockState) {
    }

    @Override
    public void onKill(EntityType entityType) {
        if (kills >= MAX_KILLS)
            return;
        if (entityType.isIn(ModEntityTags.REGEN_BOOST))
            kills++;
    }

    @Override
    protected void processUnitInternal(Unit mob) {
    }

    @Override
    public String toString() {
        return "RegenUpgrade with kills being " + kills;
    }

    @Override
    public void processUnit(Unit mob) {
        if (mob.getHealth() < mob.getMaxHealth() && mob.age % 60 == 0) {
            boolean shouldHeal = false;
            BlockPos catalystPos = mob.getBlockPos();
            for (BlockPos pos : BlockPos.iterateOutwards(mob.getBlockPos(), 2, 2, 2)) {
                if (mob.getEntityWorld().getBlockState(pos).isOf(Blocks.SOUL_SOIL)) {
                    shouldHeal = true;

                    catalystPos = pos;
                    break;
                }
            }
            ;
            if (shouldHeal) {
                double amount = (double) MAX_HEAL_AMOUNT / (double) MAX_KILLS * (double) kills;
                amount = Math.max(2, amount);
                mob.heal((float) amount);
                if (!mob.getEntityWorld().isClient()) {
                    ServerWorld world = (ServerWorld) mob.getEntityWorld();

                    // 1. Get the start (Soul Soil) and end (Mob)
                    Vec3d start = catalystPos.toCenterPos();
                    Vec3d end = new Vec3d(mob.getBlockPos());

                    // 2. Calculate the delta
                    Vec3d path = end.subtract(start);

                    // 3. Spawn 5 particles at random spots along that path
                    for (int i = 0; i < 5; i++) {
                        double pct = world.random.nextDouble(); // Random spot (0.0 to 1.0)

                        // The "Lerp" math: Start + (Path * Percentage)
                        double x = start.x + (path.x * pct);
                        double y = start.y + (path.y * pct);
                        double z = start.z + (path.z * pct);

                        // Use a "Soul" themed particle like SOUL or SOUL_FIRE_FLAME
                        world.spawnParticles(ParticleTypes.SOUL, x, y, z, 1, 0, 0, 0, 0.2);
                    }
                }
                if (mob.getEntityWorld().random.nextDouble() < .3 && mob.getHealth() < mob.getMaxHealth() * 0.3)
                    mob.getEntityWorld().playSound(mob, mob.getBlockPos(), ModSounds.FLESH_GROWING,
                            SoundCategory.HOSTILE);
            }
        }
    }

    public RegenMutation(int kills) {
        this.kills = kills;
    }

    public static MapCodec<RegenMutation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("kills").forGetter(m -> m.kills)).apply(instance, RegenMutation::new));
}
