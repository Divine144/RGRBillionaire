package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.quest.*;
import dev._100media.hundredmediaquests.init.HMQQuestInit;
import dev._100media.hundredmediaquests.quest.QuestType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class QuestInit {
    public static final DeferredRegister<QuestType<?>> QUESTS = DeferredRegister.create(HMQQuestInit.QUESTS.getRegistryKey(), RGRBillionaire.MODID);

    public static final RegistryObject<QuestType<?>> COIN_CANNON = QUESTS.register("coin_cannon", () -> QuestType.Builder.of(CoinCannonQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> EXPLOIT_WORKING_CLASS = QUESTS.register("exploit_working_class", () -> QuestType.Builder.of(ExploitWorkingClassQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> GRAND_GIVEAWAY = QUESTS.register("grand_giveaway", () -> QuestType.Builder.of(GrandGiveawayQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> MARKET_CRASHER = QUESTS.register("market_crasher", () -> QuestType.Builder.of(MarketCrasherQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> ROCKET_TO_MARS = QUESTS.register("rocket_to_mars", () -> QuestType.Builder.of(RocketToMarsQuest::new).repeatable(false).instantTurnIn(false).build());

}
