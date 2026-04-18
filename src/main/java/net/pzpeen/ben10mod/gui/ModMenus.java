package net.pzpeen.ben10mod.gui;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.gui.menus.PowerInventoryMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MOD_MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Ben10Mod.MOD_ID);

    public static final RegistryObject<MenuType<PowerInventoryMenu>> POWER_INVENTORY_MENU =
            MOD_MENUS.register("pwr_inventory_menu", () -> IForgeMenuType.create(PowerInventoryMenu::new));

    public static void register(IEventBus eventBus){
        MOD_MENUS.register(eventBus);
    }
}
