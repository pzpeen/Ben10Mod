package net.pzpeen.ben10mod.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pzpeen.ben10mod.Ben10Mod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Ben10Mod.MOD_ID);

    public static final RegistryObject<SoundEvent> OMNITRIX_ACTIVATE = registerSoundEvent("omnitrix_activate");

    public static final RegistryObject<SoundEvent> OMNITRIX_DEACTIVATE = registerSoundEvent("omnitrix_deactivate");

    public static final RegistryObject<SoundEvent> OMNITRIX_DIAL = registerSoundEvent("omnitrix_dial");

    public static final RegistryObject<SoundEvent> OMNITRIX_ACTIVE = registerSoundEvent("omnitrix_active");

    public static final RegistryObject<SoundEvent> OMNITRIX_PUTTING = registerSoundEvent("omnitrix_putting");

    public static final RegistryObject<SoundEvent> OMNITRIX_TRANSFORMATION_FAIL =
            registerSoundEvent("omnitrix_transformation_fail");

    public static final RegistryObject<SoundEvent> OMNITRIX_TRANSFORMATION = registerSoundEvent("omnitrix_transformation");

    public static final RegistryObject<SoundEvent> FLAMETHROWER = registerSoundEvent("flamethrower");


    public static RegistryObject<SoundEvent> registerSoundEvent(String name){
        return SOUND_EVENTS.register(name, () ->
                SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
