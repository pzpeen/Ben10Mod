package net.pzpeen.ben10mod.capabilities;

import net.pzpeen.ben10mod.capabilities.power_capability.PowerCap;
import net.pzpeen.ben10mod.capabilities.race_capability.RaceCap;

public interface IBen10ModCapCache {
    RaceCap ben10Mod$getCachedRaceCap();
    PowerCap ben10Mod$getCachedPowerCap();
}
