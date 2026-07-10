package net.pzpeen.ben10mod.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.client.render.power_items.omnitrix.OmnitrixRenderer;
import net.pzpeen.ben10mod.items.custom.dna_bank.AbstractDnaBankItem;
import net.pzpeen.ben10mod.items.custom.omnitrix.AbstractOmnitrixItem;
import net.pzpeen.ben10mod.items.custom.omnitrix.OmnitrixItem;
import net.pzpeen.ben10mod.systems.DnaBank;
import net.pzpeen.ben10mod.systems.Playlist;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WristPlayerRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public WristPlayerRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, AbstractClientPlayer pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        PowerCap powerCap = ((IBen10ModCapCache)pLivingEntity).ben10Mod$getCachedPowerCap();
        if(powerCap != null){
            //System.out.println("Cliente, menu aberto: " + pwrCap.isHudActive());

            if(powerCap.getInventory().getStackInSlot(0).getItem() instanceof OmnitrixItem){
                ItemStack stack = powerCap.getInventory().getStackInSlot(0);

                pPoseStack.pushPose();

                this.getParentModel().leftArm.translateAndRotate(pPoseStack);

                BlockEntityWithoutLevelRenderer customRenderer = IClientItemExtensions.of(stack).getCustomRenderer();

                if(customRenderer instanceof OmnitrixRenderer){
                    if(!powerCap.isHudActive()){
                        ((OmnitrixRenderer) customRenderer).setSelectedAlien(null);
                    }else{
                        DnaBank dnaBank = AbstractDnaBankItem.getDnaBank(AbstractOmnitrixItem.getDnaBankItem(stack));
                        Playlist<ResourceLocation> selectedPlaylist = dnaBank.getPlaylist(AbstractOmnitrixItem.getSelectedPlaylist(stack));
                        //System.out.println("hudSlotSelected: "+pwrCap.getHudSlot());
                        //System.out.println("AlienOnPlaylistInHudPosition:"+ selectedPlaylist.get(pwrCap.getHudSlot()));
                        int convertedSlot = powerCap.getHudSlot() == 0 ? 9 : powerCap.getHudSlot() - 1;
                        ((OmnitrixRenderer) customRenderer).setSelectedAlien(selectedPlaylist.get(convertedSlot));
                    }
                }

                if(customRenderer instanceof GeoItemRenderer<?> geoItemRenderer){
                    geoItemRenderer.renderByItem(
                            stack,
                            ItemDisplayContext.NONE,
                            pPoseStack,
                            pBuffer,
                            pPackedLight,
                            OverlayTexture.NO_OVERLAY
                    );
                }


                pPoseStack.popPose();

            }
        }

        /*
        pLivingEntity.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((pwrCap) -> {
            //System.out.println("Cliente, menu aberto: " + pwrCap.isHudActive());

            if(pwrCap.getInventory().getStackInSlot(0).getItem() instanceof OmnitrixItem){
                ItemStack stack = pwrCap.getInventory().getStackInSlot(0);

                pPoseStack.pushPose();

                this.getParentModel().leftArm.translateAndRotate(pPoseStack);

                BlockEntityWithoutLevelRenderer customRenderer = IClientItemExtensions.of(stack).getCustomRenderer();

                if(customRenderer instanceof OmnitrixRenderer){
                    if(!pwrCap.isHudActive()){
                        ((OmnitrixRenderer) customRenderer).setSelectedAlien(null);
                    }else{
                        DnaBank dnaBank = AbstractDnaBankItem.getDnaBank(AbstractOmnitrixItem.getDnaBankItem(stack));
                        Playlist<ResourceLocation> selectedPlaylist = dnaBank.getPlaylist(AbstractOmnitrixItem.getSelectedPlaylist(stack));
                        //System.out.println("hudSlotSelected: "+pwrCap.getHudSlot());
                        //System.out.println("AlienOnPlaylistInHudPosition:"+ selectedPlaylist.get(pwrCap.getHudSlot()));
                        int convertedSlot = pwrCap.getHudSlot() == 0 ? 9 : pwrCap.getHudSlot() - 1;
                        ((OmnitrixRenderer) customRenderer).setSelectedAlien(selectedPlaylist.get(convertedSlot));
                    }
                }

                if(customRenderer instanceof GeoItemRenderer<?> geoItemRenderer){
                    geoItemRenderer.renderByItem(
                            stack,
                            ItemDisplayContext.NONE,
                            pPoseStack,
                            pBuffer,
                            pPackedLight,
                            OverlayTexture.NO_OVERLAY
                            );
                }


                pPoseStack.popPose();

            }

        });

         */
    }
}
