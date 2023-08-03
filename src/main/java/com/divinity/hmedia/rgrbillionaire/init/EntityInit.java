package com.divinity.hmedia.rgrbillionaire.init;

import com.divinity.hmedia.rgrbillionaire.RGRBillionaire;
import com.divinity.hmedia.rgrbillionaire.entity.CurrencyProjectileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = RGRBillionaire.MODID)
public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RGRBillionaire.MODID);
    private static final List<AttributesRegister<?>> attributeSuppliers = new ArrayList<>();

    public static final RegistryObject<EntityType<CurrencyProjectileEntity>> PENNY_ENTITY = registerEntity("penny_entity", () ->
            EntityType.Builder.<CurrencyProjectileEntity>of((type, level) -> new CurrencyProjectileEntity(type, level, 8), MobCategory.MISC).sized(0.25F, 0.25F));

    public static final RegistryObject<EntityType<CurrencyProjectileEntity>> QUARTER_ENTITY = registerEntity("quarter_entity", () ->
            EntityType.Builder.<CurrencyProjectileEntity>of((type, level) -> new CurrencyProjectileEntity(type, level, 30), MobCategory.MISC).sized(0.25F, 0.25F));

    public static final RegistryObject<EntityType<CurrencyProjectileEntity>> SILVER_DOLLAR_ENTITY = registerEntity("silver_dollar_entity", () ->
            EntityType.Builder.<CurrencyProjectileEntity>of((type, level) -> new CurrencyProjectileEntity(type, level, 0, (entity, hitResult) -> {
                entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 4.0F, Level.ExplosionInteraction.TNT);
            }), MobCategory.MISC).sized(0.25F, 0.25F));

    public static final RegistryObject<EntityType<CurrencyProjectileEntity>> DOLLAR_COIN_ENTITY = registerEntity("dollar_coin_entity", () ->
            EntityType.Builder.<CurrencyProjectileEntity>of((type, level) -> new CurrencyProjectileEntity(type, level, 10, (entity, hitResult) -> {
                entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 2.0F, Level.ExplosionInteraction.TNT);
            }), MobCategory.MISC).sized(0.25F, 0.25F));

    public static final RegistryObject<EntityType<CurrencyProjectileEntity>> DOLLAR_BILL_ENTITY = registerEntity("dollar_bill_entity", () ->
            EntityType.Builder.<CurrencyProjectileEntity>of((type, level) -> new CurrencyProjectileEntity(type, level, 10), MobCategory.MISC).sized(0.25F, 0.25F));


    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> supplier) {
        return ENTITIES.register(name, () -> supplier.get().build(RGRBillionaire.MODID + ":" + name));

    }

    private static <T extends LivingEntity> RegistryObject<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> supplier,
            Supplier<AttributeSupplier.Builder> attributeSupplier) {
        RegistryObject<EntityType<T>> entityTypeSupplier = registerEntity(name, supplier);
        attributeSuppliers.add(new AttributesRegister<>(entityTypeSupplier, attributeSupplier));
        return entityTypeSupplier;
    }

    @SubscribeEvent
    public static void attribs(EntityAttributeCreationEvent e) {
        attributeSuppliers.forEach(p -> e.put(p.entityTypeSupplier.get(), p.factory.get().build()));
    }

    private record AttributesRegister<E extends LivingEntity>(Supplier<EntityType<E>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> factory) {}
}
