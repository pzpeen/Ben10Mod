package net.pzpeen.ben10mod.races;

import net.minecraft.resources.ResourceLocation;
import net.pzpeen.ben10mod.Ben10Mod;
import software.bernie.geckolib.model.GeoModel;

public class AlienArmModel extends GeoModel<AbstractRace> {
    private final ResourceLocation modelLocation;
    private final ResourceLocation textureLocation;
    private final ResourceLocation animationLocation;

    public AlienArmModel(ResourceLocation alienID){
        String path = alienID.getPath();
        this.modelLocation = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "geo/races/"+ path +"/"+ path +"_arm.geo.json");
        this.textureLocation = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/geo/races/"+ path +"/"+ path +".png");
        this.animationLocation = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "animations/races/"+ path +"/"+ path+"_arm.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(AbstractRace animatable) {
        return this.modelLocation;
    }

    @Override
    public ResourceLocation getTextureResource(AbstractRace animatable) {
        return this.textureLocation;
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractRace animatable) {
        return this.animationLocation;
    }
}
