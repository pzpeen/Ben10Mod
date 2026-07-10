package net.pzpeen.ben10mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;
import net.pzpeen.ben10mod.client.gui.hud.GenericHud;
import net.pzpeen.ben10mod.client.gui.hud.OmnitrixHud;
import net.pzpeen.ben10mod.client.render.layers.WristPlayerRenderLayer;
import net.pzpeen.ben10mod.entities.ModEntities;
import net.pzpeen.ben10mod.entities.projectiles.FireBallRenderer;
import net.pzpeen.ben10mod.items.ModItems;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.power_animations.PowerLeftMouseC2SPacket;
import net.pzpeen.ben10mod.powers.OmnitrixPower;
import net.pzpeen.ben10mod.utils.ModTags;
import org.lwjgl.glfw.GLFW;


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
        public static void onMouseClick(InputEvent.MouseButton.Pre event){
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            //if(Minecraft.getInstance().screen.isPauseScreen())
            if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT){
                if(Minecraft.getInstance().screen != null){
                    return;
                }
                PowerCap powerCap = ((IBen10ModCapCache)player).ben10Mod$getCachedPowerCap();
                if(powerCap.getPower() instanceof OmnitrixPower power){
                    if(powerCap.isHudActive()){
                        if(power.getSlapAnimationTick() <= 0){
                            event.cancel();
                            ModNetworking.sendToServer(new PowerLeftMouseC2SPacket());
                        }

                    }

                }

                /*
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {
                    if(powerCap.getPower() instanceof OmnitrixPower power){
                        if(Minecraft.getInstance().screen != null){
                            return;
                        }
                        if(powerCap.isHudActive()){
                            if(power.getSlapAnimationTick() <= 0){
                                event.cancel();
                                ModNetworking.sendToServer(new PowerLeftMouseC2SPacket());
                            }

                        }

                    }

                });

                 */
            }

        }

        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event){
            OmnitrixHud.registerMouseScrollControl(event);
        }

        @SubscribeEvent
        public static void onRenderHud(RenderGuiOverlayEvent.Pre event){
            Player player = Minecraft.getInstance().player;

            if (player == null) return;

            RaceCap raceCap = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap();
            if(raceCap != null && raceCap.getRace() != null){
                if(raceCap.getRace().getSkill1().getCooldown().isActive()){
                    GenericHud.renderSkillCooldown(event.getGuiGraphics(), 0,
                            raceCap.getRace().getSkill1());
                }
            }

            PowerCap powerCap = ((IBen10ModCapCache)player).ben10Mod$getCachedPowerCap();
            if(powerCap.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){
                if(powerCap.isHudActive()){
                    if(event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())){
                        event.setCanceled(true);
                        OmnitrixHud.renderOmnitrixHud(event.getGuiGraphics(), event.getPartialTick(), powerCap);
                    }

                }

            }
            /*
            player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((pwrCap) -> {
                if (pwrCap.isHudActive()){

                    if(event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())){

                        event.setCanceled(true);
                        if(pwrCap.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){
                            OmnitrixHud.renderOmnitrixHud(event.getGuiGraphics(), event.getPartialTick(), pwrCap);
                        }

                    }

                }
            });

             */

        }

        @SubscribeEvent
        public static void onRenderHand(RenderHandEvent event){
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player == null) return;

            PowerCap powerCap = ((IBen10ModCapCache)player).ben10Mod$getCachedPowerCap();
            if(powerCap.getInventory().getStackInSlot(0).is(ModTags.Items.POWER_ITEMS)){
                if(powerCap.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){
                    if(powerCap.isHudActive() || OmnitrixHud.menuAnimProgress > 0.0f){
                        OmnitrixHud.renderHand(event, mc, powerCap.getInventory().getStackInSlot(0));
                    }

                }

            }else{
                OmnitrixHud.lastMenuAnimProgress = 0.0f;
                OmnitrixHud.menuAnimProgress = 0.0f;
            }

            if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace().renderAlienArm(event);
            }

            OmnitrixPower.process1stPersonBrightAnim(event);

            /*
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

            player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                if (raceCap.getRace() != null){
                    raceCap.getRace().renderAlienArm(event);
                }
            });

            OmnitrixPower.process1stPersonBrightAnim(event);

             */

        }

        @SubscribeEvent
        public static void onRenderPlayer(RenderPlayerEvent.Pre event){
            Player player = event.getEntity();

            if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                event.cancel();
                ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace()
                        .render(event.getPoseStack(), player, event.getMultiBufferSource(), event.getPackedLight(),
                                event.getPartialTick());
            }
            OmnitrixPower.processBrightAnim(event);

            /*
            player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                if (raceCap.getRace() != null){
                    event.cancel();
                    raceCap.getRace().render(event.getPoseStack(), player, event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick());
                }
                OmnitrixPower.processBrightAnim(event);
            });

             */

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

        @SubscribeEvent
        public static void onRegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event){

            event.registerEntityRenderer(ModEntities.FIRE_BALL.get(), FireBallRenderer::new);
        }

    }

}
