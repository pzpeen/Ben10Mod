package net.pzpeen.ben10mod.races;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.effects.ModEffects;
import net.pzpeen.ben10mod.utils.ModClientUtilities;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;

public abstract class AbstractRace implements GeoAnimatable {
    protected Player player;

    public AbstractRace(){}

    public void setPlayer(Player player){
        this.player = player;
    }

    public abstract ResourceLocation getID();

    public static ResourceLocation getIcon(ResourceLocation alienId){
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/races/"+alienId.getPath()+"/icon.png");
    }

    public float getCustomWidth(){return 0.6f;}

    public float getCustomHeight(){return 1.8f;}

    public float getCustomEyeHeight(){return 1.62f;}

    public float getBaseDamage(){return 1.0f;}

    public void doBareHandHit(LivingHurtEvent event){}

    public float getBaseArmor(){return 0.0f;}

    public boolean isFireResistent(){return false;}

    public boolean cannotSwim(){return false;}

    //If true he gets the wet effect when in water (getWaterEffect is called, and wet effect give a buff or debuff if isWaterWeak)
    public boolean isWaterSensible(){return false;}

    //Gives a buff or a debuff to an Alien that is water sensible (Default is debuff)
    public MobEffectInstance getWaterEffect () {
        return new MobEffectInstance(
            ModEffects.WET.get(),
            getWaterEffectDuration(),
            0,
            false,
            false
        );
    }

    public boolean isWaterWeak(){return false;}

    public boolean isWaterStrong(){return false;}

    public int getWaterEffectDuration(){return 100;}

    public boolean isInFirstPersonView(){
        if(this.player.level().isClientSide()){
            return ModClientUtilities.isLocalPlayerFirstPerson(this.player);
        }
        return false;
    }

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
