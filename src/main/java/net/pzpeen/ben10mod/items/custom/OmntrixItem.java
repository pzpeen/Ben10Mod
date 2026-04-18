package net.pzpeen.ben10mod.items.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerInventoryProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerInventoryS2CPacket;
import org.jetbrains.annotations.NotNull;

public class OmntrixItem extends Item {
    public OmntrixItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide){
            pPlayer.getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY).ifPresent(pwrInv -> {
                ServerPlayer serverPlayer = ((ServerPlayer) pPlayer);
                if(pwrInv.getInventory().getStackInSlot(0).isEmpty()){
                    pwrInv.getInventory().setStackInSlot(0, itemStack.copyAndClear());
                    ModNetworking.sendToPlayer(new PowerInventoryS2CPacket(pwrInv.getInventory().serializeNBT()), serverPlayer);
                    pPlayer.sendSystemMessage(Component.literal("Pois omnitrix"));

                }else{
                    ItemStack pwrItem = pwrInv.getInventory().getStackInSlot(0);
                    pPlayer.addItem(pwrItem.copyAndClear());
                    pPlayer.sendSystemMessage(Component.literal("Tirou omnitrix"));
                    ModNetworking.sendToPlayer(new PowerInventoryS2CPacket(pwrInv.getInventory().serializeNBT()), serverPlayer);
                }
                pPlayer.sendSystemMessage(pwrInv.getInventory().getStackInSlot(0).getDisplayName());



            });
        }

        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }
}
