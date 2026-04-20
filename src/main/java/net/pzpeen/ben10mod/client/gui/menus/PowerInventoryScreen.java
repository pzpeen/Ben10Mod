package net.pzpeen.ben10mod.client.gui.menus;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.pzpeen.ben10mod.Ben10Mod;

import java.util.Objects;

public class PowerInventoryScreen extends AbstractContainerScreen<PowerInventoryMenu> {
    private static final ResourceLocation MENU_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "textures/gui/container/power_inventory.png");

    public PowerInventoryScreen(PowerInventoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.imageHeight = 221;
        this.imageWidth = 176;

        this.inventoryLabelY = 1000;
        this.titleLabelY = 1000;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MENU_TEXTURE);

        int x = (width - imageWidth)/2;
        int y = (height - imageHeight)/2;

        pGuiGraphics.blit(MENU_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderPlayerModel(x+32, y+75, 30, pMouseX, pMouseY, pGuiGraphics);

    }

    private void renderPlayerModel(int pX, int pY, int pScale, int pMouseX, int pMouseY, GuiGraphics pGuiGraphics){
        float mouseXAngle = (float) pX - pMouseX;
        float mouseYAngle = (float) pY - 50 - pMouseY;

        assert Objects.requireNonNull(this.minecraft).player != null;
        InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics, pX, pY, pScale, mouseXAngle, mouseYAngle, this.minecraft.player);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
