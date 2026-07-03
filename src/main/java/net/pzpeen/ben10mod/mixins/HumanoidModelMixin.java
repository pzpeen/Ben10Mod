package net.pzpeen.ben10mod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.items.ModItems;
import net.pzpeen.ben10mod.powers.OmnitrixPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void onSetupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks,
                             float pNetHeadYaw, float pHeadPitch, CallbackInfo ci){
        if(pEntity instanceof Player player){
            HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;

            if(Minecraft.getInstance().player == player && Minecraft.getInstance().options.getCameraType().isFirstPerson()){
                return;
            }

            player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {
                if(powerCap.isHudActive()){
                    if(powerCap.getPower() instanceof OmnitrixPower power){
                        int slapTicks = power.getSlapAnimationTick();
                        //System.out.println("TICK IN 3 PERSON: "+ slapTicks);

                        model.leftArm.y = player.isCrouching() ? 6 : 3;

                        model.leftArm.xRot = -105*Mth.DEG_TO_RAD;
                        model.leftArm.yRot = 30*Mth.DEG_TO_RAD;
                        model.leftArm.zRot = -90*Mth.DEG_TO_RAD;

                        if(slapTicks > 0){
                            if(slapTicks >= 4){
                                float progress = (8 - slapTicks) / 4.0f;
                                model.rightArm.xRot = (-80f - (progress * 60f))*Mth.DEG_TO_RAD;
                                model.rightArm.yRot = (-50f - (progress * 13f))*Mth.DEG_TO_RAD;

                            }else{
                                float progress = (3 - slapTicks) / 3.0f;
                                model.rightArm.xRot = (-140f + (progress * 60))*Mth.DEG_TO_RAD;
                                model.rightArm.yRot = -63f*Mth.DEG_TO_RAD;

                            }

                        }else{
                            //Normal pose
                            model.rightArm.xRot = -80*Mth.DEG_TO_RAD;
                            model.rightArm.yRot = -50*Mth.DEG_TO_RAD;

                        }



                    }


                }

            });

        }

    }
}
