package net.pzpeen.ben10mod.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;


public class ModUtilities {

    public static LivingEntity entityRaycast(Player player, double maxDistanceInBlocks){
        Level level = player.level();

        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle();
        Vec3 end = start.add(direction.scale(maxDistanceInBlocks));

        AABB searchArea = player.getBoundingBox().expandTowards(direction.scale(maxDistanceInBlocks)).inflate(1.0f);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchArea,
                e -> e != player && player.isAlive());

        LivingEntity closestEntity = null;
        double smallestDistanceToSqr = Double.MAX_VALUE;

        for(LivingEntity entity : entities){
            AABB hitbox = entity.getBoundingBox().inflate(0.4f);
            Optional<Vec3> impactPoint = hitbox.clip(start, end);

            if(impactPoint.isPresent()){
                double distanceToSqr = start.distanceToSqr(impactPoint.get());
                if(distanceToSqr < smallestDistanceToSqr){
                    smallestDistanceToSqr = distanceToSqr;
                    closestEntity = entity;
                }

            }


        }

        return closestEntity;


    }

    public static BlockHitResult colliderBlockRaycast(Player player, double maxDistanceInBlocks){
        Level level = player.level();

        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle();
        Vec3 end = start.add(direction.scale(maxDistanceInBlocks));

        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player);

        BlockHitResult blockHitResult = level.clip(context);

        return blockHitResult;

    }

    public static class Cooldown {
        double lastTimeMillis;
        double cdTime;

        public Cooldown(double cdTime){
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

    public static class TickTimer {

        private int remainingTicks = -1;
        private int maxTicks = 0;

        public void start(int ticks){
            this.maxTicks = ticks;
            this.remainingTicks = ticks;
        }

        public void tick(){
            if(this.remainingTicks >= 0){
                //System.out.println("DECREASING TICK");
                this.remainingTicks--;
            }
        }

        public boolean isActive(){
            return this.remainingTicks > 0;
        }

        public int getRemainingTicks(){
            return this.remainingTicks;
        }

        public float getProgress(){
            if(this.maxTicks <= 0) return 0.0f;
            return (float) this.remainingTicks / this.maxTicks;
        }

        public int getMaxTicks(){
            return this.maxTicks;
        }

        public void stop(){
            this.maxTicks = 0;
            this.remainingTicks = -1;
        }

    }

}
