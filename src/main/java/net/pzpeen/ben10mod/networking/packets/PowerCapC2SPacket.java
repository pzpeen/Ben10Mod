package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.networking.ModNetworking;

import java.util.function.Supplier;

public class PowerCapC2SPacket {
    private final boolean hudActive;
    private final int hudSlot;
    private final SoundEvent soundEvent;
    private final boolean hasSound;
    private final boolean changePowerID;
    private final ResourceLocation powerID;

    public PowerCapC2SPacket(boolean hudActive, int hudSlot) {
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;
        this.soundEvent = null;
        this.hasSound = false;
        this.changePowerID = false;
        this.powerID = null;
    }
    public PowerCapC2SPacket(boolean hudActive, int hudSlot, SoundEvent soundEvent) {
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;
        this.soundEvent = soundEvent;
        this.hasSound = true;
        this.changePowerID = false;
        this.powerID = null;
    }
    public PowerCapC2SPacket(boolean hudActive, int hudSlot, ResourceLocation powerID) {
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;
        this.soundEvent = null;
        this.hasSound = false;
        this.changePowerID = true;
        this.powerID = powerID;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeBoolean(this.hasSound);
        buf.writeBoolean(this.changePowerID);
        buf.writeBoolean(this.hudActive);
        buf.writeInt(this.hudSlot);

        if(hasSound){
            assert this.soundEvent != null;
            buf.writeResourceLocation(this.soundEvent.getLocation());
        }
        if(changePowerID){
            assert this.powerID != null;
            buf.writeResourceLocation(this.powerID);
        }

    }

    public static PowerCapC2SPacket decode(FriendlyByteBuf buf){
        boolean hasSound = buf.readBoolean();
        boolean changePowerID = buf.readBoolean();
        if(hasSound){
            return new PowerCapC2SPacket(buf.readBoolean(), buf.readInt(), ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation()));
        }else if(changePowerID){
            return new PowerCapC2SPacket(buf.readBoolean(), buf.readInt(), buf.readResourceLocation());
        }else{
            return new PowerCapC2SPacket(buf.readBoolean(), buf.readInt());
        }

    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null){
                PowerCap powerCap = ((IBen10ModCapCache)player).ben10Mod$getCachedPowerCap();
                if(powerCap != null){
                    powerCap.setHudActive(this.hudActive);
                    powerCap.setHudSlot(this.hudSlot);
                    if (this.soundEvent != null){
                        //System.out.println("Tocando som.");
                        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), this.soundEvent,
                                SoundSource.PLAYERS, 0.5f, 1.0f);
                    }

                    if(this.powerID != null){
                        powerCap.setPower(this.powerID);
                    }

                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(powerCap.getInventory()
                            .serializeNBT(), player.getUUID(), powerCap.isHudActive(), powerCap.getHudSlot()), player);
                }

                /*
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    pwrCap.setHudActive(this.hudActive);
                    pwrCap.setHudSlot(this.hudSlot);
                    if (this.soundEvent != null){
                        //System.out.println("Tocando som.");
                        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), this.soundEvent,
                                SoundSource.PLAYERS, 0.5f, 1.0f);
                    }

                    if(this.powerID != null){
                        pwrCap.setPower(this.powerID);
                    }

                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory()
                            .serializeNBT(), player.getUUID(), pwrCap.isHudActive(), pwrCap.getHudSlot()), player);
                });

                 */
            }


        });
        context.setPacketHandled(true);

    }

}
