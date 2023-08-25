package com.divinity.hmedia.rgrbillionaire.block;

import com.divinity.hmedia.rgrbillionaire.block.be.CryptoMinerBlockEntity;
import com.divinity.hmedia.rgrbillionaire.cap.GlobalLevelHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.network.clientbound.SyncCryptoBlockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

public class NetheriteMinerBlock extends CryptoMinerBlock {
    public NetheriteMinerBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof CryptoMinerBlockEntity crypto) {
                crypto.addAmount(10);
                GlobalLevelHolderAttacher.getGlobalLevelCapability(pLevel).ifPresent(h -> h.increaseProductionRate(crypto.amount));
                NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncCryptoBlockPacket(crypto.amount, pPos));
            }
        }
    }
}
