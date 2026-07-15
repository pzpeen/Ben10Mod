package net.pzpeen.ben10mod.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class SoundManager {
    private static final HashMap<UUID, HashMap<SoundEvent, ContinuousSound>> ACTIVE_SOUNDS = new HashMap<>();

    public static void playSound(Player player, SoundEvent soundEvent){
        UUID playerUUID = player.getUUID();

        if(!ACTIVE_SOUNDS.containsKey(playerUUID)){
            ContinuousSound continuousSound = new ContinuousSound(soundEvent, player, 1.0f);

            Minecraft.getInstance().getSoundManager().play(continuousSound);
            HashMap<SoundEvent, ContinuousSound> continuousSoundMap = new HashMap<>();
            continuousSoundMap.put(soundEvent, continuousSound);
            ACTIVE_SOUNDS.put(playerUUID, continuousSoundMap);
        }else{
            if(!ACTIVE_SOUNDS.get(playerUUID).containsKey(soundEvent)){
                ContinuousSound continuousSound = new ContinuousSound(soundEvent, player, 1.0f);

                Minecraft.getInstance().getSoundManager().play(continuousSound);
                ACTIVE_SOUNDS.get(playerUUID).put(soundEvent, continuousSound);
            }
        }
    }

    public static void playSound(Player player, SoundEvent soundEvent, float pitch){
        UUID playerUUID = player.getUUID();

        if(!ACTIVE_SOUNDS.containsKey(playerUUID)){
            ContinuousSound continuousSound = new ContinuousSound(soundEvent, player, pitch);

            Minecraft.getInstance().getSoundManager().play(continuousSound);
            HashMap<SoundEvent, ContinuousSound> continuousSoundMap = new HashMap<>();
            continuousSoundMap.put(soundEvent, continuousSound);
            ACTIVE_SOUNDS.put(playerUUID, continuousSoundMap);
        }else{
            if(!ACTIVE_SOUNDS.get(playerUUID).containsKey(soundEvent)){
                ContinuousSound continuousSound = new ContinuousSound(soundEvent, player, pitch);

                Minecraft.getInstance().getSoundManager().play(continuousSound);
                ACTIVE_SOUNDS.get(playerUUID).put(soundEvent, continuousSound);
            }
        }
    }

    public static void stopSound(Player player, SoundEvent soundEvent){
        UUID playerUUID = player.getUUID();

        if(ACTIVE_SOUNDS.containsKey(playerUUID) &&
        ACTIVE_SOUNDS.get(playerUUID).containsKey(soundEvent)){
            ACTIVE_SOUNDS.get(playerUUID).get(soundEvent).stopSound();
            ACTIVE_SOUNDS.get(playerUUID).remove(soundEvent);
        }

    }


}
