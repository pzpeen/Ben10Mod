package net.pzpeen.ben10mod.items.custom.omnitrix;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerCapS2CPacket;
import net.pzpeen.ben10mod.sounds.ModSounds;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;

public abstract class AbstractOmnitrixItem extends Item implements GeoItem {
    public static final String playerUsingUUIDTag = "playerUsingUUID";
    public static final String omniCoreComponentTag = "omniCoreComponent";
    public static final String batteryComponentTag = "batteryComponent";
    public static final String dnaBankComponentTag = "dnaBankComponent";
    public static final String selectedPlaylist = "selectedPlaylist";

    public AbstractOmnitrixItem(Properties pProperties) {
        super(pProperties);
    }

    public static void setOmniCore(ItemStack omnitrix, ItemStack omniCore){
        CompoundTag omnitrixNbt = omnitrix.getOrCreateTag();
        CompoundTag omniCoreInNbt = new CompoundTag();

        omniCore.save(omniCoreInNbt);

        omnitrixNbt.put(omniCoreComponentTag, omniCoreInNbt);
    }

    public static void setBattery(ItemStack omnitrix, ItemStack battery){
        CompoundTag omnitrixNbt = omnitrix.getOrCreateTag();
        CompoundTag batteryInNbt = new CompoundTag();

        battery.save(batteryInNbt);

        omnitrixNbt.put(batteryComponentTag, batteryInNbt);
    }

    public static void setDnaBankItem(ItemStack omnitrix, ItemStack dnaBankStack){
        CompoundTag omnitrixNbt = omnitrix.getOrCreateTag();
        CompoundTag dnaBankInNbt = new CompoundTag();

        dnaBankStack.save(dnaBankInNbt);

        omnitrixNbt.put(dnaBankComponentTag, dnaBankInNbt);
    }

    public static ItemStack getOmniCore(ItemStack omnitrix){
        if(omnitrix.hasTag() && omnitrix.getTag().contains(omniCoreComponentTag)){
            CompoundTag omniCoreNbt = omnitrix.getTag().getCompound(omniCoreComponentTag);
            return ItemStack.of(omniCoreNbt);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getBattery(ItemStack omnitrix){
        if(omnitrix.hasTag() && omnitrix.getTag().contains(batteryComponentTag)){
            CompoundTag batteryNbt = omnitrix.getTag().getCompound(batteryComponentTag);
            return ItemStack.of(batteryNbt);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getDnaBankItem(ItemStack omnitrix){
        if(omnitrix.hasTag() && omnitrix.getTag().contains(dnaBankComponentTag)){
            CompoundTag dnaBankNbt = omnitrix.getTag().getCompound(dnaBankComponentTag);
            return ItemStack.of(dnaBankNbt);
        }else{
            System.err.println("OMNITRIX ITEM WITHOUT DNABANKITEM");
            return ItemStack.EMPTY;
        }

    }

    public static void setSelectedPlaylist(ItemStack omnitrix, int playlist){
        if(playlist < 0 || playlist > 9){
            omnitrix.getOrCreateTag().putInt(selectedPlaylist, 0);
        }else{
            omnitrix.getOrCreateTag().putInt(selectedPlaylist, playlist);
        }
    }

    public static int getSelectedPlaylist(ItemStack omnitrix){
        if(omnitrix.getOrCreateTag().contains(selectedPlaylist)){
            return omnitrix.getOrCreateTag().getInt(selectedPlaylist);
        }
        omnitrix.getOrCreateTag().putInt(selectedPlaylist, 0);
        return 0;

    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide){
            pPlayer.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                ServerPlayer serverPlayer = ((ServerPlayer) pPlayer);
                if(pwrCap.getInventory().getStackInSlot(0).isEmpty()){
                    pwrCap.getInventory().setStackInSlot(0, itemStack.copyAndClear());
                    pwrCap.getInventory().getStackInSlot(0).getOrCreateTag().putUUID(playerUsingUUIDTag, pPlayer.getUUID());
                    GeoItem.getOrAssignId(pwrCap.getInventory().getStackInSlot(0), (ServerLevel) pPlayer.level());
                    pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.OMNITRIX_PUTTING.get(),
                            SoundSource.PLAYERS, 0.5f, 1.0f);
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), pPlayer.getUUID(),
                            pwrCap.isHudActive(), pwrCap.getHudSlot()), serverPlayer);

                    //pPlayer.sendSystemMessage(Component.literal("Pois omnitrix"));

                }
                //pPlayer.sendSystemMessage(pwrCap.getInventory().getStackInSlot(0).getDisplayName());

            });
        }

        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }
}
