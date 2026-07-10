package net.pzpeen.ben10mod.races.pyronite;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.effects.ModEffects;
import net.pzpeen.ben10mod.races.AbstractRace;
import net.pzpeen.ben10mod.races.AlienArmRenderer;
import net.pzpeen.ben10mod.skills.AbstractSkill;
import net.pzpeen.ben10mod.skills.fire.FireBallSkill;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PyroniteRace extends AbstractRace {
    public static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "pyronite");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final PyroniteRenderer RENDERER = new PyroniteRenderer();
    private static final AlienArmRenderer ARM_RENDERER = new PyroniteArmRenderer(id);

    private final FireBallSkill skill1 = new FireBallSkill();

    public PyroniteRace() {
        super();
    }

    @Override
    public ResourceLocation getID() {
        return id;
    }

    @Override
    public float getCustomHeight() {
        return 2.5f;
    }

    @Override
    public float getCustomEyeHeight() {
        return 2.1f;
    }

    public boolean isOnWater(){
        return this.player.hasEffect(ModEffects.WET.get()) || player.isInWater();
    }

    @Override
    public float getBaseDamage() {
        return 2.5f;
    }

    @Override
    public void doBareHandHit(LivingHurtEvent event) {
        assert event.getSource().getEntity() != null;
        if(this.isOnWater()) return;
        event.getEntity().setSecondsOnFire(3);
    }

    @Override
    public float getBaseArmor() {
        return 12.0f;
    }

    @Override
    public boolean isFireResistent() {
        return true;
    }

    @Override
    public boolean isWaterSensible() {
        return true;
    }

    @Override
    public boolean isWaterWeak() {
        return true;
    }

    @Override
    public int getWaterEffectDuration() {
        return (15 * 20);
    }

    @Override
    public boolean cannotSwim() {
        return true;
    }

    @Override
    public AbstractSkill getSkill1() {
        return skill1;
    }

    @Override
    public void useSkill1() {
        if(!isOnWater()){
            if(skill1.use(player)){
                player.swing(InteractionHand.MAIN_HAND);
            }
        }else{
            if(!player.level().isClientSide()){
                ServerLevel serverLevel = (ServerLevel) player.level();
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

    @Override
    public AlienArmRenderer getAlienArmRenderer() {
        return ARM_RENDERER;
    }

    @Override
    public GeoRenderer<? extends AbstractRace> getRenderer() {
        return RENDERER;
    }

    //MODEL AND ANIMATIONS
    @Override
    public void render(PoseStack poseStack, Player player, MultiBufferSource bufferSource, int packedLight, float partialTick) {

        RENDERER.renderAlien(this, player, poseStack, bufferSource, packedLight, partialTick);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "pyronite_locomotion_controller", 2, state -> {
            if(isInFirstPersonView()){
                return PlayState.STOP;
            }

            Entity e = state.getData(DataTickets.ENTITY);

            if(e instanceof Player pPlayer){
                boolean onAir = !pPlayer.onGround() && !pPlayer.isPassenger() && !pPlayer.getAbilities().flying;
                double deltaYSpeed = pPlayer.getDeltaMovement().y;

                if (onAir && deltaYSpeed < -0.4){
                    return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("falling"));
                }

                if(pPlayer.isCrouching()){
                    if (state.isMoving()){
                        return state.setAndContinue(RawAnimation.begin().thenLoop("crouching_walking"));
                    }
                    return state.setAndContinue(RawAnimation.begin().thenLoop("crouching_idle"));
                }

                if(state.isMoving()){
                    if (pPlayer.isSprinting()){
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
            if(isInFirstPersonView()){
                return PlayState.STOP;
            }

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
            if(isInFirstPersonView()){
                return PlayState.STOP;
            }

            Entity e = state.getData(DataTickets.ENTITY);
            RawAnimation using = RawAnimation.begin().thenPlayAndHold("using");
            RawAnimation swinging = RawAnimation.begin().thenLoop("swinging");

            if (e instanceof Player pPlayer){

                if ((pPlayer.swinging) && !pPlayer.isSleeping()){
                    return state.setAndContinue(swinging);
                }
                if (pPlayer.isUsingItem()){
                    return state.setAndContinue(using);
                }

            }

            if(state.isCurrentAnimation(swinging) && state.getAnimationTick() < (0.29f*20.0f)+1.9){
                return PlayState.CONTINUE;
            }else if(state.isCurrentAnimation(using)){
                state.resetCurrentAnimation();
            }

            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<GeoAnimatable>(this, "first_person_pyronite_arm_controller", 2, state -> {
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
