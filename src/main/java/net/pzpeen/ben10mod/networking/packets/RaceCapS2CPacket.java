package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;

import java.util.UUID;
import java.util.function.Supplier;

public class RaceCapS2CPacket {
    private final ResourceLocation alienID;
    private final UUID playerUUID;

    public RaceCapS2CPacket(ResourceLocation alienID, UUID playerUUID) {
        this.alienID = alienID;
        this.playerUUID = playerUUID;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeResourceLocation(this.alienID);
        buf.writeUUID(this.playerUUID);
    }

    public static RaceCapS2CPacket decode (FriendlyByteBuf buf){
        return new RaceCapS2CPacket(buf.readResourceLocation(), buf.readUUID());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;

            if(level != null){
                Player player = level.getPlayerByUUID(this.playerUUID);

                if(player != null){
                    RaceCap raceCap = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap();
                    if(raceCap != null){
                        raceCap.setRace(this.alienID, player);
                    }
                }

            }

        });

    }
}
