package com.divinity.hmedia.rgrbillionaire.block;

import com.divinity.hmedia.rgrbillionaire.block.be.CryptoMinerBlockEntity;
import com.divinity.hmedia.rgrbillionaire.cap.GlobalLevelHolderAttacher;
import com.divinity.hmedia.rgrbillionaire.init.BlockInit;
import com.divinity.hmedia.rgrbillionaire.network.NetworkHandler;
import com.divinity.hmedia.rgrbillionaire.network.clientbound.SyncCryptoBlockPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CryptoMinerBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final Map<Item, Integer> productionIncreaseForItemMap = Map.of(
            Items.IRON_INGOT, 3,
            Items.GOLD_INGOT, 5,
            Items.DIAMOND, 7,
            Items.NETHERITE_SCRAP, 10,
            Items.DRAGON_EGG, 1000
    );

    private static final Map<Integer, Item> itemForProductionRateMap = Map.of(
            1, Items.IRON_INGOT,
            3, Items.GOLD_INGOT,
            5, Items.DIAMOND,
            7, Items.NETHERITE_SCRAP,
            10, Items.DRAGON_EGG
    );

    public CryptoMinerBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @javax.annotation.Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter pLevel, BlockPos pPos, Explosion explosion) {
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        if (entity instanceof CryptoMinerBlockEntity crypto) {
            if (crypto.amount == 1000) {
                return 3600000.0F;
            }
        }
        return super.getExplosionResistance(state, pLevel, pPos, explosion);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CryptoMinerBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide ? createTickerHelper(pBlockEntityType, BlockInit.MINER_BLOCK_ENTITY.get(), this::tick) : null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock()) && !pMovedByPiston) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof CryptoMinerBlockEntity crypto) {
                GlobalLevelHolderAttacher.getGlobalLevelCapability(pLevel).ifPresent(h -> h.decreaseProductionRate(crypto.amount));
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        else {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof CryptoMinerBlockEntity crypto) {
                if (pPlayer.isShiftKeyDown()) {
                    pPlayer.sendSystemMessage(Component.literal("Current Production Rate: ").withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("$%s/Second".formatted(crypto.amount)).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)));
                }
                else {
                    ItemStack playerStack = pPlayer.getItemInHand(pHand);
                    Item playerItem = playerStack.getItem();
                    Item requiredItem = itemForProductionRateMap.get(crypto.amount);
                    if (requiredItem != null && playerStack.is(requiredItem)) {
                        int amount = productionIncreaseForItemMap.get(playerItem);
                        GlobalLevelHolderAttacher.getGlobalLevelCapability(pLevel).ifPresent(h -> {
                            h.decreaseProductionRate(crypto.amount); // Decreasing by previous amount
                            crypto.setAmount(amount);
                            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncCryptoBlockPacket(amount, pPos));
                            h.increaseProductionRate(amount);
                        });
                        pPlayer.sendSystemMessage(Component.literal("Beep Boop... Production Rate Successfully Increased To ").withStyle(ChatFormatting.WHITE)
                                .append(Component.literal("$%s/Second".formatted(crypto.amount)).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)));
                        playerStack.shrink(1);
                        return InteractionResult.CONSUME;
                    }
                    else {
                        if (requiredItem != null) {
                            pPlayer.sendSystemMessage(Component.literal("Beep Boop... I Need 1 ").withStyle(ChatFormatting.WHITE)
                                    .append(Component.translatable(requiredItem.getName(requiredItem.getDefaultInstance()).getString()).withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                                    .append(Component.literal(" For The Next Upgrade").withStyle(ChatFormatting.WHITE)));
                        }
                        else {
                            pPlayer.sendSystemMessage(Component.literal("Beep Boop... ").withStyle(ChatFormatting.WHITE)
                                    .append(Component.literal("I Have The Maximum Possible Upgrades").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)));

                        }
                    }
                }
            }
            return InteractionResult.PASS;
        }
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof CryptoMinerBlockEntity crypto) {
                crypto.addAmount(1);
                GlobalLevelHolderAttacher.getGlobalLevelCapability(pLevel).ifPresent(h -> h.increaseProductionRate(crypto.amount));
            }
        }
    }

    private void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
    }
}
