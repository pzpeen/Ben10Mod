package net.pzpeen.ben10mod.capabilities.race_capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaceCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<RaceCap> PLAYER_RACE_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    private RaceCap raceCapHolder = null;
    private final LazyOptional<RaceCap> opt = LazyOptional.of(this::createOrGetRaceCap);

    private RaceCap createOrGetRaceCap(){
        if(raceCapHolder == null){
            raceCapHolder = new RaceCap();
        }
        return raceCapHolder;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_RACE_CAP){
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createOrGetRaceCap().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createOrGetRaceCap().loadNBT(nbt);
    }
}
