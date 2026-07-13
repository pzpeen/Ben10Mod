package net.pzpeen.ben10mod.skills.fire;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.pzpeen.ben10mod.entities.projectiles.FireBallEntity;
import net.pzpeen.ben10mod.skills.AbstractSkill;

public class FireBallSkill extends AbstractSkill {
    float damage = 8.0f;
    public FireBallSkill(){
        maxCooldown = 60;
    }

    public void setDamage(float damage){
        this.damage = damage;
    }

    //Returns true if used, false if not and is called only in server side.
    @Override
    public boolean use(Player player) {
        if(!cooldown.isActive()){
            if(!player.level().isClientSide()){
                Vec3 lookAngle = player.getLookAngle();
                double accelX = lookAngle.x * 0.2D;
                double accelY = lookAngle.y * 0.2D;
                double accelZ = lookAngle.z * 0.2D;

                FireBallEntity fireBall = new FireBallEntity(damage, player, accelX, accelY, accelZ, player.level());

                fireBall.setPos(player.getX() + lookAngle.x, player.getY() + player.getEyeHeight() - 0.4f, player.getZ() + lookAngle.z);

                player.level().addFreshEntity(fireBall);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);

            }
            this.startCooldown();
            return true;
        }
        return false;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "textures/skill/fire_ball.png");
    }
}
