package net.pzpeen.ben10mod.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.entities.projectiles.FireBallEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Ben10Mod.MOD_ID);

    public static final RegistryObject<EntityType<FireBallEntity>> FIRE_BALL = ENTITIES.register("fire_ball", () ->
       EntityType.Builder.<FireBallEntity>of(FireBallEntity::new, MobCategory.MISC)
               .sized(0.5f,0.5f)
               .clientTrackingRange(4)
               .updateInterval(10)
               .fireImmune()
               .noSummon()
               .build("fire_ball")
    );



    public static void register(IEventBus eventBus){
        ENTITIES.register(eventBus);
    }

}
