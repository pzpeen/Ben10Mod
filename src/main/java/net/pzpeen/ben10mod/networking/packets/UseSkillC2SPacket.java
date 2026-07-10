package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.races.AbstractRace;

import java.util.UUID;
import java.util.function.Supplier;

public class UseSkillC2SPacket {
    private final int skill;

    public UseSkillC2SPacket(int skill){
        this.skill = skill;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(skill);
    }

    public static UseSkillC2SPacket decode(FriendlyByteBuf buf){
        return new UseSkillC2SPacket(buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player != null){
                UUID playerUUID = player.getUUID();
                AbstractRace race = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace();
                int skillCooldown = 0;
                switch (skill){
                    case 1:
                        skillCooldown = race.getSkill1().getCooldown().getRemainingTicks();
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
                ModNetworking.sendToClientTrackingAndSelf(new UseSkillS2CPacket(skill, skillCooldown, playerUUID), player);

            }

        });

    }


}
