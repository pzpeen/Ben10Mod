package net.pzpeen.ben10mod.commands;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.commands.custom.RaceSet;

@Mod.EventBusSubscriber(modid = Ben10Mod.MOD_ID)
public class ModCommands {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event){
        RaceSet.register(event.getDispatcher());
    }

}
