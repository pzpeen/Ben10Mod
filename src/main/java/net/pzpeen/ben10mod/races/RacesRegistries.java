package net.pzpeen.ben10mod.races;

import net.minecraft.resources.ResourceLocation;
import net.pzpeen.ben10mod.Ben10Mod;
import net.pzpeen.ben10mod.races.pyronite.PyroniteRace;

import java.util.HashMap;
import java.util.function.Supplier;

public class RacesRegistries {
    private static final HashMap<ResourceLocation, Supplier<? extends AbstractRace>> ALIEN_REGISTRIES = new HashMap<>();
    public static final ResourceLocation humanRaceID = ResourceLocation.fromNamespaceAndPath(Ben10Mod.MOD_ID, "human");


    public static void addAlienRegistry(ResourceLocation id, Supplier<? extends  AbstractRace> s){
        if(ALIEN_REGISTRIES.containsKey(id)){
            System.out.println("WARNING! ALIEN ID '"+id+"' already exists!");
            return;
        }
        ALIEN_REGISTRIES.put(id, s);
    }

    public static AbstractRace pickRace(ResourceLocation id){
        if(id == null || id.toString().equals(humanRaceID.toString())) return null; //Id null means that is human!
        Supplier<? extends AbstractRace> s = ALIEN_REGISTRIES.get(id);
        if(s != null){
            return s.get();
        }
        System.err.println("ERROR: TRYING TO PICK A INEXISTENT RACE " + id.toString());
        return null;
    }

    public static void register(){
        addAlienRegistry(humanRaceID, null);
        addAlienRegistry(PyroniteRace.id, PyroniteRace::new);
    }

    public static Iterable<String> getAllIDs(){
        return ALIEN_REGISTRIES.keySet().stream().map(ResourceLocation::toString).toList();
    }
}
