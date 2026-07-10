package net.pzpeen.ben10mod.entities.projectiles;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireBallModel extends GeoModel<FireBallEntity> {
    @Override
    public ResourceLocation getModelResource(FireBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "geo/entities/projectiles/fire_ball.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "textures/geo/entities/projectiles/fire_ball.png");
    }

    @Override
    public RenderType getRenderType(FireBallEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public ResourceLocation getAnimationResource(FireBallEntity animatable) {
        return null;
    }
}
