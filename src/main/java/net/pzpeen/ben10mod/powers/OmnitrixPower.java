package net.pzpeen.ben10mod.powers;

import com.eliotlash.mclib.math.functions.classic.Pow;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.client.gui.hud.OmnitrixHud;
import net.pzpeen.ben10mod.client.render.ModRenderTypes;
import net.pzpeen.ben10mod.items.custom.omnitrix.AbstractOmnitrixItem;
import net.pzpeen.ben10mod.utils.ModUtilities;

public class OmnitrixPower extends BasePower{
    public static ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "omnitrix_power");
    private final ModUtilities.TickTimer slapAnimation = new ModUtilities.TickTimer();
    private final ModUtilities.TickTimer brightAnimation = new ModUtilities.TickTimer();
    private BlockState brightColor = Blocks.LIME_CONCRETE.defaultBlockState();

    @Override
    public ResourceLocation getPowerID() {
        return id;
    }

    @Override
    public void tick(Player player) {
        player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {
            //System.out.println("IS ON POWER TICK METHOD");
            this.slapAnimation.tick();
            this.brightAnimation.tick();

            if(slapAnimation.getRemainingTicks() == 0){
                powerCap.setHudActive(false);
                if(player.level().isClientSide()){
                    Minecraft.getInstance().getSoundManager().stop(OmnitrixHud.omnitrixActiveSoundInstance);
                }
                //Call transform func
                int slot = powerCap.getHudSlot() == 0 ? 9 : powerCap.getHudSlot() - 1;
                AbstractOmnitrixItem.transform(powerCap.getInventory().getStackInSlot(0), player, slot);

            }

        });


    }

    @Override
    public void onHudRightClick() {
        this.startSlapAnimationTick(); //Play the slap animation
    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }

    public void startSlapAnimationTick() {
        this.slapAnimation.start(8);
    }

    public int getSlapAnimationTick() {
        return Math.max(slapAnimation.getRemainingTicks(), 0);
    }

    public void startBrightAnimationTick(){
        this.brightAnimation.start(10);
    }

    public int getBrightAnimationTick(){
        return Math.max(brightAnimation.getRemainingTicks(), 0);
    }

    public BlockState getBrightColor() {
        return brightColor;
    }

    public void setBrightColor(BlockState brightColor) {
        this.brightColor = brightColor;
    }

    public static void processBrightAnim(RenderPlayerEvent.Pre event){
        Player player = event.getEntity();

        player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {
            if(powerCap.getPower() instanceof OmnitrixPower power){
                int brightTick = power.getBrightAnimationTick();

                //System.out.println("BRIGHT ANIM TICK ON CLIENT: "+ brightTick);

                if(brightTick > 0){
                    PoseStack poseStack = event.getPoseStack();
                    MultiBufferSource bufferSource = event.getMultiBufferSource();
                    float time = player.tickCount + event.getPartialTick();

                    poseStack.pushPose();
                    poseStack.translate(0f, player.getBoundingBox().getYsize()/2f, 0f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(time * 10f));

                    BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
                    BlockState colorBright = power.getBrightColor();
                    BakedModel colorBrightModel = blockRenderer.getBlockModel(colorBright);

                    ResourceLocation blockAtlas = InventoryMenu.BLOCK_ATLAS;
                    RenderType renderType = ModRenderTypes.neonOnTop(blockAtlas);
                    VertexConsumer buffer = bufferSource.getBuffer(renderType);

                    for(int i = 0; i < 3; i++){
                        poseStack.pushPose();

                        poseStack.mulPose(Axis.YP.rotationDegrees(i * 120f));
                        //poseStack.translate(0d, 0d, 0d);
                        poseStack.mulPose(Axis.XN.rotationDegrees(time * 15f));
                        poseStack.mulPose(Axis.ZN.rotationDegrees(time * 20f));

                        poseStack.scale(3f, 3f, 3f);
                        poseStack.translate(-0.5f, -0.5f, -0.5f);

                        blockRenderer.getModelRenderer().renderModel(
                                poseStack.last(),
                                buffer,
                                colorBright,
                                colorBrightModel,
                                1.0f, 1.0f, 1.0f,
                                LightTexture.FULL_BRIGHT,
                                OverlayTexture.NO_OVERLAY,
                                ModelData.EMPTY,
                                renderType
                        );

                        poseStack.popPose();

                    }

                    poseStack.popPose();

                }



            }

        });

    }

    public static void process1stPersonBrightAnim(RenderHandEvent event){
        Player player = Minecraft.getInstance().player;
        if(player != null){
            player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {
                if(powerCap.getPower() instanceof OmnitrixPower power){
                    int brightTick = power.getBrightAnimationTick();
                    if(brightTick > 0){
                        PoseStack poseStack = event.getPoseStack();
                        MultiBufferSource bufferSource = event.getMultiBufferSource();

                        poseStack.pushPose();

                        poseStack.translate(-1.5f, -0.1f, -0.2f);
                        poseStack.scale(3f, 3f, 3f);

                        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
                        BlockState colorBright = power.getBrightColor();
                        BakedModel colorBrightModel = blockRenderer.getBlockModel(colorBright);

                        ResourceLocation blockAtlas = InventoryMenu.BLOCK_ATLAS;
                        RenderType renderType = ModRenderTypes.neonOnTop(blockAtlas);
                        VertexConsumer buffer = bufferSource.getBuffer(renderType);

                        blockRenderer.getModelRenderer().renderModel(
                                poseStack.last(),
                                buffer,
                                colorBright,
                                colorBrightModel,
                                1.0f, 1.0f, 1.0f,
                                LightTexture.FULL_BRIGHT,
                                OverlayTexture.NO_OVERLAY,
                                ModelData.EMPTY,
                                renderType
                        );

                        poseStack.popPose();

                    }


                }

            });


        }


    }


}
