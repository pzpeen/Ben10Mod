package net.pzpeen.ben10mod.skills.physical;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.pzpeen.ben10mod.skills.AbstractSkill;

public class FlySkill extends AbstractSkill {
    private float speed = 0.8f;//Speed per tick
    private int count = 0;

    public FlySkill() {
        maxCooldown = 60;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public boolean hold(Player player) {
        if(!cooldown.isActive()){
            Vec3 lookDirection = player.getLookAngle();

            Vec3 movement = lookDirection.scale(speed);

            player.setDeltaMovement(movement);

            player.fallDistance = 0f;

            if(count > 20){
                if(player.onGround()){
                    count = 0;
                    startCooldown();
                }
            }else{
                count++;
            }

            return true;

        }

        return false;
    }

    @Override
    public boolean release(Player player) {
        if(!cooldown.isActive()){
            count = 0;
            startCooldown();
        }
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "textures/skill/fly.png");
    }
}
