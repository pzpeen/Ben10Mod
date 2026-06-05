package net.pzpeen.ben10mod.races.pyronite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.client.render.ModRenderTypes;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.ArrayList;
import java.util.List;

public class PyroniteRenderer implements GeoRenderer<PyroniteRace> {

    private final GeoModel<PyroniteRace> model;
    private PyroniteRace currentAnimatable;
    private Player currentPlayer;
    private final List<GeoRenderLayer<PyroniteRace>> renderLayers = new ArrayList<>();
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/races/pyronite/pyronite.png");
    private static final ResourceLocation FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/races/pyronite/pyronite_fire.png");
    private static final ResourceLocation GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/races/pyronite/pyronite_glow.png");
    private static final ResourceLocation OMNITRIX_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/races/pyronite/pyronite_omnitrix.png");



    public PyroniteRenderer() {
        this.model = new PyroniteModel();
        //ItemInHand
        this.renderLayers.add(new BlockAndItemGeoLayer<>(this){
            @Override
            protected @Nullable ItemStack getStackForBone(GeoBone bone, PyroniteRace animatable) {
                if(Minecraft.getInstance().level != null){
                    if(bone.getName().equals("rightHand")){
                        return currentPlayer.getMainHandItem();
                    }
                    if(bone.getName().equals("leftHand")){
                        return currentPlayer.getOffhandItem();
                    }
                }
                return ItemStack.EMPTY;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, PyroniteRace animatable) {
                if(bone.getName().equals("rightHand")){
                    return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                }
                if(bone.getName().equals("leftHand")){
                    return ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
                }
                return ItemDisplayContext.NONE;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, PyroniteRace animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.pushPose();
                if(bone.getName().equals("rightHand")){
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
                } else if (bone.getName().equals("leftHand")) {
                    poseStack.translate(0.0, -0.1, -0.2);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-30.0f));
                    //poseStack.mulPose(Axis.ZN.rotationDegrees(-90.0f));
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);

                poseStack.popPose();
            }
        });


        //FIRE HAIR WITH GLOW
        this.renderLayers.add(new GeoRenderLayer<PyroniteRace>(this) {
            @Override
            public void render(PoseStack poseStack, PyroniteRace animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if(animatable.isOnWater()) return;


                RenderType fireRender1 = RenderType.entityCutoutNoCull(FIRE_TEXTURE);

                this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, fireRender1,
                        bufferSource.getBuffer(fireRender1), partialTick, LightTexture.FULL_BRIGHT, packedOverlay, 1f, 1f, 1f, 1f);

                if(bufferSource instanceof MultiBufferSource.BufferSource immediate){
                    immediate.endBatch(fireRender1);
                }


                RenderType fireRender2 = ModRenderTypes.pyroniteHeadFireGlow(FIRE_TEXTURE);
                this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, fireRender2,
                        bufferSource.getBuffer(fireRender2), partialTick, packedLight, packedOverlay, 1f, 1f, 1f, 1f);

                if(bufferSource instanceof MultiBufferSource.BufferSource immediate){
                    immediate.endBatch(fireRender2);
                }

            }
        });

        //GLOW EFFECT
        this.renderLayers.add(new GeoRenderLayer<PyroniteRace>(this) {
            @Override
            public void render(PoseStack poseStack, PyroniteRace animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if(animatable.isOnWater()) return;

                RenderType glowRender = RenderType.eyes(GLOW_TEXTURE);
                this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, glowRender,
                        bufferSource.getBuffer(glowRender), partialTick, packedLight, packedOverlay, 1f, 1f, 1f,1f);
            }
        });

        //OMNITRIX LAYER
        this.renderLayers.add(new GeoRenderLayer<PyroniteRace>(this) {
            @Override
            public void render(PoseStack poseStack, PyroniteRace animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                RenderType omnitrixRender = RenderType.entityTranslucent(OMNITRIX_TEXTURE);

                this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, omnitrixRender,
                        bufferSource.getBuffer(omnitrixRender), partialTick, packedLight, packedOverlay, 1f, 1f, 1f,1f);
            }
        });

    }

    public void renderAlien(PyroniteRace animatable, Player player, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick){
        System.out.println("TENTANDO RENDERIZAR CHAMA");
        //Dizendo o quem é o animatable (o pyronite race) e o player
        this.currentAnimatable = animatable;
        this.currentPlayer = player;



        //Tratando as animações
        BakedGeoModel bakedGeoModel = this.getGeoModel().getBakedGeoModel(this.getGeoModel().getModelResource(animatable).toString());

        float limbSwing = player.walkAnimation.position();
        float limbSwingAmount = player.walkAnimation.speed();
        boolean isMoving = limbSwingAmount > 0.01F;

        AnimationState<PyroniteRace> animationState = new AnimationState<>(animatable, limbSwing, limbSwingAmount, partialTick, isMoving);

        float headPitch = Mth.rotLerp(partialTick, currentPlayer.xRotO, currentPlayer.getXRot());
        float headYaw = Mth.rotLerp(partialTick, currentPlayer.yHeadRotO, currentPlayer.yHeadRot);
        float bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);

        float netHeadYaw = headYaw - bodyYaw;

        netHeadYaw = Mth.wrapDegrees(netHeadYaw);

        if (player.isSleeping()){
            netHeadYaw = 0.0f;
            headPitch = 0.0f;
        }else{
            netHeadYaw = Mth.clamp(netHeadYaw, -50.0f, 50.0f);
        }

        EntityModelData modelData = new EntityModelData(false, false, netHeadYaw, headPitch);

        animationState.setData(DataTickets.ENTITY_MODEL_DATA, modelData);
        animationState.setData(DataTickets.ENTITY, player);

        this.getGeoModel().handleAnimations(animatable, player.getId(), animationState);

        //Começando a renderizar
        poseStack.pushPose();

        //Tratando o yaw do modelo
        if(player.isSleeping()){
            Direction bedDir = player.getBedOrientation();
            if(bedDir != null) {
                poseStack.mulPose(Axis.YP.rotationDegrees(bedDir.toYRot()));
            }
        }else{
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - bodyYaw));
        }

        //Inclinando corpo se estiver morrendo
        if(player.isDeadOrDying()){
            float deathTime = player.deathTime + partialTick;
            if(deathTime > 20.0f) deathTime = 20.0f;
            float progress = deathTime / 20.0f;

            float angle = progress*90.0f;
            poseStack.mulPose(Axis.ZN.rotationDegrees(angle));

        }

        //Atualizando frames da textura animada
        this.updateAnimatedTextureFrame(animatable);

        //Render em si
        RenderType renderType = this.getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);
        VertexConsumer vertexBuffer = bufferSource.getBuffer(renderType);

        render(animatable, poseStack, bufferSource, packedLight, partialTick, bakedGeoModel, renderType, vertexBuffer);


        poseStack.popPose();
    }

    private void render(PyroniteRace animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, BakedGeoModel bakedGeoModel, RenderType renderType, VertexConsumer vertexBuffer) {
        if (!firePreRenderEvent(poseStack, bakedGeoModel, bufferSource, partialTick, packedLight)) {

            for (GeoBone bone : bakedGeoModel.topLevelBones()) {
                this.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, vertexBuffer,
                        false, partialTick, packedLight,
                        getPackedOverlay(animatable, 0, partialTick),
                        1.0F, 1.0F, 1.0F, 1.0F);
            }

            //Render layers
            for (GeoRenderLayer<PyroniteRace> layer : this.getRenderLayers()) {
                layer.render(poseStack, animatable, bakedGeoModel, renderType, bufferSource, vertexBuffer, partialTick, packedLight, OverlayTexture.NO_OVERLAY);
            }

            firePostRenderEvent(poseStack, bakedGeoModel, bufferSource, partialTick, packedLight);
        }
    }


    @Override
    public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return false;
    }

    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {

    }

    @Override
    public void fireCompileRenderLayersEvent() {

    }

    @Override
    public void updateAnimatedTextureFrame(PyroniteRace animatable) {
        AnimatableTexture.setAndUpdate(FIRE_TEXTURE);
    }

    @Override
    public ResourceLocation getTextureLocation(PyroniteRace animatable) {
        return DEFAULT_TEXTURE;
    }

    @Override
    public List<GeoRenderLayer<PyroniteRace>> getRenderLayers() {
        return renderLayers;
    }

    @Override
    public GeoModel<PyroniteRace> getGeoModel() {
        return this.model;
    }

    @Override
    public PyroniteRace getAnimatable() {
        return this.currentAnimatable;
    }

    @Override
    public int getPackedOverlay(PyroniteRace animatable, float u, float partialTick) {
        if(currentPlayer != null && (currentPlayer.hurtTime > 0 || currentPlayer.deathTime > 0)){
            float hurtPercent = (((float) currentPlayer.hurtTime - partialTick)/ 10.0f);
            if(hurtPercent < 0.0f) hurtPercent = 0.0f;

            if(currentPlayer.isDeadOrDying()){
                return OverlayTexture.pack(16, 3);
            }
            return OverlayTexture.pack(OverlayTexture.u(hurtPercent), OverlayTexture.v(hurtPercent > 0.0));

        }

        return GeoRenderer.super.getPackedOverlay(animatable, u, partialTick);
    }

}
