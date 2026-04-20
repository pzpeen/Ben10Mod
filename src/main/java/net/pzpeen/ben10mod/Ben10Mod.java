package net.pzpeen.ben10mod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pzpeen.ben10mod.client.gui.ModMenus;
import net.pzpeen.ben10mod.client.gui.menus.PowerInventoryScreen;
import net.pzpeen.ben10mod.items.ModCreativeTabs;
import net.pzpeen.ben10mod.items.ModItems;
import net.pzpeen.ben10mod.networking.ModNetworking;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Ben10Mod.MOD_ID)
public class Ben10Mod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "ben10mod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Ben10Mod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        GeckoLib.initialize();

        ModMenus.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModItems.register(modEventBus);


        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            ModNetworking.register();
            MenuScreens.register(ModMenus.POWER_INVENTORY_MENU.get(), PowerInventoryScreen::new);
        });

    }

}
