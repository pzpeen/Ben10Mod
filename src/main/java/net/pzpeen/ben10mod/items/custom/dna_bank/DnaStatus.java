package net.pzpeen.ben10mod.items.custom.dna_bank;

public enum DnaStatus {
    LOCKED("locked"),
    STORED("stored"),
    UNLOCKED("unlocked");

    private final String nbtKey;

    DnaStatus(String nbtKey) {
        this.nbtKey = nbtKey;
    }

    public String getNbtKey(){
        return this.nbtKey;
    }

    public static DnaStatus fromKey(String nbtKey){
        for(DnaStatus status : values()){
            if(status.nbtKey.equals(nbtKey)) return status;
        }
        return LOCKED;
    }
}
