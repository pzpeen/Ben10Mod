package net.pzpeen.ben10mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.pzpeen.ben10mod.capabilities.IBen10ModCapCache;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCapProvider;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCapProvider;
import net.pzpeen.ben10mod.client.gui.hud.OmnitrixHud;
import net.pzpeen.ben10mod.items.ModItems;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.OpenPowerMenuC2SPacket;
import net.pzpeen.ben10mod.networking.packets.PowerCapC2SPacket;
import net.pzpeen.ben10mod.networking.packets.UseSkillC2SPacket;
import net.pzpeen.ben10mod.sounds.ModSounds;
import net.pzpeen.ben10mod.utils.ModTags;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static final String MOD_KEYBIND_CATEGORY = "key.categories.ben10mod.ben10";

    public static final KeyMapping POWER_MENU =
            new KeyMapping("key.keybinds.ben10mod.power_menu", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_I, MOD_KEYBIND_CATEGORY);

    public static final KeyMapping ACTIVATE_POWER =
            new KeyMapping("key.keybinds.ben10mod.activate_power", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R, MOD_KEYBIND_CATEGORY);

    public static final KeyMapping SKILL_1 =
            new KeyMapping("key.keybinds.ben10mod.skill_1", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_Z, MOD_KEYBIND_CATEGORY);


    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(POWER_MENU);
        event.register(ACTIVATE_POWER);
        event.register(SKILL_1);
    }

    private static final long cooldownToActivatePower = 1000;
    private static long lastClickActivatePower = 0;

    public static void registerFunctions(InputEvent.Key event){
        //Power Menu
        if(KeyBinds.POWER_MENU.consumeClick()){
            ModNetworking.sendToServer(new OpenPowerMenuC2SPacket());
        }

        //Activate Power
        if(KeyBinds.ACTIVATE_POWER.consumeClick()){
            AbstractClientPlayer player = Minecraft.getInstance().player;
            assert player != null;
            //player.sendSystemMessage(Component.literal("Activate power"));
            if(event.getAction() == GLFW.GLFW_PRESS && System.currentTimeMillis() - lastClickActivatePower >= cooldownToActivatePower){
                lastClickActivatePower = System.currentTimeMillis();
                PowerCap powerCap = ((IBen10ModCapCache)player).ben10Mod$getCachedPowerCap();
                if(powerCap != null){
                    if (powerCap.getInventory().getStackInSlot(0).is(ModTags.Items.POWER_ITEMS)){
                        if(powerCap.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){

                            if (!powerCap.isHudActive()){
                                RaceCap raceCap = ((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap();
                                if(raceCap != null){
                                    if(raceCap.getRace() == null){
                                        ModNetworking.sendToServer(new PowerCapC2SPacket(!powerCap.isHudActive(), powerCap.getHudSlot(), ModSounds.OMNITRIX_ACTIVATE.get()));
                                        Minecraft.getInstance().getSoundManager().play(OmnitrixHud.omnitrixActiveSoundInstance);
                                    }
                                }
                                /*
                                player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                                    if(raceCap.getRace() == null){
                                        ModNetworking.sendToServer(new PowerCapC2SPacket(!powerCap.isHudActive(), powerCap.getHudSlot(), ModSounds.OMNITRIX_ACTIVATE.get()));
                                        Minecraft.getInstance().getSoundManager().play(OmnitrixHud.omnitrixActiveSoundInstance);
                                    }
                                });

                                 */

                            }else {
                                Minecraft.getInstance().getSoundManager().stop(OmnitrixHud.omnitrixActiveSoundInstance);
                                ModNetworking.sendToServer(new PowerCapC2SPacket(!powerCap.isHudActive(), powerCap.getHudSlot(), ModSounds.OMNITRIX_DEACTIVATE.get()));
                            }

                        }



                    }

                }
                /*
                player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent(pwrCap -> {
                        if (pwrCap.getInventory().getStackInSlot(0).is(ModTags.Items.POWER_ITEMS)){
                            if(pwrCap.getInventory().getStackInSlot(0).is(ModItems.OMNITRIX.get())){

                                if (!pwrCap.isHudActive()){
                                    player.getCapability(RaceCapProvider.PLAYER_RACE_CAP).ifPresent(raceCap -> {
                                        if(raceCap.getRace() == null){
                                            ModNetworking.sendToServer(new PowerCapC2SPacket(!pwrCap.isHudActive(), pwrCap.getHudSlot(), ModSounds.OMNITRIX_ACTIVATE.get()));
                                            Minecraft.getInstance().getSoundManager().play(OmnitrixHud.omnitrixActiveSoundInstance);
                                        }
                                    });

                                }else {
                                    Minecraft.getInstance().getSoundManager().stop(OmnitrixHud.omnitrixActiveSoundInstance);
                                    ModNetworking.sendToServer(new PowerCapC2SPacket(!pwrCap.isHudActive(), pwrCap.getHudSlot(), ModSounds.OMNITRIX_DEACTIVATE.get()));
                                }

                            }



                        }
                });

                 */
            }


        }

        //Using Skill1
        if(KeyBinds.SKILL_1.consumeClick()){
            AbstractClientPlayer player = Minecraft.getInstance().player;
            assert player != null;

            if(((IBen10ModCapCache)player).ben10Mod$getCachedRaceCap().getRace() != null){
                ModNetworking.sendToServer(new UseSkillC2SPacket(1));
            }

        }

    }

}
