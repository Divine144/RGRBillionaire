package com.divinity.hmedia.rgrbillionaire.network;

import com.divinity.hmedia.rgrbillionaire.block.be.CryptoMinerBlockEntity;
import com.divinity.hmedia.rgrbillionaire.client.gui.ConfettiGuiOverlay;
import com.divinity.hmedia.rgrbillionaire.client.gui.MoneyExplosionGuiOverlay;
import com.divinity.hmedia.rgrbillionaire.init.SoundInit;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientHandler {

    public static void startMoneyExplosionAnimation() {
        for (int i = 0; i < MoneyExplosionGuiOverlay.INSTANCES.length; i++) {
            var instance = MoneyExplosionGuiOverlay.INSTANCES[i];
            if (instance != null) {
                if (instance.isEnabled()) {
                    if (i == MoneyExplosionGuiOverlay.INSTANCES.length - 1) {
                        instance = MoneyExplosionGuiOverlay.INSTANCES[0];
                        instance.setStartTime(Util.getMillis());
                        instance.setEnabled(true);
                        break;
                    }
                }
                else {
                    instance.setStartTime(Util.getMillis());
                    instance.setEnabled(true);
                    break;
                }
            }
        }
    }
    public static void startConfettiAnimation() {
        ConfettiGuiOverlay.INSTANCE.setStartTime(Util.getMillis());
        ConfettiGuiOverlay.INSTANCE.setEnabled(true);
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

    public static void stopSound() {
        Minecraft.getInstance().getSoundManager().stop();
    }
}
