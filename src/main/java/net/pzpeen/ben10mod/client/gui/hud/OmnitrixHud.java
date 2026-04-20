package net.pzpeen.ben10mod.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerCapC2SPacket;
import org.lwjgl.glfw.GLFW;

public class OmnitrixHud {
    private static final ResourceLocation OMNITRIX_HUD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID,
            "textures/gui/hud/omnitrix_hud.png");

    public static void renderOmnitrixHud(GuiGraphics pGuiGraphics, float pPartialTick, int pSelectedSlot){
        int screenWidth = pGuiGraphics.guiWidth();
        int screenHeight = pGuiGraphics.guiHeight();

        int guiWidth = 202;
        int guiHeight = 22;
        int slotSize = 20;

        int startX = (screenWidth - guiWidth)/2;
        int startY = screenHeight - guiHeight - 1;

        pGuiGraphics.blit(OMNITRIX_HUD_TEXTURE, startX, startY, 0, 0, guiWidth+22, guiHeight);

        for(int i = 0; i < 10; i++){
            // i representa o slot real enquanto _i representa o slot da render
            int _i = i == 0 ? 9 : i - 1;

            int xSlot = startX + 3 + (_i * 20);
            int ySlot = startY + 3;

            if (pSelectedSlot == i){
                pGuiGraphics.blit(OMNITRIX_HUD_TEXTURE, xSlot-4, ySlot -3, 0, 22, 24, 24);
            }

        }

    }

    public static void registerNumberControl(InputEvent.Key event){

        if(event.getAction() != GLFW.GLFW_PRESS) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        mc.player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
            if (pwrCap.isHudActive()){
                for (int i = 0; i < 10; i++){
                    if(event.getKey() == GLFW.GLFW_KEY_0 + i){
                        pwrCap.setHudSlot(i);
                        ModNetworking.sendToServer(new PowerCapC2SPacket(pwrCap.isHudActive(), pwrCap.getHudSlot()));
                        if (i == 9) return;
                        mc.options.keyHotbarSlots[i].consumeClick();

                    }
                }

            }
        });

    }

    public static void blockHotbarNumberControlWhenInPowerHud(TickEvent.ClientTickEvent event){
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
            if (pwrCap.isHudActive()){
                for (int i = 0; i < 9; i++){
                    while (mc.options.keyHotbarSlots[i].consumeClick()){

                    }
                }
            }
        });
    }

    public static void registerMouseScrollControl(InputEvent.MouseScrollingEvent event){
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;

        mc.player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
            if (pwrCap.isHudActive()){
                event.setCanceled(true);

                double scrollDelta = event.getScrollDelta();

                if(scrollDelta > 0){
                    pwrCap.walkOnHudSlot(-1);
                } else if (scrollDelta < 0) {
                    pwrCap.walkOnHudSlot(1);
                }
                ModNetworking.sendToServer(new PowerCapC2SPacket(pwrCap.isHudActive(), pwrCap.getHudSlot()));
            }
        });

    }

}
