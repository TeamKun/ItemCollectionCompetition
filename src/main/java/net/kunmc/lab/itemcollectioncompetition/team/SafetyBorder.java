package net.kunmc.lab.itemcollectioncompetition.team;

import java.util.ArrayList;
import java.util.List;
import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.Util;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class SafetyBorder {

  List<Location> locationList = new ArrayList<>();

  public SafetyBorder(Location center, int halfRange, int underRange) {
    int centerX = center.getBlockX();
    int centerZ = center.getBlockZ();
    int centerY = center.getBlockY() - underRange;

    for (int y = centerY; y < 255; y++) {

      if (y == centerY) {
        for (int x = centerX - halfRange; x < centerX + halfRange; x++) {
          for (int z = centerZ - halfRange; z < centerZ + halfRange; z++) {
            Location location = new Location(center.getWorld(), x, y, z);
            if (location.distance(new Location(center.getWorld(), centerX, y, centerZ))
                <= halfRange) {
              this.locationList.add(location);
            }
          }
        }
      }

      for (int angle = 0; angle < 359; angle++) {
        double x = centerX + (halfRange - 1) * Math.cos(angle * (Math.PI / 180));
        double z = centerZ + (halfRange - 1) * Math.sin(angle * (Math.PI / 180));
        this.locationList.add(new Location(center.getWorld(), x, y, z));
      }
    }

    Util.log(String.valueOf(this.locationList.size()));
  }

  public void spawnParticle(Player player) {
    if (!ItemCollectionCompetition.config.enableSafetyAreaParticle.value()) {
      return;
    }
    World world = player.getWorld();
    Location eyeLoc = player.getEyeLocation();
    for (Location location : this.locationList) {
      if (location.distance(eyeLoc) <= 3) {
        world.spawnParticle(Particle.ENCHANTMENT_TABLE, location, 1);
      }
    }
  }
}
