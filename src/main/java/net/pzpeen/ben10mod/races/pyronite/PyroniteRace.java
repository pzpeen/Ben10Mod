package net.pzpeen.ben10mod.races.pyronite;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.pzpeen.ben10mod.races.AbstractRace;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PyroniteRace extends AbstractRace {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean isOnWater;
    private static final PyroniteRenderer RENDERER = new PyroniteRenderer();

    public PyroniteRace() {
        super("pyronite");
    }

    public boolean isOnWater(){
        return this.isOnWater;
    }


    //MODEL AND ANIMATIONS
    @Override
    public void render(PoseStack poseStack, Player player, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        this.isOnWater = player.isInWater();

        RENDERER.renderAlien(this, player, poseStack, bufferSource, packedLight, partialTick);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "pyronite_locomotion_controller", 2, state -> {
            Entity e = state.getData(DataTickets.ENTITY);

            if(e instanceof Player player){
                boolean onAir = !player.onGround() && !player.isPassenger() && !player.getAbilities().flying;
                double deltaYSpeed = player.getDeltaMovement().y;

                if (onAir && deltaYSpeed < -0.4){
                    return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("falling"));
                }

                if(player.isCrouching()){
                    if (state.isMoving()){
                        return state.setAndContinue(RawAnimation.begin().thenLoop("crouching_walking"));
                    }
                    return state.setAndContinue(RawAnimation.begin().thenLoop("crouching_idle"));
                }

                if(state.isMoving()){
                    if (player.isSprinting()){
                        return state.setAndContinue(RawAnimation.begin().thenLoop("sprinting"));
                    }
                    return state.setAndContinue(RawAnimation.begin().thenLoop("walking"));
                }else{
                    return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
                }

            }

            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));

        controllers.add(new AnimationController<GeoAnimatable>(this, "pyronite_pose_controller", 2, state -> {
            Entity e = state.getData(DataTickets.ENTITY);
            if (e instanceof  LivingEntity entity){
                if (entity.isPassenger()){
                    return state.setAndContinue(RawAnimation.begin().thenLoop("sitting"));
                }
                if(entity.isSleeping() || entity.isVisuallySwimming()){
                    return state.setAndContinue(RawAnimation.begin().thenLoop("lying"));
                }
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<GeoAnimatable>(this, "pyronite_arm_controller", 2, state -> {
            Entity e = state.getData(DataTickets.ENTITY);
            RawAnimation using = RawAnimation.begin().thenPlayAndHold("using");

            if (e instanceof Player player){

                if (player.swinging && !player.isSleeping()){
                    return state.setAndContinue(RawAnimation.begin().thenLoop("swinging").thenWait(2));
                }
                if (player.isUsingItem()){
                    return state.setAndContinue(using);
                }

            }
            if(state.isCurrentAnimation(using)){
                state.resetCurrentAnimation();
            }
            return PlayState.STOP;
        }));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object object) {
        return software.bernie.geckolib.util.RenderUtils.getCurrentTick();
    }
}
