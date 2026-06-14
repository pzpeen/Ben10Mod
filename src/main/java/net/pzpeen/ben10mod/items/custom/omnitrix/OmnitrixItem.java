package net.pzpeen.ben10mod.items.custom.omnitrix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.client.render.power_items.omnitrix.OmnitrixModel;
import net.pzpeen.ben10mod.client.render.power_items.omnitrix.OmnitrixRenderer;
import net.pzpeen.ben10mod.items.ModItems;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class OmnitrixItem extends AbstractOmnitrixItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation CORE_ACTIVATION_ANIM = RawAnimation.begin().thenPlay("activate_core").thenPlayAndHold("idle_open");
    private static final RawAnimation CORE_DEACTIVATION_ANIM = RawAnimation.begin().thenPlay("core_deactivate").thenPlayAndHold("idle");


    public OmnitrixItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack omnitrixStack = new ItemStack(this);

        AbstractOmnitrixItem.setOmniCore(omnitrixStack, new ItemStack(ModItems.OMNI_CORE_LVL2.get()));
        AbstractOmnitrixItem.setBattery(omnitrixStack, new ItemStack(ModItems.BATTERY_LVL2.get()));
        AbstractOmnitrixItem.setDnaBankItem(omnitrixStack, ModItems.CODON_CONNECTOR.get().getDefaultInstance());

        return omnitrixStack;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "omnitrix_controller", 0, state -> {

            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            //System.out.println("ItemStack pega: " + stack);

            if (stack != null && stack.getTag() != null && stack.getTag().contains(AbstractOmnitrixItem.playerUsingUUIDTag)){
                //System.out.println("Entrou no if se tem tag");
                assert Minecraft.getInstance().level != null;
                Player player = Minecraft.getInstance().level.getPlayerByUUID(stack.getTag().getUUID(AbstractOmnitrixItem.playerUsingUUIDTag));
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
