package net.pzpeen.ben10mod.races.pyronite;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.pzpeen.ben10mod.Ben10Mod;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PyroniteModel extends GeoModel<PyroniteRace> {
    @Override
    public ResourceLocation getModelResource(PyroniteRace animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "geo/races/pyronite/pyronite.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PyroniteRace animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/races/pyronite/pyronite.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PyroniteRace animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "animations/races/pyronite/pyronite.animation.json");
    }

    @Override
    public void setCustomAnimations(PyroniteRace animatable, long instanceId, AnimationState<PyroniteRace> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        EntityModelData modelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        Entity entity = animationState.getData(DataTickets.ENTITY);

        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        if(head != null){
            if(modelData != null && entity instanceof LivingEntity player){
                float headYaw = modelData.netHeadYaw() * Mth.DEG_TO_RAD;
                float headPitch = modelData.headPitch() * Mth.DEG_TO_RAD;

                head.setRotY(headYaw*-1);
                head.setRotX(headPitch*-1);
            }
        }

        CoreGeoBone rightArm = this.getAnimationProcessor().getBone("rightArm");
        CoreGeoBone leftArm = this.getAnimationProcessor().getBone("leftArm");

        if(animatable.getSkill3().isHolding() && !animatable.getSkill3().getCooldown().isActive() && rightArm != null && leftArm != null){
            if(modelData != null){
                float headPitch = modelData.headPitch() * Mth.DEG_TO_RAD;
                float headYaw = modelData.netHeadYaw() * Mth.DEG_TO_RAD;

                float rotX = -(headPitch - (90.0f * Mth.DEG_TO_RAD));

                rightArm.setRotX(rotX);
                leftArm.setRotX(rotX);

                rightArm.setRotY(0.15f - headYaw);
                leftArm.setRotY(-0.15f - headYaw);

            }
        }

    }

}
