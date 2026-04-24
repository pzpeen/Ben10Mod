package net.pzpeen.ben10mod.client.render.power_items.omnitrix;

import net.pzpeen.ben10mod.items.custom.omnitrix.OmnitrixItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class OmnitrixRenderer extends GeoItemRenderer<OmnitrixItem> {
    private GeoModel<OmnitrixItem> model;

    public OmnitrixRenderer(GeoModel<OmnitrixItem> model) {
        super(model);
        this.model = model;
    }
}
