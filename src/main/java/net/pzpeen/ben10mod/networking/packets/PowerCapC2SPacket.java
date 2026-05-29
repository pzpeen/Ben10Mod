package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.items.ModItems;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.sounds.ModSounds;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PowerCapC2SPacket {
    private final boolean hudActive;
    private final int hudSlot;
    private final SoundEvent soundEvent;
    private final boolean hasSound;

    public PowerCapC2SPacket(boolean hudActive, int hudSlot) {
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;
        this.soundEvent = null;
        this.hasSound = false;
    }
    public PowerCapC2SPacket(boolean hudActive, int hudSlot, SoundEvent soundEvent) {
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;
        this.soundEvent = soundEvent;
        this.hasSound = true;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeBoolean(this.hasSound);
        buf.writeBoolean(this.hudActive);
        buf.writeInt(this.hudSlot);

        if(hasSound){
            assert this.soundEvent != null;
            buf.writeResourceLocation(this.soundEvent.getLocation());
        }

    }

    public static PowerCapC2SPacket decode(FriendlyByteBuf buf){
        boolean hasSound = buf.readBoolean();
        if(hasSound){
            return new PowerCapC2SPacket(buf.readBoolean(), buf.readInt(), ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation()));
        }else{
            return new PowerCapC2SPacket(buf.readBoolean(), buf.readInt());
        }

    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null){
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    pwrCap.setHudActive(this.hudActive);
                    pwrCap.setHudSlot(this.hudSlot);
                    if (this.soundEvent != null){
                        //System.out.println("Tocando som.");
                        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), this.soundEvent,
                                SoundSource.PLAYERS, 0.5f, 1.0f);
                    }

                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory()
                            .serializeNBT(), player.getUUID(), pwrCap.isHudActive(), pwrCap.getHudSlot()), player);
                });
            }


        });
        context.setPacketHandled(true);

    }

}
