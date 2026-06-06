package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;

import java.util.function.Supplier;

public class RaceCapC2SPacket {
    private final boolean hasSound;
    private final ResourceLocation alienID;
    private final SoundEvent soundEvent;

    public RaceCapC2SPacket(ResourceLocation alienID) {
        this.hasSound = false;
        this.alienID = alienID;
        this.soundEvent = null;
    }

    public RaceCapC2SPacket(ResourceLocation alienID, SoundEvent soundEvent) {
        this.hasSound = true;
        this.alienID = alienID;
        this.soundEvent = soundEvent;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeBoolean(this.hasSound);
        buf.writeResourceLocation(this.alienID);
        if(this.hasSound){
            assert soundEvent != null;
            buf.writeResourceLocation(soundEvent.getLocation());
        }
    }

    public static RaceCapC2SPacket decode(FriendlyByteBuf buf){
        boolean hasSound = buf.readBoolean();
        ResourceLocation alienID = buf.readResourceLocation();
        if(hasSound){
            return new RaceCapC2SPacket(alienID, ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation()));
        }
        return new RaceCapC2SPacket(alienID);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null){
                player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                    raceCap.setRace(this.alienID);
                });
                if(this.soundEvent != null){
                    player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                            this.soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
                }

                ModNetworking.sendToClientTrackingAndSelf(new RaceCapS2CPacket(this.alienID, player.getUUID()), player);

            }

        });
        context.setPacketHandled(true);

    }

}
