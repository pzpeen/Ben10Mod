package net.pzpeen.ben10mod.powers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;

public abstract class BasePower {
    public abstract ResourceLocation getPowerID();

    public abstract void tick(Player player);

    public abstract void save(CompoundTag nbt);

    public abstract void load(CompoundTag nbt);

    public void onHudRightClick(){}

}
