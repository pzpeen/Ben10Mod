package net.pzpeen.ben10mod.capabilities.power_capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.items.ItemStackHandler;
import net.pzpeen.ben10mod.powers.BasePower;
import net.pzpeen.ben10mod.powers.PowerRegistries;

@AutoRegisterCapability
public class PowerCap {
    private final ItemStackHandler pwr_inventory = new ItemStackHandler(1);
    private boolean hudActive = false;
    private int hudSlot = 1;
    private BasePower power = null;

    public ItemStackHandler getInventory(){
        return this.pwr_inventory;
    }

    public boolean isHudActive(){
        return this.hudActive;
    }
    public void setHudActive(boolean b){
        this.hudActive = b;
    }

    public int getHudSlot(){
        return this.hudSlot;
    }
    public void setHudSlot(int i){
        this.hudSlot = i;
    }
    public void walkOnHudSlot(int i){
        if (this.hudSlot + i < 0){
            this.hudSlot = this.hudSlot + i + 10;
        } else if (this.hudSlot + i > 9) {
            this.hudSlot = this.hudSlot + i - 10;
        }else {
            this.hudSlot = this.hudSlot + i;
        }
    }

    public ResourceLocation getPowerID(){
        if(power != null){
            return power.getPowerID();
        }else{
            return null;
        }
    }

    public BasePower getPower(){
        return this.power;
    }
    public void setPower(ResourceLocation powerId){
        this.power = PowerRegistries.pickPower(powerId);
    }

    public void saveNBT(CompoundTag nbt){
        nbt.put("pwr_inventory", this.pwr_inventory.serializeNBT());
        nbt.putBoolean("hud_active", this.hudActive);
        nbt.putInt("hud_slot", this.hudSlot);
        if(this.power != null){
            nbt.putString("power_id", this.power.getPowerID().toString());

            CompoundTag powerNbt = new CompoundTag();
            this.power.save(powerNbt);
            nbt.put("power_data", powerNbt);

        }else{
            nbt.putString("power_id", "null");
        }
    }

    public void loadNBT(CompoundTag nbt){
        this.pwr_inventory.deserializeNBT(nbt.getCompound("pwr_inventory"));
        this.hudActive = nbt.getBoolean("hud_active");
        this.hudSlot = nbt.getInt("hud_slot");
        if(!nbt.getString("power_id").equals("null")){
            this.power = PowerRegistries.pickPower(ResourceLocation.parse(nbt.getString("power_id")));
            this.power.load(nbt.getCompound("power_data"));
        }

    }

}
