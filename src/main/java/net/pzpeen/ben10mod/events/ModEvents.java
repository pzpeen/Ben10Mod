package net.pzpeen.ben10mod.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerCapS2CPacket;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = Ben10Mod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus{

        @SubscribeEvent
        public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event){
            if(event.getObject() instanceof Player){
                if(!event.getObject().getCapability(PowerCapProvider.PLAYER_POWER_CAP).isPresent()){
                    event.addCapability(ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "ben10mod_power_cap"), new PowerCapProvider());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event){
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(oldCap ->
                    event.getEntity().getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(newCap -> {
                        newCap.getInventory().deserializeNBT(oldCap.getInventory().serializeNBT());
            }));
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
            if(!event.getEntity().level().isClientSide()){
                event.getEntity().getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), event.getEntity().getUUID(),
                            pwrCap.isHudActive(), pwrCap.getHudSlot()), (ServerPlayer) event.getEntity());
                });
            }
        }

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
            if(!event.getEntity().level().isClientSide()){
                event.getEntity().getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                    ModNetworking.sendToClientTrackingAndSelf(new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), event.getEntity().getUUID(),
                            pwrCap.isHudActive(), pwrCap.getHudSlot()), (ServerPlayer) event.getEntity());
                });
            }
        }

        @SubscribeEvent
        public static void onPlayerTracking(PlayerEvent.StartTracking event){
            if(event.getTarget() instanceof  Player targetPlayer){
                targetPlayer.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((pwrCap) -> {
                    ModNetworking.sendToClientTrackingAndSelf(
                            new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), targetPlayer.getUUID(),
                                    pwrCap.isHudActive(), pwrCap.getHudSlot()),
                            (ServerPlayer) event.getEntity());
                });
            }


        }
    }
}
