package com.divinity.hmedia.rgrbillionaire.mixin;

import com.divinity.hmedia.rgrbillionaire.block.CryptoMinerBlock;
import com.divinity.hmedia.rgrbillionaire.block.be.CryptoMinerBlockEntity;
import com.divinity.hmedia.rgrbillionaire.init.BlockInit;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fuck you MCDev plugin and Fuck you Intellij
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {

    @Shadow
    protected ClientLevel level;

    @Shadow public abstract void add(Particle pEffect);


    @Inject(
            method= "destroy(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
              at = @At("HEAD"),
              cancellable = true
      )
    public void destroy(BlockPos pPos, BlockState pState, CallbackInfo ci) {
        if (pState.getBlock() instanceof CryptoMinerBlock) {

            VoxelShape voxelshape = pState.getShape(this.level, pPos);
            double d0 = 0.25D;
            voxelshape.forAllBoxes((p_172273_, p_172274_, p_172275_, p_172276_, p_172277_, p_172278_) -> {
              double d1 = Math.min(1.0D, p_172276_ - p_172273_);
              double d2 = Math.min(1.0D, p_172277_ - p_172274_);
              double d3 = Math.min(1.0D, p_172278_ - p_172275_);
              int i = Math.max(2, Mth.ceil(d1 / 0.25D));
              int j = Math.max(2, Mth.ceil(d2 / 0.25D));
              int k = Math.max(2, Mth.ceil(d3 / 0.25D));

              for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 < j; ++i1) {
                  for (int j1 = 0; j1 < k; ++j1) {
                    double d4 = ((double) l + 0.5D) / (double) i;
                    double d5 = ((double) i1 + 0.5D) / (double) j;
                    double d6 = ((double) j1 + 0.5D) / (double) k;
                    double d7 = d4 * d1 + p_172273_;
                    double d8 = d5 * d2 + p_172274_;
                    double d9 = d6 * d3 + p_172275_;
                    BlockState state = pState;
                    var entity = this.level.getBlockEntity(pPos);
                    if (entity instanceof CryptoMinerBlockEntity cryptoMinerBlockEntity) {
                      switch (cryptoMinerBlockEntity.amount) {
                        case 3 -> state = Blocks.IRON_BLOCK.defaultBlockState();
                        case 5 -> state = Blocks.GOLD_BLOCK.defaultBlockState();
                        case 7 -> state = Blocks.DIAMOND_BLOCK.defaultBlockState();
                        case 10 -> state = Blocks.NETHERITE_BLOCK.defaultBlockState();
                        case 1000 -> state = Blocks.OBSIDIAN.defaultBlockState();
                      }
                    }
                    this.add(new TerrainParticle(this.level, (double) pPos.getX() + d7, (double) pPos.getY() + d8, (double) pPos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state, pPos).updateSprite(state, pPos));
                  }
                }
              }
            });
          ci.cancel();
        }
    }

    @Inject(
            method= "crack",
            at = @At("HEAD"),
            cancellable = true
    )
    public void crack(BlockPos pPos, Direction pSide, CallbackInfo ci) {
        BlockState blockstate = this.level.getBlockState(pPos);
        if (blockstate.getBlock() instanceof CryptoMinerBlock) {
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                int i = pPos.getX();
                int j = pPos.getY();
                int k = pPos.getZ();
                float f = 0.1F;
                AABB aabb = blockstate.getShape(this.level, pPos).bounds();
                double d0 = (double) i + this.level.random.nextDouble() * (aabb.maxX - aabb.minX - (double) 0.2F) + (double) 0.1F + aabb.minX;
                double d1 = (double) j + this.level.random.nextDouble() * (aabb.maxY - aabb.minY - (double) 0.2F) + (double) 0.1F + aabb.minY;
                double d2 = (double) k + this.level.random.nextDouble() * (aabb.maxZ - aabb.minZ - (double) 0.2F) + (double) 0.1F + aabb.minZ;
                if (pSide == Direction.DOWN) {
                    d1 = (double) j + aabb.minY - (double) 0.1F;
                }

                if (pSide == Direction.UP) {
                    d1 = (double) j + aabb.maxY + (double) 0.1F;
                }

                if (pSide == Direction.NORTH) {
                    d2 = (double) k + aabb.minZ - (double) 0.1F;
                }

                if (pSide == Direction.SOUTH) {
                    d2 = (double) k + aabb.maxZ + (double) 0.1F;
                }

                if (pSide == Direction.WEST) {
                    d0 = (double) i + aabb.minX - (double) 0.1F;
                }

                if (pSide == Direction.EAST) {
                    d0 = (double) i + aabb.maxX + (double) 0.1F;
                }

                var entity = this.level.getBlockEntity(pPos);

                if (entity instanceof CryptoMinerBlockEntity cryptoMinerBlockEntity) {
                    switch (cryptoMinerBlockEntity.amount) {
                        case 3 -> blockstate = Blocks.IRON_BLOCK.defaultBlockState();
                        case 5 -> blockstate = Blocks.GOLD_BLOCK.defaultBlockState();
                        case 7 -> blockstate = Blocks.DIAMOND_BLOCK.defaultBlockState();
                        case 10 -> blockstate = Blocks.NETHERITE_BLOCK.defaultBlockState();
                        case 1000 -> blockstate = Blocks.OBSIDIAN.defaultBlockState();
                    }
                }

                this.add((new TerrainParticle(this.level, d0, d1, d2, 0.0D, 0.0D, 0.0D, blockstate, pPos).updateSprite(blockstate, pPos)).setPower(0.2F).scale(0.6F));
            }
            ci.cancel();
        }
    }
}
