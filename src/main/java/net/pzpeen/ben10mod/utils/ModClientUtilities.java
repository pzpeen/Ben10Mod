package net.pzpeen.ben10mod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModClientUtilities {

    @OnlyIn(Dist.CLIENT)
    public static boolean isLocalPlayerFirstPerson(Entity e){
        Player player = Minecraft.getInstance().player;
        boolean isLocalPlayer = (player == e);
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

        return isLocalPlayer && isFirstPerson;

    }


}
