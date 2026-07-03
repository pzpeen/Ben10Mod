package net.pzpeen.ben10mod.powers;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Supplier;

public class PowerRegistries {
    private static final HashMap<ResourceLocation, Supplier<? extends BasePower>> POWER_REGISTRIES = new HashMap<>();

    public static void addPower(ResourceLocation id, Supplier<? extends BasePower> s){
        if(POWER_REGISTRIES.containsKey(id)){
            System.err.println("WARNING! POWER ID '"+id+"' already exists!");
            return;
        }
        POWER_REGISTRIES.put(id, s);

    }

    public static BasePower pickPower(ResourceLocation id){
        if(id == null) return null;
        Supplier<? extends BasePower> s = POWER_REGISTRIES.get(id);
        if(s != null){
            return s.get();
        }
        System.err.println("ERROR: TRYING TO PICK A INEXISTENT POWER " + id.toString());
        return null;
    }

    public static void register(){
        addPower(OmnitrixPower.id, OmnitrixPower::new);
    }

    public static Iterable<String> getAllIDs(){
        return POWER_REGISTRIES.keySet().stream().map(ResourceLocation::toString).toList();
    }
}
