package net.pzpeen.ben10mod.systems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.pzpeen.ben10mod.items.custom.dna_bank.DnaStatus;

import java.util.HashMap;

public class DnaBank {
    private int DNAQuantity = 0;
    private final HashMap<ResourceLocation, DnaStatus> storedDNA = new HashMap<>();
    private int maxDnaCapability = 10;
    private int maxPlaylists = 1;
    @SuppressWarnings("unchecked")
    private final Playlist<ResourceLocation>[] playlists = new Playlist[10];

    public static final String dnaListTag = "dnaListTag";
    public static final String playlistTag = "playlistTag";

    public DnaBank(int maxDnaCapability, int maxPlaylists){
        this.maxDnaCapability = maxDnaCapability;
        this.maxPlaylists = Math.min(maxPlaylists, 10);
        for(int i = 0; i < this.maxPlaylists; i++){
            playlists[i] = new Playlist<>();
        }
    }

    public DnaStatus getDNAStatus(ResourceLocation alienID){
        return this.storedDNA.getOrDefault(alienID, DnaStatus.LOCKED);
    }

    public boolean addDNA(ResourceLocation alienID){
        if(storedDNA.containsKey(alienID) && storedDNA.get(alienID) != DnaStatus.LOCKED){
            return true;
        }
        if(DNAQuantity < maxDnaCapability){
            storedDNA.put(alienID, DnaStatus.STORED);
            DNAQuantity++;
            return true;
        }
        return false;
    }

    private void insertDNA(ResourceLocation alienID, DnaStatus status){
        storedDNA.put(alienID, status);
        DNAQuantity++;
    }

    public boolean removeDNA(ResourceLocation alienID){
        if(storedDNA.containsKey(alienID)){
            storedDNA.remove(alienID);
            DNAQuantity--;
            return true;
        }
        return false;
    }

    public boolean unlockDNA(ResourceLocation alienID){
        if(this.getDNAStatus(alienID) == DnaStatus.STORED){
            this.storedDNA.put(alienID, DnaStatus.UNLOCKED);
            return true;
        }
        return false;
    }

    public boolean lockDNA(ResourceLocation alienID){
        if(this.getDNAStatus(alienID) == DnaStatus.UNLOCKED){
            this.storedDNA.put(alienID, DnaStatus.STORED);
            return true;
        }
        return false;
    }

    public boolean isUnlocked(ResourceLocation alienID){
        return this.getDNAStatus(alienID) == DnaStatus.UNLOCKED;
    }

    public boolean isStored(ResourceLocation alienID){
        return this.getDNAStatus(alienID) == DnaStatus.STORED;
    }

    public Playlist<ResourceLocation> getPlaylist(int playlist){
        if(playlist >= 0 && playlist < maxPlaylists) return this.playlists[playlist];
        return null;
    }

    public CompoundTag saveToNBT(){
        CompoundTag nbt = new CompoundTag();

        CompoundTag dnaListNbt = new CompoundTag();

        storedDNA.forEach((id, status) -> dnaListNbt.putString(id.toString(), status.getNbtKey()));

        nbt.put(dnaListTag, dnaListNbt);

        CompoundTag playlistTag = new CompoundTag();
        for(int i = 0; i < maxPlaylists; i++){
            Playlist<ResourceLocation> playlistI = getPlaylist(i);
            ListTag playlistListTag = new ListTag();
            for(int j = 0; j < 10; j++){
                if(playlistI.get(j) != null){
                    playlistListTag.add(StringTag.valueOf(playlistI.get(j).toString()));
                }else{
                    playlistListTag.add(StringTag.valueOf("null"));
                }
            }
            playlistTag.put("playlist"+i, playlistListTag);
        }
        nbt.put(DnaBank.playlistTag, playlistTag);

        return nbt;
    }

    public static DnaBank getFromNBT(CompoundTag nbt, int maxDnaCapability, int maxPlaylists){
        DnaBank dnaBank = new DnaBank(maxDnaCapability, maxPlaylists);

        if(nbt.contains(DnaBank.dnaListTag)){
            CompoundTag dnaListTag = nbt.getCompound(DnaBank.dnaListTag);

            for(String sAlienID : dnaListTag.getAllKeys()){
                ResourceLocation alienID = ResourceLocation.parse(sAlienID);
                String sStatus = dnaListTag.getString(sAlienID);
                //System.out.println("status "+ sAlienID + ":" + sStatus);
                DnaStatus status = DnaStatus.fromKey(sStatus);
                dnaBank.insertDNA(alienID, status);

            }

        }

        if(nbt.contains(DnaBank.playlistTag)){
            CompoundTag playlistsTag = nbt.getCompound(DnaBank.playlistTag);
            for(int i = 0; i < dnaBank.maxPlaylists; i++){
                String sPlaylistI = "playlist"+i;
                if(playlistsTag.contains(sPlaylistI)){
                    ListTag playlistIListTag = playlistsTag.getList(sPlaylistI, StringTag.TAG_STRING);
                    Playlist<ResourceLocation> playlistI = dnaBank.getPlaylist(i);

                    for(int j = 0; j < 10; j++){
                        if(!playlistIListTag.getString(j).equals("null")){
                            playlistI.add(ResourceLocation.parse(playlistIListTag.getString(j)));
                        }else{
                            playlistI.set(j, null);
                        }
                    }

                }

            }

        }

        return dnaBank;
    }

}
