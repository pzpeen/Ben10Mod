package net.pzpeen.ben10mod.races;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class AlienArmRenderer extends GeoObjectRenderer<AbstractRace> {
    public AlienArmRenderer(ResourceLocation alienID) {
        super(new AlienArmModel(alienID));
    }

    @Override
    public long getInstanceId(AbstractRace animatable) {
        return super.getInstanceId(animatable) + 999999L;
    }
}
