package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.init.ItemInit;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public class PortableJailItem extends Item {

    public static final ResourceLocation PRISON_OPEN = new ResourceLocation(RGRBillionaire.MODID, "open_prison");
    public static final ResourceLocation PRISON_CLOSED = new ResourceLocation(RGRBillionaire.MODID, "closed_prison");

    public PortableJailItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        BlockPos blockpos = pContext.getClickedPos().relative(pContext.getClickedFace());
        if (pContext.getClickedFace() == Direction.UP) {
            if (player instanceof ServerPlayer player1) {
                placeTemplate(player1.serverLevel(), player1, PRISON_OPEN, blockpos.below(), Rotation.NONE, Mirror.NONE, 1.0F, 0);
                ItemEntity entity = new ItemEntity(player1.serverLevel(), blockpos.getX(), blockpos.getY(), blockpos.getZ(), ItemInit.BATON.get().getDefaultInstance());
                entity.setDefaultPickUpDelay();
                player1.serverLevel().addFreshEntity(entity);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public static void placeTemplate(ServerLevel serverlevel, ServerPlayer player, ResourceLocation pTemplate, BlockPos pPos, Rotation pRotation, Mirror pMirror, float pIntegrity, int pSeed) {
        StructureTemplateManager structuretemplatemanager = serverlevel.getStructureManager();
        Optional<StructureTemplate> optional;
        try {
            optional = structuretemplatemanager.get(pTemplate);
        }
        catch (ResourceLocationException resourcelocationexception) {
            return;
        }

        if (optional.isEmpty()) {
            return;
        }
        else {
            StructureTemplate structuretemplate = optional.get();
            if (checkLoaded(serverlevel, new ChunkPos(pPos), new ChunkPos(pPos.offset(structuretemplate.getSize())))) {
                StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setMirror(pMirror).setRotation(pRotation);
                if (pIntegrity < 1.0F) {
                    structureplacesettings.clearProcessors().addProcessor(new BlockRotProcessor(pIntegrity)).setRandom(StructureBlockEntity.createRandom((long) pSeed));
                }
                boolean flag = structuretemplate.placeInWorld(serverlevel, pPos, pPos, structureplacesettings, StructureBlockEntity.createRandom((long) pSeed), 2);
                if (!flag) {
                    return;
                }
                else {}
            }
        }
    }

    private static boolean checkLoaded(ServerLevel pLevel, ChunkPos pStart, ChunkPos pEnd)  {
        return ChunkPos.rangeClosed(pStart, pEnd).noneMatch((p_214542_) -> !pLevel.isLoaded(p_214542_.getWorldPosition()));
    }
}
