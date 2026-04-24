package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;

import java.util.function.Supplier;

public class PowerCapC2SPacket {
    private final boolean hudActive;
    private final int hudSlot;

    public PowerCapC2SPacket(boolean hudActive, int hudSlot) {
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeBoolean(this.hudActive);
        buf.writeInt(this.hudSlot);
    }

    public static PowerCapC2SPacket decode(FriendlyByteBuf buf){
        return new PowerCapC2SPacket(buf.readBoolean(), buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null){
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    pwrCap.setHudActive(this.hudActive);
                    pwrCap.setHudSlot(this.hudSlot);

                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), player.getUUID(), pwrCap.isHudActive(), pwrCap.getHudSlot()), player);
                });
            }


        });

    }

}
