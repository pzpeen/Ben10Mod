package net.pzpeen.ben10mod.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.races.AbstractRace;

import java.util.UUID;
import java.util.function.Supplier;

public class UseSkillC2SPacket {
    private final int skill;
    private final char type; //p = press, h = hold, r = release

    public UseSkillC2SPacket(int skill){
        this.skill = skill;
        this.type = 'p';
    }
    public UseSkillC2SPacket(int skill, char type){
        this.skill = skill;
        this.type = type;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(skill);
        buf.writeChar(type);
    }

    public static UseSkillC2SPacket decode(FriendlyByteBuf buf){
        return new UseSkillC2SPacket(buf.readInt(), buf.readChar());
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
                        if(race.getSkill1() != null){
                            skillCooldown = race.getSkill1().getCooldown().getRemainingTicks();
                            switch (type){
                                case 'h':
                                    race.getSkill1().setHolding(true);
                                    break;
                                case 'r':
                                    race.releaseSkill1();
                                    break;
                                default:
                                    race.useSkill1();

                            }

                        }
                        break;
                    case 2:
                        if(race.getSkill2() != null){
                            skillCooldown = race.getSkill2().getCooldown().getRemainingTicks();
                            switch (type){
                                case 'h':
                                    race.getSkill2().setHolding(true);
                                    break;
                                case 'r':
                                    race.releaseSkill2();
                                    break;
                                default:
                                    race.useSkill2();

                            }
                        }
                        break;
                    case 3:
                        if(race.getSkill3() != null){
                            skillCooldown = race.getSkill3().getCooldown().getRemainingTicks();
                            switch (type){
                                case 'h':
                                    race.getSkill3().setHolding(true);
                                    break;
                                case 'r':
                                    race.releaseSkill3();
                                    break;
                                default:
                                    race.useSkill3();

                            }
                        }
                        break;
                    case 4:
                        if(race.getSkill4() != null){
                            skillCooldown = race.getSkill4().getCooldown().getRemainingTicks();
                            switch (type){
                                case 'h':
                                    race.getSkill4().setHolding(true);
                                    break;
                                case 'r':
                                    race.releaseSkill4();
                                    break;
                                default:
                                    race.useSkill4();

                            }
                        }
                        break;
                    case 5:
                        if(race.getSkill5() != null){
                            skillCooldown = race.getSkill5().getCooldown().getRemainingTicks();
                            switch (type){
                                case 'h':
                                    race.getSkill5().setHolding(true);
                                    break;
                                case 'r':
                                    race.releaseSkill5();
                                    break;
                                default:
                                    race.useSkill5();

                            }
                        }
                        break;
                }
                ModNetworking.sendToClientTrackingAndSelf(new UseSkillS2CPacket(skill, skillCooldown, playerUUID, type), player);

            }

        });

    }


}
