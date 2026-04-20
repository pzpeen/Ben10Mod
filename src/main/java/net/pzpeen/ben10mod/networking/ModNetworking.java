package net.pzpeen.ben10mod.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.networking.packets.OpenPowerMenuC2SPacket;
import net.pzpeen.ben10mod.networking.packets.PowerCapC2SPacket;
import net.pzpeen.ben10mod.networking.packets.PowerCapS2CPacket;

public class ModNetworking {
    private static SimpleChannel INSTANCE;

    private static int id = 0;
    private static int id(){
        return id++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        INSTANCE.messageBuilder(PowerCapS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PowerCapS2CPacket::decode)
                .encoder(PowerCapS2CPacket::encode)
                .consumerMainThread(PowerCapS2CPacket::handle)
                .add();


        INSTANCE.messageBuilder(OpenPowerMenuC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenPowerMenuC2SPacket::toBytes)
                .decoder(OpenPowerMenuC2SPacket::new)
                .consumerMainThread(OpenPowerMenuC2SPacket::handle)
                .add();

        INSTANCE.messageBuilder(PowerCapC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(PowerCapC2SPacket::encode)
                .decoder(PowerCapC2SPacket::decode)
                .consumerMainThread(PowerCapC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG msg){
        INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sendToPlayer(MSG msg, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static <MSG> void sendToClientTrackingAndSelf(MSG msg, ServerPlayer player){
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), msg);
    }
}
