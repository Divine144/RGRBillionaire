package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.block.CryptoMinerBlock;
import com.divinity.hmedia.rgrbillionaire.block.HundredMediaBlock;
import com.divinity.hmedia.rgrbillionaire.block.be.CryptoMinerBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RGRBillionaire.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RGRBillionaire.MODID);

    public static final RegistryObject<HundredMediaBlock> HUNDRED_MEDIA = registerBlock("hundred_media", () -> new HundredMediaBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.RED).noOcclusion().strength(1)));

    public static final RegistryObject<Block> UNBREAKABLE_STONE_BRICKS = registerBlock("stone_bricks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).isValidSpawn((q, w, e, r) -> false)));
    public static final RegistryObject<Block> UNBREAKABLE_IRON_BARS = registerBlock("iron_bars", () ->  new IronBarsBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> CRYPTO_MINER_BLOCK = registerBlock("crypto_miner", () -> new CryptoMinerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).noOcclusion().isValidSpawn((a, b, c, d) -> false)));
    public static final RegistryObject<BlockEntityType<CryptoMinerBlockEntity>> MINER_BLOCK_ENTITY = BLOCK_ENTITIES.register("miner_block_entity", () -> BlockEntityType.Builder.of(CryptoMinerBlockEntity::new, CRYPTO_MINER_BLOCK.get()).build(null));


    protected static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, b -> () -> new BlockItem(b.get(), ItemInit.getItemProperties()));
    }

    protected static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Function<RegistryObject<T>, Supplier<? extends BlockItem>> item) {
        var reg = BLOCKS.register(name, block);
        ItemInit.ITEMS.register(name, () -> item.apply(reg).get());
        return reg;
    }
}
