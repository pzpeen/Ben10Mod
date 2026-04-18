package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerInventoryProvider;
import net.pzpeen.ben10mod.gui.menus.PowerInventoryMenu;

import java.util.function.Supplier;

public class OpenPowerMenuC2SPacket {
    public OpenPowerMenuC2SPacket(){}
    public OpenPowerMenuC2SPacket(FriendlyByteBuf buf){}

    public void toBytes(FriendlyByteBuf buf){}

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player != null){

                NetworkHooks.openScreen(player,
                        new SimpleMenuProvider((id, inv, p) ->
                                new PowerInventoryMenu(id, inv,
                                        player.getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY)
                                                .orElseThrow(IllegalStateException::new).getInventory()), Component.literal("Power Inventory")));
            }
        });
        return true;
    }
}
