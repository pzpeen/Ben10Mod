package net.pzpeen.ben10mod.skills;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pzpeen.ben10mod.utils.ModUtilities;

public abstract class AbstractSkill {
    protected int maxCooldown;
    protected ModUtilities.TickTimer cooldown = new ModUtilities.TickTimer();
    private boolean holding = false;
    protected boolean ticking = false;

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

    public boolean isHolding(){
        return holding;
    }
    public void setHolding(boolean holding){
        this.holding = holding;
    }

    public boolean isOnUse(){
        if(isHolding() && !cooldown.isActive() || ticking){
            return true;
        }
        return false;
    }

    public boolean isTicking() {
        return ticking;
    }

    public boolean use(Player player){return false;};

    public boolean hold(Player player){return false;}

    public boolean release(Player player){return false;}

    public boolean tick(Player player){
        return false;
    }

    public abstract ResourceLocation getIcon();

}
