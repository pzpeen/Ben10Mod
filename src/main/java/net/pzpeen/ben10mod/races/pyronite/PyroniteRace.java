package net.pzpeen.ben10mod.races.pyronite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.effects.ModEffects;
import net.pzpeen.ben10mod.races.AbstractRace;
import net.pzpeen.ben10mod.races.AlienArmRenderer;
import net.pzpeen.ben10mod.skills.AbstractSkill;
import net.pzpeen.ben10mod.skills.fire.FireBallSkill;
import net.pzpeen.ben10mod.skills.fire.FireExtinguishSkill;
import net.pzpeen.ben10mod.skills.fire.FlamethrowerSkill;
import net.pzpeen.ben10mod.skills.physical.DownSlamSkill;
import net.pzpeen.ben10mod.skills.physical.FlySkill;
import net.pzpeen.ben10mod.sounds.ModSounds;
import net.pzpeen.ben10mod.sounds.SoundManager;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class PyroniteRace extends AbstractRace {
    public static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "pyronite");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final PyroniteRenderer RENDERER = new PyroniteRenderer();
    private static final AlienArmRenderer ARM_RENDERER = new PyroniteArmRenderer(id);

    private final FireBallSkill skill1 = new FireBallSkill();
    private final FireExtinguishSkill skill2 = new FireExtinguishSkill();
    private final FlamethrowerSkill skill3 = new FlamethrowerSkill(player);
    private final FlySkill skill4A = new FlySkill();
    private final DownSlamSkill skill4B = new DownSlamSkill(true);
    private AbstractSkill skill4OnUse = skill4A;

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

    public boolean isWet(){
        return this.player.hasEffect(ModEffects.WET.get()) || player.isInWater();
    }

    @Override
    public float getBaseDamage() {
        return 2.5f;
    }

    @Override
    public void doBareHandHit(LivingHurtEvent event) {
        assert event.getSource().getEntity() != null;
        if(this.isWet()) return;
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
    public float getFallDamageResistance() {
        return 15f;
    }

    @Override
    public float getFallDamageMultiplier() {
        return 0.1f;
    }

    @Override
    public AbstractSkill getSkill1() {
        return skill1;
    }

    @Override
    public void useSkill1() {
        if(!isWet()){
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
    public FireExtinguishSkill getSkill2() {
        return skill2;
    }

    @Override
    public void useSkill2() {

        if(!player.isInWater()){
            if(skill2.use(player)){
                if(player.level() instanceof ServerLevel serverLevel){
                    if(skill2.hasExtinguishedFire()){
                        if(player.hasEffect(ModEffects.WET.get())){
                            player.removeEffect(ModEffects.WET.get());
                        }
                    }
                    if(!isWet()){
                        serverLevel.sendParticles(
                                ParticleTypes.FLAME,
                                player.getX(),
                                player.getY() + 1,
                                player.getZ(),
                                15,
                                0.2,
                                0.2,
                                0.2,
                                0.1
                        );
                        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.3f);
                    }else{
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

        }else{
            if(player.level() instanceof  ServerLevel serverLevel){
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
    public FlamethrowerSkill getSkill3() {
        return skill3;
    }

    @Override
    public void useSkill3() {
        if(isWet()){
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
    public void holdSkill3() {
        if(!isWet()){
            skill3.hold(player);
        }else{
            releaseSkill3();
        }
    }

    @Override
    public void releaseSkill3() {
        skill3.release(player);
    }

    public FlySkill getSkill4A(){
        return skill4A;
    }

    public DownSlamSkill getSkill4B(){
        return skill4B;
    }

    @Override
    public AbstractSkill getSkill4() {
        return skill4OnUse;
    }

    @Override
    public void useSkill4() {
        if(isWet()){
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
        } else if (player.onGround() && !skill4B.getCooldown().isActive()) {
            skill4OnUse = skill4A;
        }else if(!player.onGround() && !skill4A.getCooldown().isActive()) {
            skill4OnUse = skill4B;
            if(skill4B.use(player)){
                if(player.level().isClientSide()){
                    for(int i = 0; i < 10; i++){
                        double xOffSet = (player.level().random.nextDouble() *2.0) -1.0;
                        double yOffSet = player.level().random.nextDouble();
                        double zOffSet = (player.level().random.nextDouble() *2.0) -1.0;

                        double xSpd = xOffSet*0.05f;
                        double ySpd = yOffSet * 0.01f;
                        double zSpd = zOffSet*-0.05f;

                        player.level().addParticle(
                                ParticleTypes.FLAME,
                                player.getX() + (xOffSet * 0.7), player.getY() - 0.3, player.getZ() + (zOffSet * 0.7),
                                xSpd, ySpd, zSpd
                        );
                    }

                }else{
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0f, 1.4f);
                }

            }
        }
    }

    @Override
    public void holdSkill4() {
        if(!isWet() && skill4OnUse.equals(skill4A)){
            //Fly Skill process
            if(skill4A.hold(player)){
                if(!player.level().isClientSide()){
                    AABB impactArea = player.getBoundingBox().inflate(0.5f);

                    List<LivingEntity> targets = player.level().getEntitiesOfClass(
                            LivingEntity.class,
                            impactArea,
                            entity -> entity != player);

                    for(LivingEntity entity : targets){
                        if(entity.hurt(player.damageSources().playerAttack(player), 7.0f)){
                            entity.setSecondsOnFire(5);

                            entity.knockback(0.8d,
                                    player.getX() - entity.getX(),
                                    player.getZ() - entity.getZ());

                        }



                    }

                }else{
                    SoundManager.playSound(player, ModSounds.FLAMETHROWER.get(), 0.7f);
                    for(int i = 0; i < 5; i++){
                        double xOffSet = player.level().random.nextGaussian();
                        double yOffSet = player.level().random.nextGaussian();
                        double zOffSet = player.level().random.nextGaussian();

                        double xSpd = (player.getLookAngle().x + xOffSet)*-0.1f;
                        double ySpd = yOffSet * 0.05f;
                        double zSpd = (player.getLookAngle().z + zOffSet)*-0.1f;

                        player.level().addParticle(
                                ParticleTypes.FLAME,
                                player.getX(), player.getY() - 0.6, player.getZ(),
                                xSpd, ySpd, zSpd
                        );
                    }

                }

            }
        }else{
            releaseSkill4();
        }

    }

    @Override
    public void releaseSkill4() {
        if(skill4OnUse.equals(skill4A)){
            if(player.level().isClientSide()){
                SoundManager.stopSound(player, ModSounds.FLAMETHROWER.get());
            }
            skill4A.release(player);
        }
    }

    @Override
    public void tickSkill4() {
        if(skill4B.tick(player)){
            if(skill4B.getTickCount() > skill4B.getUpTime()){
                if(player.level().isClientSide()){
                    for(int i = 0; i < 10; i++){
                        double xOffSet = (player.level().random.nextDouble() *2.0) -1.0;

                        double zOffSet = (player.level().random.nextDouble() *2.0) -1.0;

                        player.level().addParticle(
                                ParticleTypes.FLAME,
                                player.getX() + (xOffSet * 0.7), player.getY() - 0.1, player.getZ() + (zOffSet * 0.7),
                                0, 0.02, 0
                        );
                    }
                }
            }
        }
    }

    @Override
    public AlienArmRenderer getAlienArmRenderer() {
        return ARM_RENDERER;
    }

    @Override
    public boolean customAlienArmAnimations(RenderHandEvent event, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, AlienArmRenderer renderer) {
        if(skill3.isHolding() && !skill3.getCooldown().isActive()){
            event.cancel();
            if(event.getHand() == InteractionHand.MAIN_HAND){
                poseStack.pushPose();

                poseStack.translate(0.15f, -1.4f, -0.8f);

                poseStack.mulPose(Axis.XP.rotationDegrees(20.0f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(15.0f));

                renderer.render(poseStack, this, bufferSource, null,
                        bufferSource.getBuffer(
                                renderer.getRenderType(this, renderer.getTextureLocation(this),
                                        null, partialTick)), packedLight);

                poseStack.popPose();



                poseStack.pushPose();

                poseStack.scale(1.0f, 1.0f, 1.0f);

                poseStack.translate(0.1f, -0.55f, -0.5f);

                poseStack.mulPose(Axis.XP.rotationDegrees(20.0f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-195.0f));

                renderer.render(poseStack, this, bufferSource, null,
                        bufferSource.getBuffer(
                                renderer.getRenderType(this, renderer.getTextureLocation(this),
                                        null, partialTick)), packedLight);

                poseStack.popPose();

            }

            return true;
        }

        return false;
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

            if(this.skill4A.isOnUse()){
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("fly_skill"));
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
