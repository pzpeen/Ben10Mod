package net.pzpeen.ben10mod.client.gui.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.pzpeen.ben10mod.capabilities.power_inventory.PowerCapProvider;
import net.pzpeen.ben10mod.client.gui.ModMenus;
import net.pzpeen.ben10mod.client.gui.hud.OmnitrixHud;
import net.pzpeen.ben10mod.networking.ModNetworking;
import net.pzpeen.ben10mod.networking.packets.PowerCapS2CPacket;
import net.pzpeen.ben10mod.utils.ModTags;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;

public class PowerInventoryMenu extends AbstractContainerMenu {
    private final Player player;
    private final ItemStackHandler pwrSlot;

    public PowerInventoryMenu(int pContainerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(pContainerId, playerInv, new ItemStackHandler(1));
    }

    public PowerInventoryMenu(int pContainerId, Inventory playerInv, ItemStackHandler pPwrSlot){
        super(ModMenus.POWER_INVENTORY_MENU.get(), pContainerId);
        this.player = playerInv.player;
        this.pwrSlot = pPwrSlot;

        this.addSlot(new SlotItemHandler(this.pwrSlot, 0, 80, 18){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(ModTags.Items.POWER_ITEMS);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                if(!player.level().isClientSide()){
                    //Changing menu state and hud arms animation progress
                    player.getCapability(PowerCapProvider.PLAYER_POWER_CAP).ifPresent((pwrCap) -> {
                        pwrCap.setHudActive(false);
                        OmnitrixHud.menuAnimProgress = 0.0f;
                        OmnitrixHud.lastMenuAnimProgress = 0.0f;
                        ModNetworking.sendToClientTrackingAndSelf(
                                new PowerCapS2CPacket(pwrCap.getInventory().serializeNBT(), player.getUUID(),
                                        pwrCap.isHudActive(), pwrCap.getHudSlot()), (ServerPlayer) player);


                        if(!pwrCap.getInventory().getStackInSlot(0).isEmpty()){
                            System.out.println("Colocando uuid na item stack");
                            pwrCap.getInventory().getStackInSlot(0).getOrCreateTag().putUUID("playerUsingUUID", player.getUUID());
                            GeoItem.getOrAssignId(pwrCap.getInventory().getStackInSlot(0), (ServerLevel) player.level());
                        }
                    });

                }

            }
        });
        addPlayerInventory(this.player.getInventory());
        addPlayerHotbar(this.player.getInventory());

    }

    private void addPlayerInventory(Inventory pPlayerInv){
        for (int i = 0; i < 3; i++){
            for(int l = 0; l < 9; l++){
                this.addSlot(new Slot(pPlayerInv, l + i * 9 + 9, 8 + l*18, 140 + i*18));
            }
        }
    }

    private void addPlayerHotbar(Inventory pPlayerInv){
        for (int i = 0; i < 9; i++){
            this.addSlot(new Slot(pPlayerInv, i, 8 + i*18, 198));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack returnItemStack = ItemStack.EMPTY;
        Slot slot = this.getSlot(pIndex);

        if(slot.hasItem()){
            ItemStack originalItemStack = slot.getItem();
            returnItemStack = originalItemStack.copy();

            if(pIndex < 1){
                if(!this.moveItemStackTo(originalItemStack, 1, 37, true)){
                    return ItemStack.EMPTY;
                }
            }else {
                if(originalItemStack.is(ModTags.Items.POWER_ITEMS)){
                    if(!this.moveItemStackTo(originalItemStack, 0, 1, false)){
                        return ItemStack.EMPTY;
                    }
                }else {
                    return ItemStack.EMPTY;
                }
            }

            if(originalItemStack.getCount() == returnItemStack.getCount()){
                return ItemStack.EMPTY;
            }

            if(originalItemStack.isEmpty()){
                slot.set(ItemStack.EMPTY);
            }else{
                slot.setChanged();
            }

            slot.onTake(pPlayer, originalItemStack);

        }


        return returnItemStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

}
