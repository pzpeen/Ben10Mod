package net.pzpeen.ben10mod.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pzpeen.ben10mod.Ben10Mod;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ben10Mod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB =
            TABS.register("main_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetabs.ben10mod.main_tab"))
                    .icon(() -> new ItemStack(ModItems.OMNITRIX.get()))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.OMNITRIX.get());

                    }).build());

    public static void register(IEventBus eventBus){
        TABS.register(eventBus);
    }
}
