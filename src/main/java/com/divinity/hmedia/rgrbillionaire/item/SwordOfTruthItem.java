package com.divinity.hmedia.rgrbillionaire.item;

import com.divinity.hmedia.rgrbillionaire.client.CustomRenderTypes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SwordOfTruthItem extends TieredItem {
    private int storedMoney;
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public SwordOfTruthItem(Properties pProperties) {
        super(Tiers.DIAMOND, pProperties);
        this.attackDamage = (float) 3 + Tiers.DIAMOND.getAttackDamageBonus() + 1;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4F, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public void setStoredMoney(int money) {
        this.storedMoney = money;
    }

    public int getStoredMoney() {
        return storedMoney;
    }

    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("money", storedMoney);
        return tag;
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Minecraft mc = Minecraft.getInstance();
            consumer.accept(new IClientItemExtensions() {
                private final BlockEntityWithoutLevelRenderer cachedBEWLR = new BlockEntityWithoutLevelRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels()) {
                    @Override
                    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
                        ItemRenderer renderer = mc.getItemRenderer();
                        BakedModel pModel = renderer.getModel(pStack, mc.level, null, 0);
                        for (var model : pModel.getRenderPasses(pStack, true)) {
                            for (var renderType : model.getRenderTypes(pStack, true)) {
                                // Default render layer behaviour
                                VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(pBuffer, renderType, true, pStack.hasFoil());
                                var tag = SwordOfTruthItem.this.getShareTag(pStack);
                                if (tag != null) {
                                    if (tag.getInt("money") >= 1000) {
                                        // Setting to creeper render layer
                                        vertexConsumer = VertexMultiConsumer.create(pBuffer.getBuffer(CustomRenderTypes.ENERGY_SWIRL_ITEM), pBuffer.getBuffer(renderType));
                                    }
                                }
                                renderer.renderModelLists(model, pStack, pPackedLight, pPackedOverlay, pPoseStack, vertexConsumer);
                            }
                        }
                        super.renderByItem(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
                    }
                };

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return cachedBEWLR;
                }

                @Override
                public @Nullable Font getFont(ItemStack stack, FontContext context) {
                    return IClientItemExtensions.super.getFont(stack, context);
                }
            });
        }
    }
}
