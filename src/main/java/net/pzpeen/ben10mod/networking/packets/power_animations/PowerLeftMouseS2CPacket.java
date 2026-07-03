package net.pzpeen.ben10mod.networking.packets.power_animations;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.powers.OmnitrixPower;

import java.util.UUID;
import java.util.function.Supplier;

public class PowerLeftMouseS2CPacket {
    private final UUID playerUUID;

    public PowerLeftMouseS2CPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeUUID(playerUUID);
    }

    public static PowerLeftMouseS2CPacket decode(FriendlyByteBuf buf){
        return new PowerLeftMouseS2CPacket(buf.readUUID());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if(level != null){
                Player player = level.getPlayerByUUID(this.playerUUID);
                if(player != null){
                    player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {

                        if(powerCap.getPower() != null){
                            powerCap.getPower().onHudRightClick();
                        }

                    });
                }


            }

        });

    }
}
