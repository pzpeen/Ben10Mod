package net.pzpeen.ben10mod.mixins;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;
import net.pzpeen.ben10mod.effects.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void onGetDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir){
        Player player = (Player) (Object) this;

        player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
            if(raceCap.getRace() != null){
                if(pose == Pose.CROUCHING){
                    cir.setReturnValue(EntityDimensions.scalable(raceCap.getRace().getCustomWidth(),
                            raceCap.getRace().getCustomHeight() - 0.6f));
                }else if (pose == Pose.STANDING || pose == Pose.SITTING){
                    cir.setReturnValue(EntityDimensions.scalable(raceCap.getRace().getCustomWidth(),
                            raceCap.getRace().getCustomHeight()));
                }
            }
        });

    }

    @Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
    private void onGetStandingEyeHeight(Pose pose, EntityDimensions pSize, CallbackInfoReturnable<Float> cir){
        Player player = (Player) (Object) this;

        player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
            if(raceCap.getRace() != null){
                switch (pose) {
                    case SWIMMING:
                    case FALL_FLYING:
                    case SPIN_ATTACK:
                        cir.setReturnValue(0.4f);
                        break;
                    case CROUCHING:
                        cir.setReturnValue(raceCap.getRace().getCustomEyeHeight() - 0.6f);
                        break;
                    default:
                        cir.setReturnValue(raceCap.getRace().getCustomEyeHeight());
                        break;
                }
            }
        });
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void onUpdateSwimming(CallbackInfo ci){
        Player player = (Player) (Object) this;

        player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
            if(raceCap.getRace() != null && raceCap.getRace().cannotSwim()){
                player.setSwimming(false);

                if(player.isInWater()){
                    player.setSprinting(false);
                }

                ci.cancel();
            }
        });

    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickEvent(CallbackInfo ci){
        Player player = (Player) (Object) this;

        if(!player.level().isClientSide()){
            if(player.tickCount % 10 == 0){
                player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                    if(raceCap.getRace() != null && raceCap.getRace().isWaterSensible()){
                        if(player.isInWater()){
                            var actualEffect = player.getEffect(ModEffects.WET.get());
                            if(actualEffect == null || actualEffect.getDuration() <= (raceCap.getRace().getWaterEffectDuration() * 0.75)){
                                player.addEffect(raceCap.getRace().getWaterEffect());

                                if(actualEffect == null){
                                    if(player.level() instanceof ServerLevel serverLevel){
                                        serverLevel.sendParticles(
                                                ParticleTypes.LARGE_SMOKE,
                                                player.getX(),
                                                player.getY() + 1,
                                                player.getZ(),
                                                15,
                                                0.2,
                                                0.2,
                                                0.2,
                                                0.05
                                        );
                                        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                                SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 1.0f);


                                    }
                                }

                            }

                        }

                    }

                });

            }
        }

    }
}
