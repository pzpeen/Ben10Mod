package net.pzpeen.ben10mod.client.render.skill_models;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class RockBoardModel extends GeoModel {
    @Override
    public ResourceLocation getModelResource(GeoAnimatable animatable) {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "geo/skills/rock_board.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable animatable) {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "textures/geo/skills/rock_board.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return null;
    }
}
