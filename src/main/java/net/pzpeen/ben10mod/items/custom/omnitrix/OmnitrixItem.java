package net.pzpeen.ben10mod.items.custom.omnitrix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.client.render.power_items.omnitrix.OmnitrixModel;
import net.pzpeen.ben10mod.client.render.power_items.omnitrix.OmnitrixRenderer;
import net.pzpeen.ben10mod.items.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(!pLevel.isClientSide() && !pStack.hasTag()){
            AbstractOmnitrixItem.setOmniCore(pStack, new ItemStack(ModItems.OMNI_CORE_LVL2.get()));
            AbstractOmnitrixItem.setBattery(pStack, new ItemStack(ModItems.BATTERY_LVL2.get()));
            AbstractOmnitrixItem.setDnaBankItem(pStack, ModItems.CODON_CONNECTOR.get().getDefaultInstance());
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        pTooltipComponents.add(Component.literal("COMPONENTS:"));

        CompoundTag nbt = pStack.getTag();
        if(nbt == null){
            pTooltipComponents.add(Component.literal("ERROR NO COMPONENTS"));
            return;
        }

        if(nbt.contains(dnaBankComponentTag)){
            ItemStack dnaBankStack = ItemStack.of(nbt.getCompound(dnaBankComponentTag));
            pTooltipComponents.add(Component.literal("Dna Bank: ").append(dnaBankStack.getHoverName()));
        }else{
            pTooltipComponents.add(Component.literal("NO DNA BANK"));
        }

        if(nbt.contains(batteryComponentTag)){
            ItemStack batteryStack = ItemStack.of(nbt.getCompound(batteryComponentTag));
            pTooltipComponents.add(Component.literal("Battery: ").append(batteryStack.getHoverName()));
        }else{
            pTooltipComponents.add(Component.literal("NO BATTERY"));
        }

        if(nbt.contains(omniCoreComponentTag)){
            ItemStack omnicoreStack = ItemStack.of(nbt.getCompound(omniCoreComponentTag));
            pTooltipComponents.add(Component.literal("Omni core: ").append(omnicoreStack.getHoverName()));
        }else{
            pTooltipComponents.add(Component.literal("NO CORE"));
        }

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
            private OmnitrixRenderer renderer = null;

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
