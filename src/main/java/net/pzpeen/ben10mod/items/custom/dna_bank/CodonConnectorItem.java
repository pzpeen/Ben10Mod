package net.pzpeen.ben10mod.items.custom.dna_bank;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.pzpeen.ben10mod.races.pyronite.PyroniteRace;
import net.pzpeen.ben10mod.systems.CodonStream;
import net.pzpeen.ben10mod.systems.DnaBank;
import org.jetbrains.annotations.NotNull;

public class CodonConnectorItem extends AbstractDnaBankItem{
    public CodonConnectorItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);

        DnaBank dnaBank = CodonStream.codonBank;
        dnaBank.unlockDNA(PyroniteRace.id);

        dnaBank.getPlaylist(0).add(PyroniteRace.id);

        AbstractDnaBankItem.saveDnaBank(stack, dnaBank);
        return stack;
    }

    @Override
    public int getMaxPlaylists() {
        return 10;
    }

    @Override
    public int getMaxDNACapability() {
        return 100;
    }
}
