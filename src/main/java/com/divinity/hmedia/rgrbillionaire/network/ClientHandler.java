package com.divinity.hmedia.rgrbillionaire.network;

import com.divinity.hmedia.rgrbillionaire.block.be.CryptoMinerBlockEntity;
import com.divinity.hmedia.rgrbillionaire.client.gui.MoneyExplosionGuiOverlay;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientHandler {

    public static void startMoneyExplosionAnimation() {
        MoneyExplosionGuiOverlay.INSTANCE.setStartTime(Util.getMillis());
        MoneyExplosionGuiOverlay.INSTANCE.setEnabled(true);
    }

    public static void syncCryptoMiner(int amount, BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != null) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CryptoMinerBlockEntity cryptoMinerBlockEntity) {
                cryptoMinerBlockEntity.setAmount(amount);
            }
        }
    }
}
