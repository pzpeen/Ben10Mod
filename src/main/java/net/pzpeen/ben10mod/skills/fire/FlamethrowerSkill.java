package net.pzpeen.ben10mod.skills.fire;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pzpeen.ben10mod.skills.AbstractSkill;
import net.pzpeen.ben10mod.sounds.ModSounds;
import net.pzpeen.ben10mod.sounds.SoundManager;
import net.pzpeen.ben10mod.utils.ModUtilities;

import java.util.Optional;
import java.util.UUID;

public class FlamethrowerSkill extends AbstractSkill {
    UUID slowModifierUUID = UUID.randomUUID();
    AttributeModifier slowModifier = new AttributeModifier(slowModifierUUID,
            "flamethrower_slow", -0.7, AttributeModifier.Operation.MULTIPLY_TOTAL);
    int maxUsingTime = 100;
    int usingTime = 0;
    int maxRange = 10;
    float damage = 2.0f;

    boolean justStarted = true;
    double distance = maxRange;

    double clientDistance = maxRange;

    public FlamethrowerSkill(Player player){
        maxCooldown = 160;
    }


    @Override
    public boolean hold(Player player) {
        if(!getCooldown().isActive()){
            if(!player.level().isClientSide()){
                //Raycast the block and the entity and handle them each 5 ticks
                if(justStarted || player.tickCount % 5 == 0){
                    //Slowing player
                    AttributeInstance playerSpeedAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);

                    if(playerSpeedAtt != null && !playerSpeedAtt.hasModifier(slowModifier)){
                        playerSpeedAtt.addTransientModifier(slowModifier);
                    }

                    //Raycast and process
                    justStarted = false;
                    BlockHitResult impactBlock = ModUtilities.colliderBlockRaycast(player, maxRange);

                    Vec3 startPoint = player.getEyePosition();

                    Vec3 endPoint = impactBlock.getType() == HitResult.Type.BLOCK ?
                            impactBlock.getLocation() : startPoint.add(player.getLookAngle().scale(maxRange));

                    distance = startPoint.distanceTo(endPoint);

                    LivingEntity target = ModUtilities.entityRaycast(player, distance);

                    if(target != null){
                        distance = Math.sqrt(startPoint.distanceToSqr(target.getX(), target.getY(), target.getZ()));
                        DamageSource flamethrowerSource = new DamageSource(
                                player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                        .getHolderOrThrow(DamageTypes.FIREBALL), target, player);
                        target.hurt(flamethrowerSource, damage);
                        target.setSecondsOnFire(5);

                        target.knockback(0.15f, startPoint.x - target.getX(), startPoint.z - target.getZ());
                    }else{
                        BlockPos blockPos = impactBlock.getBlockPos();
                        Direction direction = impactBlock.getDirection();
                        BlockPos blockFace = blockPos.relative(direction);

                        if(player.level().isEmptyBlock(blockFace)){
                            player.level().setBlockAndUpdate(blockFace, BaseFireBlock.getState(player.level(), blockFace));
                        }

                    }

                }


            }else{
                if(player.tickCount % 2 == 0){
                    BlockHitResult impactBlock = ModUtilities.colliderBlockRaycast(player, maxRange);
                    Vec3 startPoint = player.getEyePosition();
                    Vec3 endPoint = impactBlock.getType() == HitResult.Type.BLOCK ?
                            impactBlock.getLocation() : startPoint.add(player.getLookAngle().scale(maxRange));

                    clientDistance = startPoint.distanceTo(endPoint);

                    //Handle the particle effect every 2 ticks
                    startPoint = player.getEyePosition().subtract(0f, 0.5f, 0.0f);
                    startPoint.add(player.getLookAngle().x * 0.3f, 0f, player.getLookAngle().z * 0.3f);

                    double baseSpeed = Math.min(2.0/3.0, (clientDistance / 30.0)*2.0) ;

                    int density = 15;
                    double coneAperture = Math.max(0.5/clientDistance, 0.1) ;

                    for(int i = 0; i < density; i++){
                        double xScattering = player.level().random.nextGaussian() * coneAperture;
                        double yScattering = player.level().random.nextGaussian() * coneAperture;
                        double zScattering = player.level().random.nextGaussian() * coneAperture;

                        double xSpd = player.getLookAngle().x + xScattering;
                        double ySpd = player.getLookAngle().y + yScattering;
                        double zSpd = player.getLookAngle().z + zScattering;

                        player.level().addParticle(
                                ParticleTypes.FLAME,
                                startPoint.x, startPoint.y, startPoint.z,
                                xSpd*baseSpeed, ySpd*baseSpeed, zSpd*baseSpeed
                        );
                    }


                    //Sound
                    SoundManager.playSound(player, ModSounds.FLAMETHROWER.get());
                }

            }
            //Check if the skill is on end then start the cooldown and reset some variables
            usingTime++;
            if(usingTime >= maxUsingTime){
                this.startCooldown();
                justStarted = true;
                distance = maxRange;
                usingTime = 0;
                if(!player.level().isClientSide()){
                    AttributeInstance playerSpeedAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);

                    if(playerSpeedAtt != null){
                        playerSpeedAtt.removeModifier(slowModifier);
                    }
                }else{
                    SoundManager.stopSound(player, ModSounds.FLAMETHROWER.get());
                }
            }
            return true;

        }

        return false;
    }

    @Override
    public boolean release(Player player) {
        if(!this.cooldown.isActive()){
            this.getCooldown().start(Math.max((int)(usingTime*1.6), 40));
            justStarted = true;
            distance = maxRange;
            clientDistance = maxRange;
            usingTime = 0;
            if(!player.level().isClientSide()){
                AttributeInstance playerSpeedAtt = player.getAttribute(Attributes.MOVEMENT_SPEED);

                if(playerSpeedAtt != null){
                    playerSpeedAtt.removeModifier(slowModifier);
                }
            }else{
                SoundManager.stopSound(player, ModSounds.FLAMETHROWER.get());
            }

        }
        return super.release(player);
    }

    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath("ben10mod", "textures/skill/flamethrower.png");
    }
}
