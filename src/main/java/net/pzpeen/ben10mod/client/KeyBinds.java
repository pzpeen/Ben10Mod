package net.pzpeen.ben10mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static final String MOD_KEYBIND_CATEGORY = "key.categories.ben10mod.ben10";

    public static final KeyMapping POWER_MENU =
            new KeyMapping("key.keybinds.ben10mod.power_menu", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_I, MOD_KEYBIND_CATEGORY);

}
