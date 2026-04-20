package net.pzpeen.ben10mod.items.custom.omnitrix;

import net.minecraft.resources.ResourceLocation;
import net.pzpeen.ben10mod.Ben10Mod;
import software.bernie.geckolib.model.GeoModel;

public class OmnitrixModel extends GeoModel<OmntrixItem> {
    @Override
    public ResourceLocation getModelResource(OmntrixItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "geo/armor/omnitrix.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OmntrixItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/armor/omnitrix.png");
    }

    @Override
    public ResourceLocation getAnimationResource(OmntrixItem animatable) {
        return null;
    }
}
