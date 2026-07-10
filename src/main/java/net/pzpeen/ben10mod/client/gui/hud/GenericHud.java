package net.pzpeen.ben10mod.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;
import net.pzpeen.ben10mod.skills.AbstractSkill;

import java.awt.*;

public class GenericHud {

    public static void renderSkillCooldown(GuiGraphics pGuiGraphics, int pSlotPos, AbstractSkill pSkill){
        int screenWidth = pGuiGraphics.guiWidth();
        int screenHeight = pGuiGraphics.guiHeight();

        int slotSize = 22;

        int startX = 0;
        int startY = screenHeight/2 - 55;

        int actualY = startY + (pSlotPos*22);

        pGuiGraphics.blit(OmnitrixHud.OMNITRIX_HUD_TEXTURE, startX, actualY, 0, 46, slotSize, slotSize);
        pGuiGraphics.blit(pSkill.getIcon(), startX+3, actualY + 3, 0, 0, 16, 16, 16, 16);

        float progress =  (float)pSkill.getCooldown().getRemainingTicks() / pSkill.getMaxCooldown();
        int squareSize = (int) Math.ceil(progress * 16);
        int squareYPos = actualY + 3 + (16 - squareSize);
        int color = 0x7FFFFFFF;

        pGuiGraphics.fill(startX+3, squareYPos, startX+19, actualY + slotSize - 3, color);

        int cooldown = pSkill.getCooldown().getRemainingTicks() / 20 + 1;

        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, Integer.toString(cooldown), slotSize-1, actualY + slotSize - 5, Color.white.getRGB());





    }


}
