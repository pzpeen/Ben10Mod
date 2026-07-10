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
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;
import net.pzpeen.ben10mod.effects.ModEffects;
import net.pzpeen.ben10mod.races.AbstractRace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IBen10ModCapCache {

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    //Crating cache access to capabilities variables to optimize process
    @Unique
    private RaceCap _$cachedRaceCap;
    @Unique
    private PowerCap _$cachedPowerCap;

    @Override
    public RaceCap ben10Mod$getCachedRaceCap() {
        if(_$cachedRaceCap == null){
            Player player = (Player) (Object) this;
            _$cachedRaceCap = player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).resolve().orElse(null);
        }

        return _$cachedRaceCap;
    }

    @Override
    public PowerCap ben10Mod$getCachedPowerCap() {
        if(_$cachedPowerCap == null){
            Player player = (Player) (Object) this;
            _$cachedPowerCap = player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).resolve().orElse(null);
        }

        return _$cachedPowerCap;
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void onGetDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir){
        Player player = (Player) (Object) this;
        if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap() != null){
            AbstractRace race = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace();
            if(race != null){
                if(pose == Pose.CROUCHING){
                    cir.setReturnValue(EntityDimensions.scalable(race.getCustomWidth(),
                            race.getCustomHeight() - 0.6f));
                }else if (pose == Pose.STANDING || pose == Pose.SITTING){
                    cir.setReturnValue(EntityDimensions.scalable(race.getCustomWidth(),
                            race.getCustomHeight()));
                }
            }

        }


        /*
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

         */

    }

    @Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
    private void onGetStandingEyeHeight(Pose pose, EntityDimensions pSize, CallbackInfoReturnable<Float> cir){
        Player player = (Player) (Object) this;

        if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap() != null){
            AbstractRace race = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace();

            if(race != null){
                switch (pose) {
                    case SWIMMING:
                    case FALL_FLYING:
                    case SPIN_ATTACK:
                        cir.setReturnValue(0.4f);
                        break;
                    case CROUCHING:
                        cir.setReturnValue(race.getCustomEyeHeight() - 0.6f);
                        break;
                    default:
                        cir.setReturnValue(race.getCustomEyeHeight());
                        break;
                }
            }

        }



        /*
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

         */
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void onUpdateSwimming(CallbackInfo ci){
        Player player = (Player) (Object) this;

        AbstractRace race = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace();
        if(race != null && race.cannotSwim()){
            player.setSwimming(false);

            if(player.isInWater()){
                player.setSprinting(false);
            }

            ci.cancel();
        }

        /*
        player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
            if(raceCap.getRace() != null && raceCap.getRace().cannotSwim()){
                player.setSwimming(false);

                if(player.isInWater()){
                    player.setSprinting(false);
                }

                ci.cancel();
            }
        });

         */

    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickEvent(CallbackInfo ci){
        Player player = (Player) (Object) this;

        AbstractRace race = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace();
        if(race != null){
            race.tick();
        }

        if(!player.level().isClientSide()){

            if(race != null){

                if(player.tickCount % 5 == 0){
                    //Wet Effect
                    if(race.isWaterSensible()){
                        if(player.isInWater()){
                            var actualEffect = player.getEffect(ModEffects.WET.get());
                            if(actualEffect == null || actualEffect.getDuration() <= (race.getWaterEffectDuration() * 0.75)){
                                player.addEffect(race.getWaterEffect());

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

                /*
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

                 */

                }
            }


        }

    }
}
