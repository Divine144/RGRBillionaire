package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.cap.MoneyHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.entity.special.DummyMerchant;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.init.MarkerInit;
import com.divinity.hmedia.rgrbillionaire.menu.ButlerInventoryMenu;
import com.divinity.hmedia.rgrbillionaire.menu.MarketplaceMenu;
import com.divinity.hmedia.rgrbillionaire.mixin.TemplateStructurePieceAccessor;
import com.divinity.hmedia.rgrbillionaire.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.LootDesertTempleGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.LootEndShipGoal;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

@Mod.EventBusSubscriber(modid = RGRBillionaire.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    @SubscribeEvent
    public static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        String advancementID = event.getAdvancement().getId().toString();
        QuestHolderAttacher.checkAllGoals(event.getEntity(), goal -> {
            if (goal instanceof AquireAdvancementGoal advancementGoal) {
                if (advancementID.contains(advancementGoal.getAdvancementID())) {
                    advancementGoal.addProgress(1);
                    return true;
                }
            }
            return false;
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer player && event.phase == TickEvent.Phase.END) {
            BillionaireHolderAttacher.getHolder(player).ifPresent(h -> {
                var marker = MarkerHolderAttacher.getMarkerHolderUnwrap(player);
                if (marker != null && !marker.hasMarker(MarkerInit.NO_BASE_PRODUCTION_RATE.get())) {
                    if (player.tickCount % 100 == 0) {
                        h.addMoney(1);
                    }
                }
                if (player.tickCount % 20 == 0) {
                    h.addMoney(h.getProductionRate());
                }
            });
        }
    }

    @SubscribeEvent
    public static void onRightClickChest(PlayerInteractEvent.RightClickBlock event) {
        BlockHitResult result = event.getHitVec();
        Player player = event.getEntity();
        if (result != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                if (playerIsInStructure(BuiltinStructures.DESERT_PYRAMID, serverPlayer)) {
                    QuestHolderAttacher.checkAllGoals(event.getEntity(), goal -> {
                        if (goal instanceof LootDesertTempleGoal lootDesertTempleGoal) {
                            lootDesertTempleGoal.addProgress(1);
                            return true;
                        }
                        return false;
                    });
                }
                else if (playerIsInStructurePiece(player.blockPosition().below(), getStructureOfType(BuiltinStructures.END_CITY, serverPlayer), "ship")) {
                    QuestHolderAttacher.checkAllGoals(event.getEntity(), goal -> {
                        if (goal instanceof LootEndShipGoal endShipGoal) {
                            endShipGoal.addProgress(1);
                            return true;
                        }
                        return false;
                    });
                }
            }
        }
    }

    public static List<MerchantOffer> offers;

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            if (event.getItemStack().getItem() == ItemInit.MARKETPLACE.get()) {
                initializeLists();
                NetworkHooks.openScreen(serverPlayer,
                        new SimpleMenuProvider((id, inv, pl) -> new MarketplaceMenu(id, inv, new DummyMerchant(pl, offers)), Component.literal("How are we here")));
            }
        }
    }

    private static void initializeLists() {
        if (offers == null) {
            offers = List.of(new MerchantOffer(new ItemStack(Blocks.DIRT.asItem(), 16), BillionaireUtils.getMoneyForAmount(40), Integer.MAX_VALUE, 0, 0));
        }
    }

    private static StructureStart getStructureOfType(ResourceKey<Structure> key, ServerPlayer player) {
        return player.serverLevel().structureManager().getStructureWithPieceAt(player.blockPosition().below(), key);
    }

    private static boolean playerIsInStructure(ResourceKey<Structure> key, ServerPlayer player) {
        return getStructureOfType(key, player).isValid();
    }

    private static boolean playerIsInStructurePiece(BlockPos pPos, StructureStart pStructureStart, String name) {
        for (StructurePiece structurepiece : pStructureStart.getPieces()) {
            if (structurepiece instanceof EndCityPieces.EndCityPiece endCityPiece && endCityPiece.getBoundingBox().isInside(pPos)) {
                if (endCityPiece instanceof TemplateStructurePieceAccessor accessor) {
                    if (accessor.getTemplateName().contains(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
