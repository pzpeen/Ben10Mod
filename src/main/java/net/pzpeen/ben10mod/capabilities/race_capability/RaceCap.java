package net.pzpeen.ben10mod.capabilities.race_capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.pzpeen.ben10mod.races.AbstractRace;
import net.pzpeen.ben10mod.races.RacesRegistries;

import javax.annotation.Nullable;

@AutoRegisterCapability
public class RaceCap {
    private Player player;
    private AbstractRace race = null;
    private ResourceLocation alienId = RacesRegistries.humanRaceID;

    public RaceCap(Player player) {
        this.player = player;
    }

    public AbstractRace getRace(){
        return this.race;
    }
    public ResourceLocation getRaceId(){
        if(this.getRace() != null){
            return this.race.getID();
        }else{
            return RacesRegistries.humanRaceID;
        }

    }

    public void setRace(ResourceLocation raceId, Player player){
        if(this.player == null){
            this.player = player;
        }
        this.alienId = raceId;
        this.race = RacesRegistries.pickRace(raceId, player);
    }

    public void saveNBT(CompoundTag nbt){
        nbt.putString("alien_id", this.alienId.toString());
    }
    public void loadNBT(CompoundTag nbt){
        //this.setRace(new ResourceLocation(nbt.getString("alien_id")));
        this.setRace(ResourceLocation.parse(nbt.getString("alien_id")), player);
    }

}
