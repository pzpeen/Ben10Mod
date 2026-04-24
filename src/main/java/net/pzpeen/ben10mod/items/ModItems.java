package net.pzpeen.ben10mod.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.items.custom.omnitrix.OmnitrixItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Ben10Mod.MOD_ID);

    public static final RegistryObject<Item> OMNITRIX = ITEMS.register("omnitrix", () ->
            new OmnitrixItem(new Item.Properties().stacksTo(1)));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
