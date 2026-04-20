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

public class PowerCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PowerCap> PLAYER_POWER_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});

    private PowerCap powerCapHolder = null;
    private final LazyOptional<PowerCap> opt = LazyOptional.of(this::createOrGetPowerCap);


    private PowerCap createOrGetPowerCap(){
        if(powerCapHolder == null){
            powerCapHolder = new PowerCap();
        }
        return powerCapHolder;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_POWER_CAP){
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createOrGetPowerCap().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createOrGetPowerCap().loadNBT(nbt);
    }
}
