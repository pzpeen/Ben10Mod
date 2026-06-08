package net.pzpeen.ben10mod.races;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderHandEvent;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.utils.ModClientUtilities;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

public abstract class AbstractRace implements GeoAnimatable {
    protected Player player;

    public AbstractRace(){}

    public void setPlayer(Player player){
        this.player = player;
    }

    public boolean isInFirstPersonView(){
        if(this.player.level().isClientSide()){
            return ModClientUtilities.isLocalPlayerFirstPerson(this.player);
        }
        return false;
    }


    public abstract ResourceLocation getID();

    public abstract ResourceLocation getIcon();

    public abstract AlienArmRenderer getAlienArmRenderer();

    public abstract GeoRenderer<? extends AbstractRace> getRenderer();

    public abstract void render(PoseStack poseStack, Player player, MultiBufferSource bufferSource, int packedLight, float partialTick);

    public void renderAlienArm(RenderHandEvent event){
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();
        float partialTick = event.getPartialTick();
        AlienArmRenderer renderer = this.getAlienArmRenderer();

        if(event.getHand() == InteractionHand.MAIN_HAND){
            if(event.getItemStack().isEmpty()){
                event.cancel();
                poseStack.pushPose();

                //Putting the arm on the right place
                float yOffSet = -1.1f * event.getEquipProgress();
                poseStack.translate(0.2f, -1.1f + yOffSet, -1.1f + (yOffSet *0.5));


                poseStack.mulPose(Axis.YP.rotationDegrees(5.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(45.0f));

                //Doing the swing animation
                boolean isMining = Minecraft.getInstance().hitResult != null && Minecraft.getInstance().hitResult.getType() == HitResult.Type.BLOCK;
                float swingProgress = player.getAttackAnim(partialTick);
                float swingSqrt = Mth.sqrt(swingProgress);
                float swingSin = Mth.sin(swingSqrt * Mth.PI);

                float maxXRotation = isMining ? -45.0f : -75.0f;

                if(swingProgress > 0.0f){
                    poseStack.mulPose(Axis.XP.rotationDegrees(swingSin * maxXRotation));
                    poseStack.mulPose(Axis.YP.rotationDegrees(swingSin * 10.0f));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(swingSin * 10.0f));
                }

                renderer.render(
                        poseStack,
                        this,
                        bufferSource,
                        null,
                        bufferSource.getBuffer(renderer.getRenderType(this, renderer.getTextureLocation(this), null, partialTick)),
                        packedLight
                );

                poseStack.popPose();
            }

        }


    }
}
