package de.articdive.lwckeys.implementations;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyImplementation {
    public boolean isAreaClaimed(Location location) {
        return getTown(location) != null;
    }
    
    public boolean isResidentOfTown(Player player, Location location) {
        Town town = getTown(location);
        if (town == null) {
            return false;
        }
        com.palmergames.bukkit.towny.object.Resident resident;
        try {
            resident = com.palmergames.bukkit.towny.object.TownyUniverse.getDataSource().getResident(player.getName());
        } catch (com.palmergames.bukkit.towny.exceptions.NotRegisteredException e) {
            resident = null;
        }
        return town.getResidents().contains(resident);
    }
    
    private Town getTown(Location location) {
        TownBlock tb = com.palmergames.bukkit.towny.object.TownyUniverse.getTownBlock(location);
        if (tb == null) {
            return null;
        }
        try {
            return tb.getTown();
        } catch (com.palmergames.bukkit.towny.exceptions.NotRegisteredException e) {
            return null;
        }
    }
}
