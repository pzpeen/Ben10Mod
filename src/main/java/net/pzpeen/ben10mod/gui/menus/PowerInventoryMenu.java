package net.pzpeen.ben10mod.gui.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.pzpeen.ben10mod.gui.ModMenus;
import net.pzpeen.ben10mod.items.ModItems;
import org.jetbrains.annotations.NotNull;

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
                return stack.is(ModItems.OMNITRIX.get());
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
                if(originalItemStack.is(ModItems.OMNITRIX.get())){
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
