package net.pzpeen.ben10mod.skills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pzpeen.ben10mod.utils.ModUtilities;

public abstract class AbstractSkill {
    protected int maxCooldown;
    protected ModUtilities.TickTimer cooldown = new ModUtilities.TickTimer();

    public void setMaxCooldown(int maxCooldown){
        this.maxCooldown = maxCooldown;
    }
    public int getMaxCooldown(){
        return maxCooldown;
    }
    public ModUtilities.TickTimer getCooldown(){
        return cooldown;
    }
    public void startCooldown(){
        cooldown.start(maxCooldown);
    }

    public abstract boolean use(Player player);

    public abstract ResourceLocation getIcon();

}
