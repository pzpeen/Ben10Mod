package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerInventoryProvider;

import java.util.function.Supplier;

public class PowerInventoryS2CPacket {
    private final CompoundTag inventoryNBT;

    public PowerInventoryS2CPacket(CompoundTag inventoryNBT) {
        this.inventoryNBT = inventoryNBT;
    }

    public static PowerInventoryS2CPacket decode(FriendlyByteBuf buf){
        //System.out.println("Decoder nbt:" + buf.readNbt());
        return new PowerInventoryS2CPacket(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(this.inventoryNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player
                    .getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY).ifPresent(pwrInv -> {
                        //pwrInv.getInventory().deserializeNBT(this.inventoryNBT);
                        pwrInv.loadNBT(this.inventoryNBT);

            });
        });
    }
}
