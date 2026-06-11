package net.pzpeen.ben10mod.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.effects.custom.WetEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Ben10Mod.MOD_ID);

    public static final RegistryObject<MobEffect> WET = EFFECTS.register("wet", WetEffect::new);

    public static void register(IEventBus bus){
        EFFECTS.register(bus);
    }
}
