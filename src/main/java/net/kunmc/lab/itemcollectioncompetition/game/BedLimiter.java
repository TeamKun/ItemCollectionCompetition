package net.kunmc.lab.itemcollectioncompetition.game;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;

public class BedLimiter implements Listener {

  @EventHandler
  public void onBlockCanBuild(BlockCanBuildEvent event) {
    List<Material> BED_LIST = Arrays.asList(Material.WHITE_BED, Material.ORANGE_BED,
        Material.MAGENTA_BED, Material.LIGHT_BLUE_BED, Material.YELLOW_BED, Material.LIME_BED,
        Material.PINK_BED, Material.GRAY_BED, Material.LIGHT_GRAY_BED, Material.CYAN_BED,
        Material.PURPLE_BED, Material.BLUE_BED, Material.BROWN_BED, Material.GREEN_BED,
        Material.RED_BED, Material.BLACK_BED);

    if (!BED_LIST.contains(event.getMaterial())) {
      return;
    }

    if (!event.getPlayer().getWorld().getEnvironment().equals(Environment.NORMAL)) {
      event.setBuildable(false);
    }
  }
}
