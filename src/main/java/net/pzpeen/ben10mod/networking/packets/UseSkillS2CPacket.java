package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.races.AbstractRace;

import java.util.UUID;
import java.util.function.Supplier;

public class UseSkillS2CPacket {
    private final int skill;
    private final int cooldown;
    private final UUID playerUUID;

    public UseSkillS2CPacket(int skill, int cooldown, UUID playerUUID) {
        this.skill = skill;
        this.cooldown = cooldown;
        this.playerUUID = playerUUID;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(skill);
        buf.writeInt(cooldown);
        buf.writeUUID(playerUUID);
    }

    public static UseSkillS2CPacket decode(FriendlyByteBuf buf){
        return new UseSkillS2CPacket(buf.readInt(), buf.readInt(), buf.readUUID());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if(level != null){
                Player player = level.getPlayerByUUID(playerUUID);
                if(player != null){
                    AbstractRace race = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace();
                    switch (skill){
                        case 1:
                            race.getSkill1().getCooldown().start(cooldown);
                            race.useSkill1();
                            break;
                        case 2:
                            race.useSkill2();
                            break;
                        case 3:
                            race.useSkill3();
                            break;
                        case 4:
                            race.useSkill4();
                            break;
                        case 5:
                            race.useSkill5();
                            break;
                    }

                }

            }

        });

    }
}
