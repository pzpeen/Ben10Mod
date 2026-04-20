package net.pzpeen.ben10mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.items.ModItems;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.OpenPowerMenuC2SPacket;
import net.pzpeen.ben10mod.networking.packets.PowerCapC2SPacket;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static final String MOD_KEYBIND_CATEGORY = "key.categories.ben10mod.ben10";

    public static final KeyMapping POWER_MENU =
            new KeyMapping("key.keybinds.ben10mod.power_menu", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_I, MOD_KEYBIND_CATEGORY);

    public static final KeyMapping ACTIVATE_POWER =
            new KeyMapping("key.keybinds.ben10mod.activate_power", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R, MOD_KEYBIND_CATEGORY);


    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(POWER_MENU);
        event.register(ACTIVATE_POWER);
    }

    public static void registerFunctions(InputEvent.Key event){
        //Power Menu
        if(KeyBinds.POWER_MENU.consumeClick()){
            ModNetworking.sendToServer(new OpenPowerMenuC2SPacket());
        }

        //Activate Power
        if(KeyBinds.ACTIVATE_POWER.consumeClick()){
            AbstractClientPlayer player = Minecraft.getInstance().player;
            assert player != null;
            //player.sendSystemMessage(Component.literal("Activate power"));
            if(event.getAction() == GLFW.GLFW_PRESS){
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    if (pwrCap.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){
                        pwrCap.setHudActive(!pwrCap.isHudActive());

                        ModNetworking.sendToServer(new PowerCapC2SPacket(pwrCap.isHudActive(), pwrCap.getHudSlot()));
                    }

                });
            }


        }

    }

}
