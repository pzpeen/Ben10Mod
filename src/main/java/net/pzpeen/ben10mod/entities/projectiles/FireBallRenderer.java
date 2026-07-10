package net.pzpeen.ben10mod.entities.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FireBallRenderer extends GeoEntityRenderer<FireBallEntity> {

    public FireBallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireBallModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
