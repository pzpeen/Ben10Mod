package net.pzpeen.ben10mod.skills.fire;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.pzpeen.ben10mod.skills.AbstractSkill;

import java.util.List;

public class FireExtinguishSkill extends AbstractSkill {
    int radius = 3;
    boolean extinguishFire = false;

    public FireExtinguishSkill(){
        maxCooldown = 40;
    }

    public boolean hasExtinguishedFire(){
        return extinguishFire;
    }

    @Override
    public boolean use(Player player) {
        if(!cooldown.isActive()){
            extinguishFire = false;
            Level level = player.level();
            if(!level.isClientSide()){

                BlockPos playerPos = player.blockPosition();

                for(BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -radius, -radius),
                        playerPos.offset(radius, radius, radius))){
                    BlockState blockState = level.getBlockState(pos);

                    if(blockState.getBlock() instanceof BaseFireBlock){
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        extinguishFire = true;
                    }

                }

                AABB entityRadius = player.getBoundingBox().inflate(radius);
                List<LivingEntity> entityList = level.getEntitiesOfClass(LivingEntity.class, entityRadius);
                for(LivingEntity entity : entityList){
                    if(entity.isOnFire()){
                        entity.extinguishFire();
                        extinguishFire = true;
                    }

                }

            }

            this.startCooldown();
            return true;

        }


        return false;
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "textures/skill/fire_extinguish.png");
    }
}
