package net.pzpeen.ben10mod.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerInventoryProvider;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerInventoryS2CPacket;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = Ben10Mod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus{

        @SubscribeEvent
        public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event){
            if(event.getObject() instanceof Player){
                if(!event.getObject().getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY).isPresent()){
                    event.addCapability(ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "power_inventory"), new PowerInventoryProvider());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event){
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY).ifPresent(oldCap ->
                    event.getEntity().getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY).ifPresent(newCap -> {
                        newCap.getInventory().deserializeNBT(oldCap.getInventory().serializeNBT());
            }));
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
            if(!event.getEntity().level().isClientSide()){
                event.getEntity().getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY).ifPresent(pwrInv -> {
                    ModNetworking.sendToPlayer(new PowerInventoryS2CPacket(pwrInv.getInventory().serializeNBT()), (ServerPlayer) event.getEntity());
                });
            }
        }

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
            if(!event.getEntity().level().isClientSide()){
                event.getEntity().getCapability(PowerInventoryProvider.PLAYER_POWER_INVENTORY).ifPresent(pwrInv -> {
                    ModNetworking.sendToPlayer(new PowerInventoryS2CPacket(pwrInv.getInventory().serializeNBT()), (ServerPlayer) event.getEntity());
                });
            }
        }
    }

    public static class ModBus{

    }


}
