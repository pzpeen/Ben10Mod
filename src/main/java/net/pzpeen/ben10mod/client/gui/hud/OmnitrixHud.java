package net.pzpeen.ben10mod.client.gui.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.TickEvent;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerCapC2SPacket;
import net.pzpeen.ben10mod.utils.ModTags;
import org.lwjgl.glfw.GLFW;

public class OmnitrixHud {
    private static final ResourceLocation OMNITRIX_HUD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID,
            "textures/gui/hud/omnitrix_hud.png");

    public static final float menuInterpolationSpeed = 0.2f;
    public static float menuAnimProgress = 0.0f;
    public static float lastMenuAnimProgress = 0.0f;

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
                ItemDisplayContext.NONE,
                poseStack,
                multiBufferSource,
                combinedLight,
                OverlayTexture.NO_OVERLAY
        );
        poseStack.popPose();

        //Right hand render
        poseStack.pushPose();

        float rightHandYPos = Mth.lerp(smoothInterpolationProgress, -2.0f, -0.3f);

        poseStack.translate(0.5f, rightHandYPos, -0.75f);
        poseStack.mulPose(Axis.XP.rotationDegrees(30.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(50.0f));
        poseStack.mulPose(Axis.ZN.rotationDegrees(-30.0f));
        poseStack.mulPose(Axis.XN.rotationDegrees( 80.0f));
        poseStack.mulPose(Axis.YN.rotationDegrees(-30.0f));

        playerRenderer.renderRightHand(poseStack, multiBufferSource, combinedLight, mc.player);


        poseStack.popPose();

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
                } else if (scrollDelta < 0) {
                    pwrCap.walkOnHudSlot(1);
                }
                ModNetworking.sendToServer(new PowerCapC2SPacket(pwrCap.isHudActive(), pwrCap.getHudSlot()));
            }
        });

    }

}
