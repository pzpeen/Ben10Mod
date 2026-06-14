package net.pzpeen.ben10mod.systems;

import net.minecraft.resources.ResourceLocation;
import net.pzpeen.ben10mod.races.pyronite.PyroniteRace;

import java.util.ArrayList;
import java.util.List;

public class CodonStream {
    public static List<ResourceLocation> DNAsOnCodon = new ArrayList<>();
    public static DnaBank codonBank = new DnaBank(100, 10);

    public static void initializeCodonStreamBank(){
        codonBank.addDNA(PyroniteRace.id);
    }

}