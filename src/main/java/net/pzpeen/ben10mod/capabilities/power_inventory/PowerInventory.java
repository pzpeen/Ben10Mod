package net.pzpeen.ben10mod.capabilities.power_inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.items.ItemStackHandler;

@AutoRegisterCapability
public class PowerInventory {
    private final ItemStackHandler pwr_inventory = new ItemStackHandler(1);

    public ItemStackHandler getInventory(){
        return this.pwr_inventory;
    }

    public void saveNBT(CompoundTag nbt){
        nbt.put("pwr_inventory", this.pwr_inventory.serializeNBT());
    }

    public void loadNBT(CompoundTag nbt){
        this.pwr_inventory.deserializeNBT(nbt.getCompound("pwr_inventory"));
    }

}
