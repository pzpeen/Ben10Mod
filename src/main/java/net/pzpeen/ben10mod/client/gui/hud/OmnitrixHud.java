package net.pzpeen.ben10mod.client.gui.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.TickEvent;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.items.custom.dna_bank.AbstractDnaBankItem;
import net.pzpeen.ben10mod.items.custom.omnitrix.AbstractOmnitrixItem;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerCapC2SPacket;
import net.pzpeen.ben10mod.powers.OmnitrixPower;
import net.pzpeen.ben10mod.races.AbstractRace;
import net.pzpeen.ben10mod.sounds.ModSounds;
import net.pzpeen.ben10mod.systems.DnaBank;
import net.pzpeen.ben10mod.systems.Playlist;
import net.pzpeen.ben10mod.utils.ModUtilities;
import org.lwjgl.glfw.GLFW;

public class OmnitrixHud {
    private static final ResourceLocation OMNITRIX_HUD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID,
            "textures/gui/hud/omnitrix_hud.png");
    public static final ResourceLocation LOCKED_ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID,
            "textures/gui/hud/locked_icon.png");

    public static final float menuInterpolationSpeed = 0.2f;
    public static float menuAnimProgress = 0.0f;
    public static float lastMenuAnimProgress = 0.0f;

    public static SimpleSoundInstance omnitrixActiveSoundInstance = new SimpleSoundInstance(
            ModSounds.OMNITRIX_ACTIVE.get().getLocation(),
            SoundSource.PLAYERS,
            1.0f,
            1.0f,
            SoundInstance.createUnseededRandom(),
            true,
            0,
            SoundInstance.Attenuation.NONE,
            0f,
            0f,
            0f,
            true
            );

    public static void renderOmnitrixHud(GuiGraphics pGuiGraphics, float pPartialTick, PowerCap pPowerCap){
        int screenWidth = pGuiGraphics.guiWidth();
        int screenHeight = pGuiGraphics.guiHeight();
        int pSelectedSlot = pPowerCap.getHudSlot();

        int guiWidth = 202;
        int guiHeight = 22;
        int slotSize = 20;

        int startX = (screenWidth - guiWidth)/2;
        int startY = screenHeight - guiHeight - 1;

        pGuiGraphics.blit(OMNITRIX_HUD_TEXTURE, startX, startY, 0, 0, guiWidth+22, guiHeight);

        ItemStack omnitrixStack = pPowerCap.getInventory().getStackInSlot(0);
        int selectedPlaylist = AbstractOmnitrixItem.getSelectedPlaylist(omnitrixStack);
        //System.out.println(AbstractOmnitrixItem.getDnaBankItem(omnitrixStack));
        DnaBank dnaBank = AbstractDnaBankItem.getDnaBank(AbstractOmnitrixItem.getDnaBankItem(omnitrixStack));
        Playlist<ResourceLocation> playlist = dnaBank.getPlaylist(selectedPlaylist);

        for(int i = 0; i < 10; i++){
            // i representa o slot real enquanto _i representa o slot da render
            int _i = i == 0 ? 9 : i - 1;

            int xSlot = startX + 3 + (_i * 20);
            int ySlot = startY + 3;

            int xRealSlot = startX + 3 + (i * 20);

            ResourceLocation alienIcon = AbstractRace.getIconOrLockedIcon(playlist.get(i));
            //System.out.println(alienIcon);

            if (pSelectedSlot == i){
                pGuiGraphics.blit(OMNITRIX_HUD_TEXTURE, xSlot-4, ySlot -3, 0, 22, 24, 24);
            }
            pGuiGraphics.blit(alienIcon, xRealSlot, ySlot, 0, 0, 16, 16, 16, 16);

        }

    }

    public static void renderHand(RenderHandEvent event, Minecraft mc, ItemStack omnitrixStack){
        event.setCanceled(true);
        if(event.getHand() == InteractionHand.OFF_HAND) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource multiBufferSource = event.getMultiBufferSource();
        int combinedLight = event.getPackedLight();

        assert mc.player != null;
        PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(mc.player);
        BlockEntityWithoutLevelRenderer omnitrixRenderer = IClientItemExtensions.of(omnitrixStack).getCustomRenderer();

        float partialTicks = event.getPartialTick();
        float smoothInterpolationProgress = Mth.lerp(partialTicks, lastMenuAnimProgress, menuAnimProgress);

        poseStack.pushPose();

        //Left hand render
        float leftHandYPos = Mth.lerp(smoothInterpolationProgress, -2.0f, -0.7f);

        poseStack.translate(-0.9f, leftHandYPos, -0.7f);
        poseStack.mulPose(Axis.XP.rotationDegrees(-35.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(-75.0f));
        poseStack.mulPose(Axis.ZN.rotationDegrees(-10));
        poseStack.mulPose(Axis.XN.rotationDegrees( 45.0f));

        playerRenderer.renderLeftHand(poseStack, multiBufferSource, combinedLight, mc.player);

        //Omnitrix render on left hand
        playerRenderer.getModel().leftArm.translateAndRotate(poseStack);
        omnitrixRenderer.renderByItem(
                omnitrixStack,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                poseStack,
                multiBufferSource,
                combinedLight,
                OverlayTexture.NO_OVERLAY
        );
        poseStack.popPose();

        //Right hand render
        mc.player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {
            int slapTick = 0;
            if(powerCap.getPower() instanceof OmnitrixPower power){
                slapTick = power.getSlapAnimationTick();
                //System.out.println("GETTING SLAP ANIMATION TICK ON 1 PERSON, TICK: " + slapTick);
            }


            poseStack.pushPose();
            float rightHandYPos = Mth.lerp(smoothInterpolationProgress, -2.0f, -0.3f);
            if(slapTick > 0){
                if(slapTick >= 4){
                    float progress = (8 - slapTick) / 4.0f;

                    poseStack.translate(0.5f - (progress * 0.15), rightHandYPos + (progress * 0.2f), -0.75f + (progress * 0.1f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(30.0f));
                    poseStack.mulPose(Axis.YP.rotationDegrees(50.0f));
                    poseStack.mulPose(Axis.ZN.rotationDegrees(-30.0f));
                    poseStack.mulPose(Axis.XN.rotationDegrees( 80.0f - (progress * 30f)));
                    poseStack.mulPose(Axis.YN.rotationDegrees(-30.0f));

                }else{
                    float progress = (3 - slapTick) / 3.0f;

                    poseStack.translate(0.35f, ((rightHandYPos +  0.2f) - (progress * 0.2f)), -0.65f - (progress * 0.1f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(30.0f));
                    poseStack.mulPose(Axis.YP.rotationDegrees(50.0f));
                    poseStack.mulPose(Axis.ZN.rotationDegrees(-30.0f));
                    poseStack.mulPose(Axis.XN.rotationDegrees( 50.0f + (progress * 30f)));
                    poseStack.mulPose(Axis.YN.rotationDegrees(-30.0f));
                }

            }else{

                poseStack.translate(0.5f, rightHandYPos, -0.75f);
                poseStack.mulPose(Axis.XP.rotationDegrees(30.0f));
                poseStack.mulPose(Axis.YP.rotationDegrees(50.0f));
                poseStack.mulPose(Axis.ZN.rotationDegrees(-30.0f));
                poseStack.mulPose(Axis.XN.rotationDegrees( 80.0f));
                poseStack.mulPose(Axis.YN.rotationDegrees(-30.0f));

            }

            playerRenderer.renderRightHand(poseStack, multiBufferSource, combinedLight, mc.player);


            poseStack.popPose();


        });


    }

    static ModUtilities.Cooldown dialCooldown = new ModUtilities.Cooldown(50);

    public static void registerNumberControl(InputEvent.Key event){


        if(event.getAction() != GLFW.GLFW_PRESS) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        mc.player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
            if (pwrCap.isHudActive()){
                for (int i = 0; i < 10; i++){
                    if(event.getKey() == GLFW.GLFW_KEY_0 + i){
                        if(pwrCap.getHudSlot() != i){
                            if(dialCooldown.isCharged()){
                                mc.player.clientLevel.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                                        ModSounds.OMNITRIX_DIAL.get(), SoundSource.PLAYERS,
                                        1.0f, 1.0f, false);
                                dialCooldown.setLastTimeUsed();
                            }

                        }
                        pwrCap.setHudSlot(i);

                        ModNetworking.sendToServer(new PowerCapC2SPacket(pwrCap.isHudActive(), pwrCap.getHudSlot()));
                        if (i == 9) return;
                        mc.options.keyHotbarSlots[i].consumeClick();

                    }
                }

            }
        });

    }

    public static void interpolateMenuAnimation(TickEvent.ClientTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;

        mc.player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
            lastMenuAnimProgress = menuAnimProgress;
            if (pwrCap.isHudActive() && menuAnimProgress < 1.0f){
                menuAnimProgress += menuInterpolationSpeed;
            } else if (!pwrCap.isHudActive() && menuAnimProgress > 0.0f) {
                menuAnimProgress -= menuInterpolationSpeed;
            }
            menuAnimProgress = Mth.clamp(menuAnimProgress, 0.0f, 1.0f);

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
                    if(dialCooldown.isCharged()){
                        mc.player.clientLevel.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                                ModSounds.OMNITRIX_DIAL.get(), SoundSource.PLAYERS,
                                1.0f, 1.0f, false);
                        dialCooldown.setLastTimeUsed();
                    }
                } else if (scrollDelta < 0) {
                    pwrCap.walkOnHudSlot(1);
                    if(dialCooldown.isCharged()){
                        mc.player.clientLevel.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                                ModSounds.OMNITRIX_DIAL.get(), SoundSource.PLAYERS,
                                1.0f, 1.0f, false);
                        dialCooldown.setLastTimeUsed();
                    }
                }
                ModNetworking.sendToServer(new PowerCapC2SPacket(pwrCap.isHudActive(), pwrCap.getHudSlot()));
            }
        });

    }

}
