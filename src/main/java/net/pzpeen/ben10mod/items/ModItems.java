package net.pzpeen.ben10mod.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.items.custom.battery.BatteryLvl2Item;
import net.pzpeen.ben10mod.items.custom.dna_bank.CodonConnectorItem;
import net.pzpeen.ben10mod.items.custom.omni_core.OmniCoreLvl2Item;
import net.pzpeen.ben10mod.items.custom.omnitrix.OmnitrixItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Ben10Mod.MOD_ID);

    public static final RegistryObject<Item> OMNITRIX = ITEMS.register("omnitrix", () ->
            new OmnitrixItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> OMNI_CORE_LVL2 = ITEMS.register("omni_core_lvl2", () ->
            new OmniCoreLvl2Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BATTERY_LVL2 = ITEMS.register("battery_lvl2", () ->
            new BatteryLvl2Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CODON_CONNECTOR = ITEMS.register("codon_connector", () ->
            new CodonConnectorItem(new Item.Properties().stacksTo(1)));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
