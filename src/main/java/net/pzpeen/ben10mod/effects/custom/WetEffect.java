package net.pzpeen.ben10mod.effects.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;
import net.pzpeen.ben10mod.effects.ModEffects;

import java.util.UUID;

public class WetEffect extends MobEffect {
    private static final UUID DAMAGE_MODIFIER_UUID = UUID.randomUUID();
    private static final UUID ARMOR_MODIFIER_UUID = UUID.randomUUID();
    private static final UUID BUFF_DAMAGE_MODIFIER_UUID = UUID.randomUUID();
    private static final UUID BUFF_ARMOR_MODIFIER_UUID = UUID.randomUUID();

    private final AttributeModifier buffDamageModifier;
    private final AttributeModifier debuffDamageModifier;
    private final AttributeModifier buffArmorModifier;
    private final AttributeModifier debuffArmorModifier;

    public WetEffect() {
        super(MobEffectCategory.NEUTRAL, 0x3498db);

        this.buffDamageModifier = new AttributeModifier(BUFF_DAMAGE_MODIFIER_UUID, "wet_buff_damage_modifier",
                0.3, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.debuffDamageModifier = new AttributeModifier(DAMAGE_MODIFIER_UUID, "wet_damage_modifier",
                -0.3, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.buffArmorModifier = new AttributeModifier(BUFF_ARMOR_MODIFIER_UUID, "wet_buff_armor_modifier",
                0.3, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.debuffArmorModifier = new AttributeModifier(ARMOR_MODIFIER_UUID, "wet_armor_modifier",
                -0.3, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if(pLivingEntity instanceof Player player){
            player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                if(raceCap.getRace() != null){
                    if(raceCap.getRace().isWaterWeak()){
                        var damageAtrInstance = pAttributeMap.getInstance(Attributes.ATTACK_DAMAGE);
                        if(damageAtrInstance != null && !damageAtrInstance.hasModifier(this.debuffDamageModifier)){
                            damageAtrInstance.addTransientModifier(this.debuffDamageModifier);
                        }

                        var armorAtrInstance = pAttributeMap.getInstance(Attributes.ARMOR);
                        if(armorAtrInstance != null && !armorAtrInstance.hasModifier(this.debuffArmorModifier)){
                            armorAtrInstance.addTransientModifier(this.debuffArmorModifier);
                        }


                    }else if(raceCap.getRace().isWaterStrong()){
                        var damageAtrInstance = pAttributeMap.getInstance(Attributes.ATTACK_DAMAGE);
                        if(damageAtrInstance != null && !damageAtrInstance.hasModifier(this.buffDamageModifier)){
                            damageAtrInstance.addTransientModifier(this.buffDamageModifier);
                        }

                        var armorAtrInstance = pAttributeMap.getInstance(Attributes.ARMOR);
                        if(armorAtrInstance != null && !armorAtrInstance.hasModifier(this.buffArmorModifier)){
                            armorAtrInstance.addTransientModifier(this.buffArmorModifier);
                        }

                    }

                }

            });

        }
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        var damageAtrInstance = pAttributeMap.getInstance(Attributes.ATTACK_DAMAGE);
        if(damageAtrInstance != null){
            damageAtrInstance.removeModifier(this.debuffDamageModifier);
            damageAtrInstance.removeModifier(this.buffDamageModifier);
        }
        var armorAtrInstance = pAttributeMap.getInstance(Attributes.ARMOR);
        if(armorAtrInstance != null){
            armorAtrInstance.removeModifier(this.debuffArmorModifier);
            armorAtrInstance.removeModifier(this.buffDamageModifier);
        }
        if(pLivingEntity instanceof Player player){
            player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                if(raceCap.getRace() != null && raceCap.getRace().isWaterWeak()){
                    if(!player.isInWater()){
                        if(player.level() instanceof ServerLevel serverLevel){
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
                                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);

                        }
                    }
                }

            });

        }

        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(pLivingEntity.level() instanceof ServerLevel serverLevel){
            if(pLivingEntity.isInWater()) return;
            if(pLivingEntity.isOnFire()){
                pLivingEntity.removeEffect(this);
            }
            int amountParticles = 3;

            for(int i = 0; i < amountParticles; i++){
                double x = pLivingEntity.getX() + (pLivingEntity.getRandom().nextDouble() - 0.5) * pLivingEntity.getBbWidth();
                double y = pLivingEntity.getY() + pLivingEntity.getRandom().nextDouble() * pLivingEntity.getBbHeight();
                double z = pLivingEntity.getZ() + (pLivingEntity.getRandom().nextDouble() - 0.5) * pLivingEntity.getBbWidth();

                serverLevel.sendParticles(
                        ParticleTypes.FALLING_WATER,
                        x,
                        y,
                        z,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                );
            }
        }
    }

}
