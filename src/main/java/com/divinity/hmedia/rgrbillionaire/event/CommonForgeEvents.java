package com.divinity.hmedia.rgrbillionaire.event;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.block.be.CryptoMinerBlockEntity;
import com.divinity.hmedia.rgrbillionaire.cap.BillionaireHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.cap.GlobalLevelHolder;
import com.divinity.hmedia.rgrbillionaire.cap.GlobalLevelHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.*;
import com.divinity.hmedia.rgrbillionaire.item.PortableJailItem;
import com.divinity.hmedia.rgrbillionaire.item.SwordOfTruthItem;
import com.divinity.hmedia.rgrbillionaire.menu.MinebookMenu;
import com.divinity.hmedia.rgrbillionaire.menu.TaxForumMenu;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.network.clientbound.SyncCryptoBlockPacket;
import com.divinity.hmedia.rgrbillionaire.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.LootDesertTempleGoal;
import com.divinity.hmedia.rgrbillionaire.quest.goal.LootEndShipGoal;
import com.divinity.hmedia.rgrbillionaire.util.BillionaireUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.morph.Morph;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = RGRBillionaire.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    private static final MobEffectInstance WEAKNESS = new MobEffectInstance(MobEffects.WEAKNESS, 60, 0);
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC");

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
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(RGRBillionaire.MODID)
                .then(Commands.literal("rocket")
                    .then(Commands.literal("setReadyTime")
                            .then(Commands.argument("seconds", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        int amount = IntegerArgumentType.getInteger(context, "seconds");
                                        var holder = GlobalLevelHolderAttacher.getGlobalLevelCapabilityUnwrap(context.getSource().getLevel());
                                        if (holder != null) {
                                            holder.setRocketTimer(amount);
                                        }
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("setYLevelForDestruction")
                            .then(Commands.argument("yLevel", IntegerArgumentType.integer())
                                .executes(context -> {
                                    int amount = IntegerArgumentType.getInteger(context, "yLevel");
                                    var holder = GlobalLevelHolderAttacher.getGlobalLevelCapabilityUnwrap(context.getSource().getLevel());
                                    if (holder != null) {
                                        holder.setRocketYLevel(amount);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                            )
                    )
                )
                .then(Commands.literal("money")
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> {
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    var holder = BillionaireHolderAttacher.getHolderUnwrap(EntityArgument.getPlayer(context, "player"));
                                                    if (holder != null) {
                                                        holder.addMoney(amount);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> {
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    var holder = BillionaireHolderAttacher.getHolderUnwrap(EntityArgument.getPlayer(context, "player"));
                                                    if (holder != null) {
                                                        holder.addMoney(-amount);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> {
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    var holder = BillionaireHolderAttacher.getHolderUnwrap(EntityArgument.getPlayer(context, "player"));
                                                    if (holder != null) {
                                                        holder.setMoney(amount);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("moneyBarUI")
                        .then(Commands.argument("xPos", IntegerArgumentType.integer())
                                .then(Commands.argument("yPos", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int x = IntegerArgumentType.getInteger(context, "xPos");
                                            int y = IntegerArgumentType.getInteger(context, "yPos");
                                            var holder = GlobalLevelHolderAttacher.getGlobalLevelCapabilityUnwrap(context.getSource().getLevel());
                                            if (holder != null) {
                                                holder.setMoneyBarX(x);
                                                holder.setMoneyBarY(y);
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .then(Commands.literal("toggleRocketDestruction")
                        .executes(context -> {
                            var holder = GlobalLevelHolderAttacher.getGlobalLevelCapabilityUnwrap(context.getSource().getLevel());
                            if (holder != null) {
                                holder.setDestroying(!holder.isDestroying());
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel level) {
            if (level.dimension() == Level.OVERWORLD) {
                var holder = GlobalLevelHolderAttacher.getGlobalLevelCapabilityUnwrap(level);
                if (holder != null) {
                    if (holder.isDestroying() && level.getServer().getTickCount() % 20 == 0) {
                        BlockPos.MutableBlockPos pos = holder.getInitialDestructionPos().mutable();
                        BlockPos initialBlockPos = holder.getInitialDestructionPos();
                        int radius = holder.getDestroyRadius();
                        for (int x = initialBlockPos.getX() - radius; x < initialBlockPos.getX() + radius; ++x) {
                            for (int y = initialBlockPos.getY() - 20; y < initialBlockPos.getY() + 20; ++y) {
                                for (int z = initialBlockPos.getZ() - radius; z < initialBlockPos.getZ() + radius; ++z) {
                                    pos.set(x, y, z);
                                    if (!level.getBlockState(pos).isAir()) {
                                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                        if (level.getRandom().nextIntBetweenInclusive(0, 700) == 0) {
                                            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3F, Level.ExplosionInteraction.NONE);
                                        }
                                    }
                                }
                            }
                        }
                        if (holder.getDestroyRadius() >= 100) {
                            holder.setDestroying(false);
                            holder.setInitialDestructionPos(BlockPos.ZERO);
                            holder.setDestroyRadius(10);
                            return;
                        }
                        holder.setDestroyRadius(holder.getDestroyRadius() + 5);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer player && event.phase == TickEvent.Phase.END) {
            BillionaireHolderAttacher.getHolder(player).ifPresent(h -> {
                var reachDistance = player.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = player.getAttribute(ForgeMod.ENTITY_REACH.get());
                var marker = MarkerHolderAttacher.getMarkerHolderUnwrap(player);
                if (marker != null) {
                    if (!marker.hasMarker(MarkerInit.NO_BASE_PRODUCTION_RATE.get())) {
                        if (player.tickCount % 100 == 0) {
                            h.addMoney(1);
                        }
                    }
                    if (player.tickCount % 20 == 0) {
                        if (!marker.hasMarker(MarkerInit.NO_ADDED_PRODUCTION_RATE.get())) {
                            h.addMoney(GlobalLevelHolderAttacher.getGlobalLevelCapability(player.serverLevel()).map(GlobalLevelHolder::getProductionRate).orElse(0));
                        }
                    }
                    if (marker.hasMarker(MarkerInit.TAX_FORM.get())) {
                        if (!player.hasEffect(MobEffects.WEAKNESS)) {
                            player.addEffect(WEAKNESS);
                        }
                    }
                }
                if (h.getMugEatTicks() > 0) {
                    if (reachDistance != null && attackDistance != null) {
                        if (reachDistance.getBaseValue() <= 4.5) {
                            reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 1);
                            attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue() + 1);
                        }
                    }
                    h.setMugEatTicks(h.getMugEatTicks() - 1);
                }
                else {
                    if (reachDistance != null && attackDistance != null) {
                        if (reachDistance.getBaseValue() != 4.5) {
                            reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue());
                            attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue());
                        }
                    }
                    if (marker != null) {
                        marker.removeMarker(MarkerInit.MUG_OF_COFFEE.get(), true);
                    }
                }
                if (!h.isCanDoubleJump()) {
                    if (h.getMugEatTicks() > 0 && player.onGround()) {
                        h.setCanDoubleJump(true);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onHurtEnemy(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer hurt) {
            int amount = (int) (hurt.getAttributes().getValue(Attributes.MAX_HEALTH) - hurt.getAttributes().getBaseValue(Attributes.MAX_HEALTH));
            if (amount > 0) {
                amount = (int) Mth.clamp((amount - event.getAmount()), 0, 1000);
                Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
                multimap.put(Attributes.MAX_HEALTH, new AttributeModifier(MAX_HEALTH_UUID, "Temp Hearts", amount, AttributeModifier.Operation.ADDITION));
                hurt.getAttributes().removeAttributeModifiers(multimap);
                hurt.getAttributes().addTransientAttributeModifiers(multimap);
            }
        }
        if (event.getSource().getDirectEntity() instanceof ServerPlayer player) {
            if (event.getEntity() instanceof ServerPlayer hurtPlayer) {
                var holder = MarkerHolderAttacher.getMarkerHolderUnwrap(hurtPlayer);
                if (holder != null && holder.hasMarker(MarkerInit.TAX_FORM.get())) {
                    BillionaireHolderAttacher.getHolder(hurtPlayer).ifPresent(p -> p.addMoney(-100));
                }
                else {
                    for (InteractionHand hand : InteractionHand.values()) {
                        ItemStack stack = player.getItemInHand(hand);
                        if (stack.getItem() instanceof SwordOfTruthItem item) {
                            BillionaireHolderAttacher.getHolder(hurtPlayer).ifPresent(cap -> {
                                int money = item.getStoredMoney();
                                if (money == 1000) {
                                    item.setStoredMoney(0);
                                    MarkerHolderAttacher.getMarkerHolder(hurtPlayer).ifPresent(h -> h.addMarker(MarkerInit.TAX_FORM.get(), false));
                                    hurtPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, -1, 0, false, false, false));
                                    NetworkHooks.openScreen(hurtPlayer,
                                            new SimpleMenuProvider((id, inv, pl) -> new TaxForumMenu(MenuInit.TAX_FORUM_SCREEN.get(), id), Component.empty()));
                                }
                                else {
                                    cap.addMoney(-100);
                                    item.setStoredMoney(item.getStoredMoney() + 100);
                                }
                            });
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().level().isClientSide) {
            Player player = event.getPlayer();
            BlockPos pos = event.getPos();
            Level level = player.level();
            if (level.getBlockState(pos).is(BlockInit.CRYPTO_MINER_BLOCK.get())) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof CryptoMinerBlockEntity crypto) {
                    if (crypto.amount == 1000) {
                        if (!player.isCreative()) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickChest(PlayerInteractEvent.RightClickBlock event) {
        BlockHitResult result = event.getHitVec();
        Player player = event.getEntity();
        if (result != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                var state = player.level().getBlockState(result.getBlockPos());
                if (state.is(Blocks.STONE_BUTTON)) {
                    var nextState = player.level().getBlockState(result.getBlockPos().relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite()));
                    if (nextState.is(BlockInit.UNBREAKABLE_STONE_BRICKS.get())) {
                        BlockPos pos = result.getBlockPos();
                        BlockPos facingWest = result.getBlockPos().relative(Direction.DOWN, 6).relative(Direction.NORTH, 3);
                        BlockPos facingNorth = result.getBlockPos().relative(Direction.DOWN, 6).relative(Direction.WEST, 3);
                        BlockPos facingEast = result.getBlockPos().relative(Direction.DOWN, 6).relative(Direction.NORTH, 3).relative(Direction.WEST, 6);
                        BlockPos facingSouth = result.getBlockPos().relative(Direction.DOWN, 6).relative(Direction.WEST, 3).relative(Direction.NORTH, 6);
                        switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                            case EAST -> pos = facingEast;
                            case WEST -> pos = facingWest;
                            case SOUTH -> pos = facingSouth;
                            case NORTH -> pos = facingNorth;
                        }
                        PortableJailItem.placeTemplate(serverPlayer.serverLevel(), serverPlayer, PortableJailItem.PRISON_CLOSED, pos.below(), Rotation.NONE, Mirror.NONE, 1.0F, 0);
                    }
                }
                else if (state.getBlock() instanceof AbstractChestBlock<?>) {
                    if (BillionaireUtils.playerIsInStructure(StructureType.DESERT_PYRAMID, serverPlayer)) {
                        QuestHolderAttacher.checkAllGoals(event.getEntity(), goal -> {
                            if (goal instanceof LootDesertTempleGoal lootDesertTempleGoal) {
                                lootDesertTempleGoal.addProgress(1);
                                return true;
                            }
                            return false;
                        });
                    }
                    else if (BillionaireUtils.playerIsInStructurePiece(player.blockPosition().below(), BuiltinStructures.END_CITY, "ship", serverPlayer)) {
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
    }

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
                int amount = (int) (serverPlayer.getAttributes().getValue(Attributes.MAX_HEALTH) - serverPlayer.getAttributes().getBaseValue(Attributes.MAX_HEALTH));
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

    @SubscribeEvent
    public static void onCrit(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getItemBySlot(EquipmentSlot.CHEST).is(ItemInit.GOLDEN_JETPACK.get())) {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer entity) {
            var morph = MorphHolderAttacher.getCurrentMorphUnwrap(entity);
            if (morph != null) {
                if (morph == MorphInit.TIGHT_BUDGET_TEEN.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 0, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, false, false));
                }
                else if (morph == MorphInit.MIDDLE_CLASS_MAN.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false, false));
                }
                else if (morph == MorphInit.MULTI_MILLIONAIRE.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 2, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, false, false));
                }
                else if (morph == MorphInit.THE_BILLIONAIRE.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 3, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 3, false, false, false));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {

    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getEntity() != null && event.getEntity().getLastAttacker() instanceof ServerPlayer player) {
            if (player.getItemBySlot(EquipmentSlot.CHEST).is(ItemInit.GOLDEN_JETPACK.get())) {
                BillionaireHolderAttacher.getHolder(player).ifPresent(cap -> cap.addMoney(100_000));
            }
        }
    }
}
