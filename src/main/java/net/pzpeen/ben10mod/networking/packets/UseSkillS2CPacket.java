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
    private final char type;

    public UseSkillS2CPacket(int skill, int cooldown, UUID playerUUID, char type) {
        this.skill = skill;
        this.cooldown = cooldown;
        this.playerUUID = playerUUID;
        this.type = type;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(skill);
        buf.writeInt(cooldown);
        buf.writeUUID(playerUUID);
        buf.writeChar(type);
    }

    public static UseSkillS2CPacket decode(FriendlyByteBuf buf){
        return new UseSkillS2CPacket(buf.readInt(), buf.readInt(), buf.readUUID(), buf.readChar());
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
                            if(race.getSkill1() != null){
                                race.getSkill1().getCooldown().start(cooldown);
                                switch (type){
                                    case 'h':
                                        race.getSkill1().setHolding(true);
                                        break;
                                    case 'r':
                                        race.getSkill1().setHolding(false);
                                        race.releaseSkill1();
                                        break;
                                    default:
                                        race.useSkill1();

                                }
                            }
                            break;
                        case 2:
                            if(race.getSkill2() != null){
                                race.getSkill2().getCooldown().start(cooldown);
                                switch (type){
                                    case 'h':
                                        race.getSkill2().setHolding(true);
                                        break;
                                    case 'r':
                                        race.getSkill2().setHolding(false);
                                        race.releaseSkill2();
                                        break;
                                    default:
                                        race.useSkill2();

                                }
                            }
                            break;
                        case 3:
                            if(race.getSkill3() != null){
                                race.getSkill3().getCooldown().start(cooldown);
                                switch (type){
                                    case 'h':
                                        race.getSkill3().setHolding(true);
                                        break;
                                    case 'r':
                                        race.getSkill3().setHolding(false);
                                        race.releaseSkill3();
                                        break;
                                    default:
                                        race.useSkill3();

                                }
                            }
                            break;
                        case 4:
                            if(race.getSkill4() != null){
                                race.getSkill4().getCooldown().start(cooldown);
                                switch (type){
                                    case 'h':
                                        race.getSkill4().setHolding(true);
                                        break;
                                    case 'r':
                                        race.getSkill4().setHolding(false);
                                        race.releaseSkill4();
                                        break;
                                    default:
                                        race.useSkill4();

                                }
                            }
                            break;
                        case 5:
                            if(race.getSkill5() != null){
                                race.getSkill5().getCooldown().start(cooldown);
                                switch (type){
                                    case 'h':
                                        race.getSkill5().setHolding(true);
                                        break;
                                    case 'r':
                                        race.getSkill5().setHolding(false);
                                        race.releaseSkill5();
                                        break;
                                    default:
                                        race.useSkill5();

                                }
                            }
                            break;
                    }

                }

            }

        });

    }
}
