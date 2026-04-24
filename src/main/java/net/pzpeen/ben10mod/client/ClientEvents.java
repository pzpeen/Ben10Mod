package net.pzpeen.ben10mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.client.gui.hud.OmnitrixHud;
import net.pzpeen.ben10mod.client.render.layers.WristPlayerRenderLayer;
import net.pzpeen.ben10mod.items.ModItems;
import net.pzpeen.ben10mod.utils.ModTags;


public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Ben10Mod.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents{

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event){
            //Omnitrix Power hud
            OmnitrixHud.blockHotbarNumberControlWhenInPowerHud(event);
            OmnitrixHud.interpolateMenuAnimation(event);

        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event){
            KeyBinds.registerFunctions(event);
            OmnitrixHud.registerNumberControl(event);
        }

        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event){
            OmnitrixHud.registerMouseScrollControl(event);
        }

        @SubscribeEvent
        public static void onRenderHud(RenderGuiOverlayEvent.Pre event){
            Player player = Minecraft.getInstance().player;

            if (player == null) return;

            player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((pwrCap) -> {
                if (pwrCap.isHudActive()){

                    if(event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())){

                        event.setCanceled(true);
                        if(pwrCap.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){
                            OmnitrixHud.renderOmnitrixHud(event.getGuiGraphics(), event.getPartialTick(), pwrCap.getHudSlot());
                        }

                    }

                }
            });

        }

        @SubscribeEvent
        public static void onRenderHand(RenderHandEvent event){
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player == null) return;

            player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                if (pwrCap.getInventory().getStackInSlot(0).is(ModTags.Items.POWER_ITEMS)){
                    if(pwrCap.isHudActive() || OmnitrixHud.menuAnimProgress > 0.0f){
                        if (pwrCap.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){
                            OmnitrixHud.renderHand(event, mc, pwrCap.getInventory().getStackInSlot(0));
                        }

                    }
                }else {
                    OmnitrixHud.lastMenuAnimProgress = 0.0f;
                    OmnitrixHud.menuAnimProgress = 0.0f;
                }

            });

        }

    }

    @Mod.EventBusSubscriber(modid = Ben10Mod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents{

        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event){
            PlayerRenderer defaultRenderer = event.getPlayerSkin("default");
            if(defaultRenderer != null){
                defaultRenderer.addLayer(new WristPlayerRenderLayer(defaultRenderer));
            }

            PlayerRenderer slimRenderer = event.getPlayerSkin("slim");
            if(slimRenderer != null){
                slimRenderer.addLayer(new WristPlayerRenderLayer(slimRenderer));
            }
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event){
            KeyBinds.registerKeys(event);
        }

    }

}
