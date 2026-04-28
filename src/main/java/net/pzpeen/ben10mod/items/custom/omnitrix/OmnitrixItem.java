package net.pzpeen.ben10mod.items.custom.omnitrix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.client.render.power_items.omnitrix.OmnitrixModel;
import net.pzpeen.ben10mod.client.render.power_items.omnitrix.OmnitrixRenderer;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerCapS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class OmnitrixItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation CORE_ACTIVATION_ANIM = RawAnimation.begin().thenPlay("activate_core").thenPlayAndHold("idle_open");
    private static final RawAnimation CORE_DEACTIVATION_ANIM = RawAnimation.begin().thenPlay("core_deactivate").thenPlayAndHold("idle");


    public OmnitrixItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide){
            pPlayer.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                ServerPlayer serverPlayer = ((ServerPlayer) pPlayer);
                if(pwrCap.getInventory().getStackInSlot(0).isEmpty()){
                    pwrCap.getInventory().setStackInSlot(0, itemStack.copyAndClear());
                    pwrCap.getInventory().getStackInSlot(0).getOrCreateTag().putUUID("playerUsingUUID", pPlayer.getUUID());
                    GeoItem.getOrAssignId(pwrCap.getInventory().getStackInSlot(0), (ServerLevel) pPlayer.level());

                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), pPlayer.getUUID(),
                            pwrCap.isHudActive(), pwrCap.getHudSlot()), serverPlayer);

                    //pPlayer.sendSystemMessage(Component.literal("Pois omnitrix"));

                }
                //pPlayer.sendSystemMessage(pwrCap.getInventory().getStackInSlot(0).getDisplayName());

            });
        }

        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "omnitrix_controller", 0, state -> {

            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            //System.out.println("ItemStack pega: " + stack);

            if (stack != null && stack.getTag() != null && stack.getTag().contains("playerUsingUUID")){
                //System.out.println("Entrou no if se tem tag");
                assert Minecraft.getInstance().level != null;
                Player player = Minecraft.getInstance().level.getPlayerByUUID(stack.getTag().getUUID("playerUsingUUID"));
                if (player == null) return PlayState.CONTINUE;
                boolean isMenuOpen = player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).map(PowerCap::isHudActive).orElse(false);

                if (isMenuOpen){
                    //System.out.println("Menu aberto");
                    return state.setAndContinue(OmnitrixItem.CORE_ACTIVATION_ANIM);
                }else {
                    //System.out.println("Menu fechado");
                    return state.setAndContinue(OmnitrixItem.CORE_DEACTIVATION_ANIM);
                }

            }
            if(state.isCurrentAnimation(CORE_ACTIVATION_ANIM) || state.isCurrentAnimation(CORE_DEACTIVATION_ANIM)){
                return PlayState.CONTINUE;
            }
            return state.setAndContinue(CORE_DEACTIVATION_ANIM);
        }));


    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<OmnitrixItem> renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(this.renderer == null){
                    this.renderer = new OmnitrixRenderer(new OmnitrixModel());
                }
                return this.renderer;
            }

        });
    }

}
