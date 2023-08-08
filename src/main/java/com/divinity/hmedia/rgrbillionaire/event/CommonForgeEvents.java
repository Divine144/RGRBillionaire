package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import com.divinity.hmedia.rgrbillionaire.init.MarkerInit;
import com.divinity.hmedia.rgrbillionaire.init.MenuInit;
import com.divinity.hmedia.rgrbillionaire.menu.MinebookMenu;
import com.divinity.hmedia.rgrbillionaire.mixin.TemplateStructurePieceAccessor;
import com.divinity.hmedia.rgrbillionaire.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.LootDesertTempleGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.LootEndShipGoal;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

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

    private static final UUID MAX_HEALTH_UUID = UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC");


    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            if (event.getItemStack().getItem() == ItemInit.MARKETPLACE.get()) {
                NetworkHooks.openScreen(serverPlayer,
                        new SimpleMenuProvider((id, inv, pl) -> new MinebookMenu(MenuInit.MINEBOOK_SCREEN.get(), id), Component.literal("How are we here")));
            }
            else if (event.getItemStack().getItem() == ItemInit.HEART.get()) {
                Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
                int amount = (int) (serverPlayer.getAttributes().getValue(Attributes.MAX_HEALTH) - 20);
                multimap.put(Attributes.MAX_HEALTH, new AttributeModifier(MAX_HEALTH_UUID, "Temp Hearts", 2 + amount, AttributeModifier.Operation.ADDITION));
                serverPlayer.getAttributes().addTransientAttributeModifiers(multimap);
                event.getItemStack().shrink(1);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        BillionaireUtils.initializeLists();
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
