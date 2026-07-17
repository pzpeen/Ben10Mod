package net.pzpeen.ben10mod.skills.physical;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pzpeen.ben10mod.skills.AbstractSkill;
import net.pzpeen.ben10mod.utils.ModUtilities;

public class DownSlamSkill extends AbstractSkill {
    private final Vec3 upSpeed = new Vec3(0, 0.6, 0);
    private final Vec3 downSpeed = new Vec3(0, -1.8, 0);

    private float damage = 17.0f;
    private float radius = 1.6f;
    private final boolean putFire;

    private final int upTime = 10;
    private int tickCount = 0;

    public DownSlamSkill(boolean putFire){
        maxCooldown = 240;
        this.putFire = putFire;
    }

    @Override
    public boolean use(Player player) {
        if(!cooldown.isActive()){
            player.setDeltaMovement(upSpeed);

            startCooldown();

            tickCount = 0;
            ticking = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean tick(Player player) {
        tickCount++;
        if(tickCount > upTime){
            if(tickCount > 117){
                ticking = false;
                return false;
            }

            if(!player.onGround()){
                player.setDeltaMovement(downSpeed);

                player.fallDistance = 0;
            }else{
                if(!player.level().isClientSide()){
                    ModUtilities.noDamageExplode(player, player.getX(), player.getY(), player.getZ(), radius);

                    double damageRadius = radius + 2;
                    AABB damageBox = new AABB(
                            player.getX() - damageRadius, player.getY() - damageRadius, player.getZ() - damageRadius,
                            player.getX() + damageRadius, player.getY() + damageRadius, player.getZ() + damageRadius);

                    for(LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, damageBox)){
                        if(target != player){
                            target.hurt(player.damageSources().playerAttack(player), damage + ((tickCount - upTime) / 8f));
                            if(putFire){
                                target.setSecondsOnFire(5);
                            }
                            target.knockback(1.0, player.getX() - target.getX(), player.getZ() - target.getZ());
                            Vec3 targetActualMovement = target.getDeltaMovement();
                            target.setDeltaMovement(targetActualMovement.x, 1.0, targetActualMovement.z);
                        }
                    }
                }


                ticking = false;
            }

        }

        return true;
    }

    public int getTickCount() {
        return tickCount;
    }

    public int getUpTime() {
        return upTime;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "textures/skill/down_slam.png");
    }
}
