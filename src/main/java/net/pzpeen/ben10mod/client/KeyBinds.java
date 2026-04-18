package net.pzpeen.ben10mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerInventoryProvider;
import net.pzpeen.ben10mod.items.ModItems;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.OpenPowerMenuC2SPacket;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static final String MOD_KEYBIND_CATEGORY = "key.categories.ben10mod.ben10";

    public static final KeyMapping POWER_MENU =
            new KeyMapping("key.keybinds.ben10mod.power_menu", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_I, MOD_KEYBIND_CATEGORY);



    public static class KeyBindsFunctions{
        public static void register(InputEvent.Key event){
            //Power Menu
            if(KeyBinds.POWER_MENU.consumeClick()){
                /*
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("Bro vc apertou meu butao >("));
                Minecraft.getInstance().player.getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY).ifPresent(pwrInv -> {
                    System.out.println(pwrInv.getInventory().getStackInSlot(0));
                    if(pwrInv.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Usando o omnitrix pae"));
                    }else{
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Ta sem omnitrix ze"));
                    }
                });

                 */
                ModNetworking.sendToServer(new OpenPowerMenuC2SPacket());
            }

        }
    }
}
