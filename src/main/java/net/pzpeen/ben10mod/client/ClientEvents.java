package net.pzpeen.ben10mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
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
import net.pzpeen.ben10mod.networking.packets.UseSkillC2SPacket;
import net.pzpeen.ben10mod.networking.packets.power_animations.PowerLeftMouseC2SPacket;
import net.pzpeen.ben10mod.powers.OmnitrixPower;
import net.pzpeen.ben10mod.utils.ModTags;
import org.lwjgl.glfw.GLFW;


public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Ben10Mod.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents{

        //Handling with hold skills
        public static boolean wasHoldingSkill1 = false;
        public static boolean wasHoldingSkill2 = false;
        public static boolean wasHoldingSkill3 = false;
        public static boolean wasHoldingSkill4 = false;
        public static boolean wasHoldingSkill5 = false;

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event){
            //Omnitrix Power hud
            OmnitrixHud.blockHotbarNumberControlWhenInPowerHud(event);
            OmnitrixHud.interpolateMenuAnimation(event);

            //Handling with hold skills
            boolean isHoldingSkill1 = KeyBinds.SKILL_1.isDown();
            boolean isHoldingSkill2 = KeyBinds.SKILL_2.isDown();
            boolean isHoldingSkill3 = KeyBinds.SKILL_3.isDown();
            boolean isHoldingSkill4 = KeyBinds.SKILL_4.isDown();
            boolean isHoldingSkill5 = KeyBinds.SKILL_5.isDown();
            AbstractClientPlayer player = Minecraft.getInstance().player;
            assert player != null;

            //Skill1
            if(!wasHoldingSkill1 && isHoldingSkill1 &&
                    !(isHoldingSkill2 || isHoldingSkill3 || isHoldingSkill4 || isHoldingSkill5)){

                wasHoldingSkill1 = true;
                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(1, 'h'));
                }
            }
            if(wasHoldingSkill1 && !isHoldingSkill1){
                wasHoldingSkill1 = false;

                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(1, 'r'));
                }
            }

            //Skill2
            if(!wasHoldingSkill2 && isHoldingSkill2 &&
                    !(isHoldingSkill1 || isHoldingSkill3 || isHoldingSkill4 || isHoldingSkill5)){

                wasHoldingSkill2 = true;
                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(2, 'h'));
                }
            }
            if(wasHoldingSkill2 && !isHoldingSkill2){
                wasHoldingSkill2 = false;

                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(2, 'r'));
                }
            }

            //Skill3
            if(!wasHoldingSkill3 && isHoldingSkill3 &&
                    !(isHoldingSkill1 || isHoldingSkill2 || isHoldingSkill4 || isHoldingSkill5)){
                wasHoldingSkill3 = true;
                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(3, 'h'));
                }
            }
            if(wasHoldingSkill3 && !isHoldingSkill3){
                wasHoldingSkill3 = false;
                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(3, 'r'));
                }
            }

            //Skill4
            if(!wasHoldingSkill4 && isHoldingSkill4 &&
                    !(isHoldingSkill1 || isHoldingSkill2 || isHoldingSkill3 || isHoldingSkill5)){

                wasHoldingSkill4 = true;
                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(4, 'h'));
                }
            }
            if(wasHoldingSkill4 && !isHoldingSkill4){
                wasHoldingSkill4 = false;

                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(4, 'r'));
                }
            }

            //Skill5
            if(!wasHoldingSkill5 && isHoldingSkill5 &&
                    !(isHoldingSkill1 || isHoldingSkill2 || isHoldingSkill3 || isHoldingSkill4)){

                wasHoldingSkill5 = true;
                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(5, 'h'));
                }
            }
            if(wasHoldingSkill5 && !isHoldingSkill5){
                wasHoldingSkill5 = false;

                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    ModNetworking.sendToServer(new UseSkillC2SPacket(5, 'r'));
                }
            }

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
                if(raceCap.getRace().getSkill1() != null){
                    if(raceCap.getRace().getSkill1().getCooldown().isActive()){
                        GenericHud.renderSkillCooldown(event.getGuiGraphics(), 0,
                                raceCap.getRace().getSkill1());
                    }
                }
                if(raceCap.getRace().getSkill2() != null){
                    if(raceCap.getRace().getSkill2().getCooldown().isActive()){
                        GenericHud.renderSkillCooldown(event.getGuiGraphics(), 1,
                                raceCap.getRace().getSkill2());
                    }
                }
                if(raceCap.getRace().getSkill3() != null){
                    if(raceCap.getRace().getSkill3().getCooldown().isActive()){
                        GenericHud.renderSkillCooldown(event.getGuiGraphics(), 2,
                                raceCap.getRace().getSkill3());
                    }
                }
                if(raceCap.getRace().getSkill4() != null){
                    if(raceCap.getRace().getSkill4().getCooldown().isActive()){
                        GenericHud.renderSkillCooldown(event.getGuiGraphics(), 3,
                                raceCap.getRace().getSkill4());
                    }
                }
                if(raceCap.getRace().getSkill5() != null){
                    if(raceCap.getRace().getSkill5().getCooldown().isActive()){
                        GenericHud.renderSkillCooldown(event.getGuiGraphics(), 4,
                                raceCap.getRace().getSkill5());
                    }
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
