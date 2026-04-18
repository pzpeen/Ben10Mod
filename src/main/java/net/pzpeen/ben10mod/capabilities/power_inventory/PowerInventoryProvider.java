package net.pzpeen.ben10mod.capabilities.power_inventory;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PowerInventoryProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PowerInventory> PLAYER_POWER_INVENTORY =
            CapabilityManager.get(new CapabilityToken<>() {});

    private PowerInventory powerInventoryHolder = null;
    private final LazyOptional<PowerInventory> opt = LazyOptional.of(this::createOrGetPowerInventory);


    private PowerInventory createOrGetPowerInventory(){
        if(powerInventoryHolder == null){
            powerInventoryHolder = new PowerInventory();
        }
        return powerInventoryHolder;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_POWER_INVENTORY){
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createOrGetPowerInventory().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createOrGetPowerInventory().loadNBT(nbt);
    }
}
