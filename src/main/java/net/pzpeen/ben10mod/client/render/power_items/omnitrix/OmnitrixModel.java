package net.pzpeen.ben10mod.client.render.power_items.omnitrix;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.items.custom.omnitrix.OmnitrixItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class OmnitrixModel extends GeoModel<OmnitrixItem> {

    @Override
    public ResourceLocation getModelResource(OmnitrixItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "geo/armor/omnitrix.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OmnitrixItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/armor/omnitrix.png");
    }

    @Override
    public ResourceLocation getAnimationResource(OmnitrixItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "animations/omnitrix.animation.json");
    }

}
