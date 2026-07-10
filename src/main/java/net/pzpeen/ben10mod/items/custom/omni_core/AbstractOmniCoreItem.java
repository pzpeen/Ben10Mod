package net.pzpeen.ben10mod.items.custom.omni_core;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;
import net.pzpeen.ben10mod.powers.OmnitrixPower;
import net.pzpeen.ben10mod.races.RacesRegistries;
import net.pzpeen.ben10mod.sounds.ModSounds;
import org.joml.Vector3f;

import java.util.UUID;

public abstract class AbstractOmniCoreItem extends Item {
    public AbstractOmniCoreItem(Properties pProperties) {
        super(pProperties);
    }

    public void transform(Player player, ResourceLocation raceId){

        if(raceId != null && raceId != RacesRegistries.humanRaceID){
            PowerCap powerCap = ((IBen10ModCapCache)player).ben10Mod$getCachedPowerCap();
            if(powerCap != null){
                if(powerCap.getPower() instanceof OmnitrixPower power){
                    if(player.level() instanceof ServerLevel level){
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ModSounds.OMNITRIX_TRANSFORMATION.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

                    }
                    power.startBrightAnimationTick();
                }
            }
            RaceCap raceCap = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap();
            if(raceCap != null){
                raceCap.setRace(raceId, player);
            }

            /*
            player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {
                if(powerCap.getPower() instanceof OmnitrixPower power){
                    if(player.level() instanceof ServerLevel level){
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ModSounds.OMNITRIX_TRANSFORMATION.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

                    }
                    power.startBrightAnimationTick();
                }

            });

            player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                raceCap.setRace(raceId, player);
            });

             */

        }else{
            if(player.level() instanceof ServerLevel level){
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.OMNITRIX_TRANSFORMATION_FAIL.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

            }
        }


    }
}
