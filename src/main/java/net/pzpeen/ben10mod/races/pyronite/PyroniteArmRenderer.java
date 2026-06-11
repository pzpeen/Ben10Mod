package net.pzpeen.ben10mod.races.pyronite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.effects.ModEffects;
import net.pzpeen.ben10mod.races.AbstractRace;
import net.pzpeen.ben10mod.races.AlienArmRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PyroniteArmRenderer extends AlienArmRenderer {
    private static final ResourceLocation GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/races/pyronite/pyronite_glow.png");

    public PyroniteArmRenderer(ResourceLocation alienID) {
        super(alienID);
        this.addRenderLayer(new GeoRenderLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, AbstractRace animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if (animatable instanceof PyroniteRace pAnimatable) {
                    LocalPlayer player = Minecraft.getInstance().player;
                    if(player != null){
                        if (pAnimatable.isOnWater()){
                            return;
                        }
                    }

                    //if (pAnimatable.isOnWater()) return;

                    RenderType glowRender = RenderType.eyes(GLOW_TEXTURE);
                    VertexConsumer glowBuffer = bufferSource.getBuffer(glowRender);
                    for(GeoBone bone : bakedModel.topLevelBones()){
                        this.getRenderer().renderRecursively(
                                poseStack,
                                pAnimatable,
                                bone,
                                glowRender,
                                bufferSource,
                                glowBuffer,
                                false,
                                partialTick,
                                packedLight,
                                packedOverlay,
                                1.0f,
                                1.0f,
                                1.0f,
                                1.0f
                        );
                    }
                }


            }
        });
    }
}
