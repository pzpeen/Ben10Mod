package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.powers.PowerRegistries;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class PowerCapS2CPacket {
    private final CompoundTag inventoryNBT;
    private final UUID playerId;
    private final boolean hudActive;
    private final int hudSlot;
    private final ResourceLocation powerID;
    private final boolean changePowerID;

    public PowerCapS2CPacket(CompoundTag inventoryNBT, UUID pPlayerId, boolean hudActive, int hudSlot) {
        this.inventoryNBT = inventoryNBT;
        this.playerId = pPlayerId;
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;
        this.changePowerID = false;
        this.powerID = null;

    }

    public PowerCapS2CPacket(CompoundTag inventoryNBT, UUID pPlayerId, boolean hudActive, int hudSlot, ResourceLocation powerId) {
        this.inventoryNBT = inventoryNBT;
        this.playerId = pPlayerId;
        this.hudActive = hudActive;
        this.hudSlot = hudSlot;
        this.changePowerID = true;
        this.powerID = powerId;

    }

    public static PowerCapS2CPacket decode(FriendlyByteBuf buf){
        //System.out.println("Decoder nbt:" + buf.readNbt());
        CompoundTag inventroyNbt = buf.readNbt();
        UUID playerUUID = buf.readUUID();
        boolean isHudActive = buf.readBoolean();
        int hudSlot = buf.readInt();

        ResourceLocation powerId = null;
        if(buf.readBoolean()){
            powerId = buf.readResourceLocation();
        }
        if(powerId != null){
            return new PowerCapS2CPacket(inventroyNbt, playerUUID, isHudActive, hudSlot, powerId);
        }
        return new PowerCapS2CPacket(inventroyNbt, playerUUID, isHudActive, hudSlot);
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(this.inventoryNBT);
        buf.writeUUID(this.playerId);
        buf.writeBoolean(this.hudActive);
        buf.writeInt(this.hudSlot);
        buf.writeBoolean(this.changePowerID);
        if(this.changePowerID){
            buf.writeResourceLocation(Objects.requireNonNullElse(powerID, PowerRegistries.noPowerID));

        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;

            if(level != null){
                Entity player = level.getPlayerByUUID(this.playerId);

                if(player instanceof Player playerToRender){
                    PowerCap powerCap = ((IBen10ModCapCache)playerToRender).ben10Mod$getCachedPowerCap();
                    if(powerCap != null){
                        powerCap.getInventory().deserializeNBT(this.inventoryNBT);
                        powerCap.setHudActive(this.hudActive);
                        powerCap.setHudSlot(this.hudSlot);
                        if(this.powerID != null){
                            powerCap.setPower(this.powerID);
                        }
                    }
                    /*
                    playerToRender.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((powerCap) -> {
                        powerCap.getInventory().deserializeNBT(this.inventoryNBT);
                        powerCap.setHudActive(this.hudActive);
                        powerCap.setHudSlot(this.hudSlot);
                        if(this.powerID != null){
                            powerCap.setPower(this.powerID);
                        }
                    });

                     */
                }
            }
        });
    }
}
