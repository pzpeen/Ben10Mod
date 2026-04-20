package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;

import java.util.UUID;
import java.util.function.Supplier;

public class PowerCapS2CPacket {
    private final CompoundTag inventoryNBT;
    private final UUID playerId;
    private final boolean hudActive;
    private final int hudSlot;

    public PowerCapS2CPacket(CompoundTag inventoryNBT, UUID pPlayerId, boolean hudActive, int hudSlot) {
        this.inventoryNBT = inventoryNBT;
        this.playerId = pPlayerId;
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;

    }

    public static PowerCapS2CPacket decode(FriendlyByteBuf buf){
        //System.out.println("Decoder nbt:" + buf.readNbt());
        return new PowerCapS2CPacket(buf.readNbt(), buf.readUUID(), buf.readBoolean(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(this.inventoryNBT);
        buf.writeUUID(this.playerId);
        buf.writeBoolean(this.hudActive);
        buf.writeInt(this.hudSlot);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;

            if(level != null){
                Entity player = level.getPlayerByUUID(this.playerId);

                if(player instanceof Player playerToRender){
                    playerToRender.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((pwrInv) -> {
                        pwrInv.getInventory().deserializeNBT(this.inventoryNBT);
                        pwrInv.setHudActive(this.hudActive);
                        pwrInv.setHudSlot(this.hudSlot);
                    });
                }
            }
        });
    }
}
