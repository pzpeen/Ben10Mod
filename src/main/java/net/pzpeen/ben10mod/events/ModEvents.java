package net.pzpeen.ben10mod.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerCapS2CPacket;
import net.pzpeen.ben10mod.networking.packets.RaceCapS2CPacket;
import net.pzpeen.ben10mod.races.AbstractRace;


public class ModEvents {

    @Mod.EventBusSubscriber(modid = Ben10Mod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus{

        //Cap events

        @SubscribeEvent
        public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event){
            if(event.getObject() instanceof Player player){
                //Attaching power cap
                if(!event.getObject().getCapability(PowerCapProvider.PLAYER_POWER_CAP).isPresent()){
                    event.addCapability(ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "ben10mod_power_cap"), new PowerCapProvider());
                }

                //Attaching race cap
                if(!event.getObject().getCapability(RaceCapProvider.PLAYER_RACE_CAP).isPresent()){
                    event.addCapability(ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "ben10mod_race_cap"), new RaceCapProvider(player));
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event){
            event.getOriginal().reviveCaps();

            //Keeping PowerCap on server
            event.getOriginal().getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(oldCap ->
                    event.getEntity().getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(newCap -> {
                        newCap.getInventory().deserializeNBT(oldCap.getInventory().serializeNBT());
                        newCap.setPower(oldCap.getPowerID());

                    }
            ));

            //Keeping RaceCap on server
            event.getOriginal().getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(oldCap ->
                    event.getEntity().getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(newCap ->
                            newCap.setRace(oldCap.getRaceId(), event.getEntity())));
        }

        @SubscribeEvent
        public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event){
            if(!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player){

                PowerCap powerCap = ((IBen10ModCapCache)player).ben10Mod$getCachedPowerCap();
                if(powerCap != null){
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(powerCap.getInventory().serializeNBT(),
                            event.getEntity().getUUID(), powerCap.isHudActive(), powerCap.getHudSlot(), powerCap.getPowerID()), player);
                }
                RaceCap raceCap = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap();
                if(raceCap != null){
                    ModNetworking.sendToClientTrackingAndSelf(new RaceCapS2CPacket(raceCap.getRaceId(), player.getUUID()), player);
                }

                /*
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(powerCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(powerCap.getInventory().serializeNBT(),
                            event.getEntity().getUUID(), powerCap.isHudActive(), powerCap.getHudSlot(), powerCap.getPowerID()), player);
                });

                player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(new RaceCapS2CPacket(raceCap.getRaceId(), player.getUUID()), player);
                });

                 */

            }

        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
            if(!event.getEntity().level().isClientSide()){
                //Keeping PowerCap
                PowerCap powerCap = ((IBen10ModCapCache)event.getEntity()).ben10Mod$getCachedPowerCap();
                if(powerCap != null){
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(powerCap.getInventory().serializeNBT(), event.getEntity().getUUID(),
                            powerCap.isHudActive(), powerCap.getHudSlot(), powerCap.getPowerID()), (ServerPlayer) event.getEntity());

                }
                //Keeping RaceCap
                RaceCap raceCap = ((IBen10ModCapCache)event.getEntity()).ben10Mod$getCachedRaceCap();
                if(raceCap != null){
                    ModNetworking.sendToClientTrackingAndSelf(new RaceCapS2CPacket(raceCap.getRaceId(), event.getEntity().getUUID()),
                            (ServerPlayer) event.getEntity());
                }

                /*
                event.getEntity().getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), event.getEntity().getUUID(),
                            pwrCap.isHudActive(), pwrCap.getHudSlot(), pwrCap.getPowerID()), (ServerPlayer) event.getEntity());
                });

                event.getEntity().getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(new RaceCapS2CPacket(raceCap.getRaceId(), event.getEntity().getUUID()),
                            (ServerPlayer) event.getEntity());
                });

                 */
            }
        }

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
            if(!event.getEntity().level().isClientSide()){
                //Keeping PowerCap
                PowerCap powerCap = ((IBen10ModCapCache)event.getEntity()).ben10Mod$getCachedPowerCap();
                if(powerCap != null){
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(powerCap.getInventory().serializeNBT(), event.getEntity().getUUID(),
                            powerCap.isHudActive(), powerCap.getHudSlot(), powerCap.getPowerID()), (ServerPlayer) event.getEntity());
                }
                //Keeping RaceCap
                RaceCap raceCap = ((IBen10ModCapCache)event.getEntity()).ben10Mod$getCachedRaceCap();
                if(raceCap != null){
                    ModNetworking.sendToClientTrackingAndSelf(new RaceCapS2CPacket(raceCap.getRaceId(), event.getEntity().getUUID()),
                            (ServerPlayer) event.getEntity());
                }

                /*
                event.getEntity().getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), event.getEntity().getUUID(),
                            pwrCap.isHudActive(), pwrCap.getHudSlot(), pwrCap.getPowerID()), (ServerPlayer) event.getEntity());
                });

                event.getEntity().getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(new RaceCapS2CPacket(raceCap.getRaceId(), event.getEntity().getUUID()),
                            (ServerPlayer) event.getEntity());
                });
                 */
            }
        }

        @SubscribeEvent
        public static void onPlayerTracking(PlayerEvent.StartTracking event){
            if(event.getTarget() instanceof  Player targetPlayer){
                //Tracking PowerCap
                PowerCap powerCap = ((IBen10ModCapCache)targetPlayer).ben10Mod$getCachedPowerCap();
                if(powerCap != null){
                    ModNetworking.sendToClientTrackingAndSelf(
                            new PowerCapS2CPacket(powerCap.getInventory().serializeNBT(), targetPlayer.getUUID(),
                                    powerCap.isHudActive(), powerCap.getHudSlot(), powerCap.getPowerID()),
                            (ServerPlayer) event.getEntity());

                }
                //Tracking RaceCap
                RaceCap raceCap = ((IBen10ModCapCache)targetPlayer).ben10Mod$getCachedRaceCap();
                if(raceCap != null){
                    ModNetworking.sendToClientTrackingAndSelf(
                            new RaceCapS2CPacket(raceCap.getRaceId(), targetPlayer.getUUID()),
                            (ServerPlayer) event.getEntity());

                }

                /*
                targetPlayer.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((pwrCap) -> {
                    ModNetworking.sendToClientTrackingAndSelf(
                            new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), targetPlayer.getUUID(),
                                    pwrCap.isHudActive(), pwrCap.getHudSlot(), pwrCap.getPowerID()),
                            (ServerPlayer) event.getEntity());
                });

                targetPlayer.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(
                            new RaceCapS2CPacket(raceCap.getRaceId(), targetPlayer.getUUID()),
                            (ServerPlayer) event.getEntity()
                    );
                });

                 */

            }


        }

        //Armor Events

        @SubscribeEvent
        public static void onEquipmentChange(LivingEquipmentChangeEvent event){
            if(event.getEntity() instanceof Player player){
                if(event.getSlot().isArmor()){
                    /*
                    player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                        if(raceCap.getRace() != null){
                            ItemStack armor = event.getTo();

                            if(!armor.isEmpty()){
                                player.setItemSlot(event.getSlot(), ItemStack.EMPTY);

                                if(!player.getInventory().add(armor)){
                                    player.drop(armor, false);
                                }
                            }
                        }
                    });

                     */
                    if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                        ItemStack armor = event.getTo();

                        if(!armor.isEmpty()){
                            player.setItemSlot(event.getSlot(), ItemStack.EMPTY);

                            if(!player.getInventory().add(armor)){
                                player.drop(armor, false);
                            }
                        }

                    }
                //Blocking Shield if transformed
                }else if(event.getSlot() == EquipmentSlot.OFFHAND){
                    /*
                    player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                        if(raceCap.getRace() != null){
                            ItemStack toOffHandItem = event.getTo();
                            if(toOffHandItem.getItem() instanceof ShieldItem){
                                player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

                                if(!player.getInventory().add(toOffHandItem)){
                                    player.drop(toOffHandItem, false);
                                }
                            }
                        }
                    });

                     */
                    if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                        ItemStack toOffHandItem = event.getTo();
                        if(toOffHandItem.getItem() instanceof ShieldItem){
                            player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

                            if(!player.getInventory().add(toOffHandItem)){
                                player.drop(toOffHandItem, false);
                            }
                        }
                    }

                }
            }
        }


        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event){
            if(event.getEntity() instanceof Player player && !player.level().isClientSide()){
                AbstractRace race = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace();
                if(race != null){
                    float newDistance = Math.max(0f, event.getDistance() - race.getFallDamageResistance());
                    event.setDistance(newDistance);
                    event.setDamageMultiplier(event.getDamageMultiplier() * race.getFallDamageMultiplier());
                }

            }
        }


        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event){
            if(!(event.getEntity() instanceof Player player)) return;

            if(event.getSource().is(DamageTypeTags.IS_FIRE)){
                /*
                player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                    if (raceCap.getRace() != null){
                        if(raceCap.getRace().isFireResistent()){
                            event.cancel();
                        }
                    }
                });

                 */
                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                    if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace().isFireResistent()){
                        event.cancel();
                    }
                }

            }

        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event){
            if (!(event.getSource().getEntity() instanceof Player player)) return;

            /*
            player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                if(raceCap.getRace() != null && player.getMainHandItem().isEmpty()){
                    raceCap.getRace().doBareHandHit(event);
                }
            });

             */

            if(!event.getSource().is(DamageTypeTags.IS_PROJECTILE)){
                if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null && player.getMainHandItem().isEmpty()){
                    ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace().doBareHandHit(event);
                }
            }
        }


        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event){
            if(event.phase == TickEvent.Phase.END){
                //Ticking power
                if(((IBen10ModCapCache)event.player).ben10Mod$getCachedPowerCap().getPower() != null){
                    ((IBen10ModCapCache)event.player).ben10Mod$getCachedPowerCap().getPower().tick(event.player);
                }

                //Ticking holding skills and ticking skills
                if(((IBen10ModCapCache)event.player).ben10Mod$getCachedRaceCap().getRace() != null){
                    AbstractRace race = ((IBen10ModCapCache)event.player).ben10Mod$getCachedRaceCap().getRace();
                    if(race.getSkill1() != null){
                        if(race.getSkill1().isHolding()){
                            race.holdSkill1();
                        }
                        if(race.getSkill1().isTicking()){
                            race.tickSkill1();
                        }

                    }
                    if(race.getSkill2() != null){
                        if(race.getSkill2().isHolding()){
                            race.holdSkill2();
                        }
                        if(race.getSkill2().isTicking()){
                            race.tickSkill2();
                        }
                    }
                    if(race.getSkill3() != null){
                        if(race.getSkill3().isHolding()){
                            race.holdSkill3();
                        }
                        if(race.getSkill3().isTicking()){
                            race.tickSkill3();
                        }
                    }
                    if(race.getSkill4() != null){
                        if(race.getSkill4().isHolding()){
                            race.holdSkill4();
                        }
                        if(race.getSkill4().isTicking()){
                            race.tickSkill4();
                        }
                    }
                    if(race.getSkill5() != null){
                        if(race.getSkill5().isHolding()){
                            race.holdSkill5();
                        }
                        if(race.getSkill5().isTicking()){
                            race.tickSkill5();
                        }
                    }

                }


            }
        }

        @SubscribeEvent
        public static void onExplosion(ExplosionEvent.Detonate event){
            if(event.getExplosion().getExploder() instanceof Player player){
                if(player.getPersistentData().getBoolean("noDmgExplosion")){
                    event.getAffectedEntities().clear();
                }

            }

        }



    }



}
