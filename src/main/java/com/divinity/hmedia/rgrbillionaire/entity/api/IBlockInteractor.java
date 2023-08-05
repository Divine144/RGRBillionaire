package com.divinity.hmedia.rgrbillionaire.entity.api;

public interface IBlockInteractor {
    int getInteractionTicks();

    void setInteractionTicks(int ticks);

    boolean getMining();

    void setMining(boolean mining);
}
