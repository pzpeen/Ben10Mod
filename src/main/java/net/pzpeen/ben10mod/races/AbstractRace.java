package net.pzpeen.ben10mod.races;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pzpeen.ben10mod.Ben10Mod;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

public abstract class AbstractRace implements GeoAnimatable {
    protected final String alienID;
    protected final ResourceLocation icon;

    public AbstractRace(String alienID){
        this.alienID = alienID;
        this.icon = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/races/"+alienID+"/icon.png");
    }

    public String getAlienID() {
        return alienID;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public abstract void render(PoseStack poseStack, Player player, MultiBufferSource bufferSource, int packedLight, float partialTick);
}
