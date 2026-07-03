package net.pzpeen.ben10mod.networking.packets.power_animations;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;

import java.util.function.Supplier;

public class PowerLeftMouseC2SPacket {

    public PowerLeftMouseC2SPacket(){}

    public void encode(FriendlyByteBuf buf){}

    public static PowerLeftMouseC2SPacket decode(FriendlyByteBuf buf){
        return new PowerLeftMouseC2SPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player != null){
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {

                    if(powerCap.getPower() != null){
                        powerCap.getPower().onHudRightClick();
                    }

                    ModNetworking.sendToClientTrackingAndSelf(new PowerLeftMouseS2CPacket(player.getUUID()), player);
                });

            }

        });


    }
}
