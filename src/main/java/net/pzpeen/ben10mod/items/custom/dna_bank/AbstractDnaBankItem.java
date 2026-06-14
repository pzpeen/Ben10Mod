package net.pzpeen.ben10mod.items.custom.dna_bank;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.pzpeen.ben10mod.systems.DnaBank;
import net.pzpeen.ben10mod.systems.Playlist;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class AbstractDnaBankItem extends Item {
    private static final String dnaBankTag = "dnaBank";


    public AbstractDnaBankItem(Properties pProperties) {
        super(pProperties);
    }
    public abstract int getMaxPlaylists();

    public abstract int getMaxDNACapability();

    @NotNull
    public static DnaBank getDnaBank(ItemStack stack){
        if((stack.getItem() instanceof AbstractDnaBankItem dnaBankItem)){
            CompoundTag nbt = stack.getOrCreateTag();
            if(nbt.contains(dnaBankTag)){
                CompoundTag dnaBankNbt = nbt.getCompound(dnaBankTag);
                return DnaBank.getFromNBT(dnaBankNbt, dnaBankItem.getMaxDNACapability(), dnaBankItem.getMaxPlaylists());
            }

            DnaBank dnaBank = new DnaBank(dnaBankItem.getMaxDNACapability(), dnaBankItem.getMaxPlaylists());

            saveDnaBank(stack, dnaBank);

            return dnaBank;
        }else{
            System.err.println("TRYING GET DNABANK FROM A NON DNABANK ITEM");
            return null;
        }
    }

    public static void saveDnaBank(ItemStack stack, DnaBank dnaBank){
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.put(dnaBankTag, dnaBank.saveToNBT());
    }


}
