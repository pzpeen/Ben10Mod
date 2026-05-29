package net.pzpeen.ben10mod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class ModUtilities {

    public static class cooldown{
        double lastTimeMillis;
        double cdTime;

        public cooldown(double cdTime){
            this.cdTime = cdTime;
            this.lastTimeMillis = 0;
        }

        public void setLastTimeUsed(){
            this.lastTimeMillis = System.currentTimeMillis();
        }

        public boolean isCharged(){
            return (System.currentTimeMillis() - this.lastTimeMillis) >= this.cdTime;
        }

    }
}
