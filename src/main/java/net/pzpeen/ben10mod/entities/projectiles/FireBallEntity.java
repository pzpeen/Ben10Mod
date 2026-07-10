package net.pzpeen.ben10mod.entities.projectiles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.pzpeen.ben10mod.entities.ModEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FireBallEntity extends AbstractHurtingProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final float damage;
    private final float explosionStrength = 1.0f;

    public FireBallEntity(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        damage = 4.0f;
    }

    public FireBallEntity(float pDamage, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, Level pLevel) {
        super(ModEntities.FIRE_BALL.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
        this.damage = pDamage;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(!this.level().isClientSide()){
            Entity target = pResult.getEntity();
            Entity owner = this.getOwner();

            target.setSecondsOnFire(7);
            DamageSource fireBallDamageSource = new DamageSource(
                    this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(DamageTypes.FIREBALL), target, owner);
            target.hurt(fireBallDamageSource, damage);
            this.level().explode(this, getX(), getY(), getZ(), explosionStrength,
                    Level.ExplosionInteraction.NONE);
            this.discard();
        }

    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(!this.level().isClientSide()){

            this.level().explode(this, getX(), getY(), getZ(), explosionStrength, true,
                    Level.ExplosionInteraction.MOB);

            this.discard();
        }

    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
