package com.divinity.hmedia.rgrbillionaire.network;

import com.divinity.hmedia.rgrbillionaire.client.gui.MoneyExplosionGuiOverlay;
import net.minecraft.Util;

public class ClientHandler {

    public static void startMoneyExplosionAnimation() {
        MoneyExplosionGuiOverlay.INSTANCE.setStartTime(Util.getMillis());
        MoneyExplosionGuiOverlay.INSTANCE.setEnabled(true);
    }
}
