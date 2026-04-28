package net.pzpeen.ben10mod.client.render.power_items.omnitrix;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.items.custom.omnitrix.OmnitrixItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OmnitrixModel extends GeoModel<OmnitrixItem> {
    private final Map<UUID, Float> angleOnPlayer = new HashMap<>();

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

    @Override
    public void setCustomAnimations(OmnitrixItem animatable, long instanceId, AnimationState<OmnitrixItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone dial = this.getBone("dial").orElseThrow();
        int selectedSlot;

        ItemStack stack = animationState.getData(DataTickets.ITEMSTACK);

        if (stack.hasTag() && stack.getTag().contains("playerUsingUUID")){
            UUID playerUUID = stack.getTag().getUUID("playerUsingUUID");
            float angleNow = angleOnPlayer.getOrDefault(playerUUID, 0f);
            if (angleNow == 0f) dial.setRotX(0f);
            assert Minecraft.getInstance().level != null;
            Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
            if (player != null){
                boolean isHudOn = player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).map(PowerCap::isHudActive).orElse(false);
                selectedSlot = player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).map(PowerCap::getHudSlot).orElse(0);
                selectedSlot = selectedSlot == 0 ? 9 : selectedSlot - 1;

                float angleToGo = isHudOn ? (float) Math.toRadians(selectedSlot * 36.0) : 0f;

                float newAngle = angleNow + (angleToGo - angleNow)*0.3f;

                this.angleOnPlayer.put(playerUUID, newAngle);
                dial.setRotX(newAngle);
            }

        }



    }
}
