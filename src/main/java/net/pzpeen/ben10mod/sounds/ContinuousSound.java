package net.pzpeen.ben10mod.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ContinuousSound extends AbstractTickableSoundInstance {
    private final Player player;
    private boolean stopping = false;
    private int fadeInCount = 0;

    protected ContinuousSound(SoundEvent soundEvent, Player player, float pitch) {
        super(soundEvent, SoundSource.PLAYERS, player.level().random);
        this.player = player;
        this.pitch = pitch;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.05f;
        this.relative = false;

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public void tick() {
        if(player.isRemoved() || !player.isAlive()){
            this.stop();
            return;
        }

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();


        if(stopping){
            this.volume -= 0.1f;
            if(volume <= 0f){
                this.stop();
            }
        }else if(fadeInCount < 20){
            this.volume += 1f/20f;
            fadeInCount++;
        }

    }

    public void stopSound(){
        this.stopping = true;
    }

}
