package net.pzpeen.ben10mod.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.items.custom.omnitrix.OmnitrixItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WristPlayerRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public WristPlayerRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, AbstractClientPlayer pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        pLivingEntity.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((pwrCap) -> {
            System.out.println("Cliente, menu aberto: " + pwrCap.isHudActive());

            if(pwrCap.getInventory().getStackInSlot(0).getItem() instanceof OmnitrixItem){
                ItemStack stack = pwrCap.getInventory().getStackInSlot(0);

                pPoseStack.pushPose();

                this.getParentModel().leftArm.translateAndRotate(pPoseStack);

                BlockEntityWithoutLevelRenderer customRenderer = IClientItemExtensions.of(stack).getCustomRenderer();

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
    }
}
