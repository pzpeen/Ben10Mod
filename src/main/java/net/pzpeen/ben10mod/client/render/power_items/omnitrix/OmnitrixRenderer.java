package net.pzpeen.ben10mod.client.render.power_items.omnitrix;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.items.custom.dna_bank.AbstractDnaBankItem;
import net.pzpeen.ben10mod.items.custom.omnitrix.AbstractOmnitrixItem;
import net.pzpeen.ben10mod.items.custom.omnitrix.OmnitrixItem;
import net.pzpeen.ben10mod.races.AbstractRace;
import net.pzpeen.ben10mod.systems.DnaBank;
import net.pzpeen.ben10mod.systems.Playlist;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class OmnitrixRenderer extends GeoItemRenderer<OmnitrixItem> {
    private GeoModel<OmnitrixItem> model;
    private ResourceLocation selectedAlien;

    public OmnitrixRenderer(GeoModel<OmnitrixItem> model) {
        super(model);
        this.model = model;
        this.renderLayers.addLayer(new GeoRenderLayer<OmnitrixItem>(this) {
            @Override
            public void render(PoseStack poseStack, OmnitrixItem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                //System.out.println("Selected alien: "+ selectedAlien);
                if (selectedAlien == null) return;
                ResourceLocation silhouetteTexture = AbstractRace.getSilhouette(selectedAlien);

                GeoBone alienSilhouette = bakedModel.getBone("alien_icon").orElse(null);

                if(alienSilhouette != null){
                    RenderType pRenderType = RenderType.entityCutoutNoCull(silhouetteTexture);
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(pRenderType);
                    //System.out.println("Trying render the silhouette layer");
                    reRender(bakedModel, poseStack, bufferSource, animatable, pRenderType, vertexConsumer, partialTick,
                            packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);


                }

            }
        });
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if(transformType.firstPerson()){
            LocalPlayer player = Minecraft.getInstance().player;

            if(player != null){
                PowerCap powerCap = ((IBen10ModCapCache)player).ben10Mod$getCachedPowerCap();
                if(powerCap != null){
                    if(!powerCap.isHudActive()){
                        setSelectedAlien(null);
                    }else{
                        DnaBank dnaBank = AbstractDnaBankItem.getDnaBank(AbstractOmnitrixItem.getDnaBankItem(stack));
                        Playlist<ResourceLocation> selectedPlaylist = dnaBank.getPlaylist(AbstractOmnitrixItem.getSelectedPlaylist(stack));
                        //System.out.println("hudSlotSelected: "+pwrCap.getHudSlot());
                        //System.out.println("AlienOnPlaylistInHudPosition:"+ selectedPlaylist.get(pwrCap.getHudSlot()));
                        int convertedSlot = powerCap.getHudSlot() == 0 ? 9 : powerCap.getHudSlot() - 1;
                        setSelectedAlien(selectedPlaylist.get(convertedSlot));
                    }

                }

                /*
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    if(!pwrCap.isHudActive()){
                        setSelectedAlien(null);
                    }else{
                        DnaBank dnaBank = AbstractDnaBankItem.getDnaBank(AbstractOmnitrixItem.getDnaBankItem(stack));
                        Playlist<ResourceLocation> selectedPlaylist = dnaBank.getPlaylist(AbstractOmnitrixItem.getSelectedPlaylist(stack));
                        //System.out.println("hudSlotSelected: "+pwrCap.getHudSlot());
                        //System.out.println("AlienOnPlaylistInHudPosition:"+ selectedPlaylist.get(pwrCap.getHudSlot()));
                        int convertedSlot = pwrCap.getHudSlot() == 0 ? 9 : pwrCap.getHudSlot() - 1;
                        setSelectedAlien(selectedPlaylist.get(convertedSlot));
                    }

                });

                 */
            }

        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    public void setSelectedAlien(ResourceLocation selectedAlien) {
        this.selectedAlien = selectedAlien;
    }

    public ResourceLocation getSelectedAlien() {
        return selectedAlien;
    }
}
