package net.kunmc.lab.itemcollectioncompetition.team;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DeliveryChest extends BukkitRunnable implements Listener {

  private final Chest chest;
  private int currentAmount;
  private SafetyBorder safetyBorder;

  public DeliveryChest(Chest chest) {
    IntegerValue safetyAreaHalfRange = ItemCollectionCompetition.config.safetyAreaHalfRange;
    IntegerValue safetyAreaUnderRange = ItemCollectionCompetition.config.safetyAreaUnderRange;

    this.chest = chest;
    Plugin plugin = ItemCollectionCompetition.plugin;
    Bukkit.getPluginManager().registerEvents(this, plugin);
    this.runTaskTimerAsynchronously(plugin, 0, 1);
    this.safetyBorder = new SafetyBorder(chest.getLocation(),
        safetyAreaHalfRange.value(),
        safetyAreaUnderRange.value());

    safetyAreaHalfRange.onSet((value, commandContext) -> {
      this.safetyBorder = new SafetyBorder(this.chest.getLocation(), value,
          ItemCollectionCompetition.config.safetyAreaUnderRange.value());
    });

    safetyAreaUnderRange.onSet((value, commandContext) -> {
      this.safetyBorder = new SafetyBorder(this.chest.getLocation(),
          ItemCollectionCompetition.config.safetyAreaHalfRange.value(), value);
    });
  }

  public int currentAmount() {
    return this.currentAmount;
  }

  public boolean isInSafeArea(Location targetLocation) {
    Location center = this.chest.getLocation();

    // チェストと目標の平面上の距離
    Location center2D = center.clone();
    center2D.setY(0);
    Location targetLocation2D = targetLocation.clone();
    targetLocation2D.setY(0);

    if (center2D.distance(targetLocation2D)
        >= ItemCollectionCompetition.config.safetyAreaHalfRange.value()) {
      return false;
    }

    // 高さの判定
    center = center.add(0, ItemCollectionCompetition.config.safetyAreaUnderRange.value() * -1, 0);
    if (center.getBlockY() <= targetLocation.getBlockY()) {
      return true;
    }

    return false;
  }

  public void spawnSafeBorderParticle(Player player) {
    this.safetyBorder.spawnParticle(player);
  }

  public Location location() {
    return this.chest.getLocation();
  }

  public void clearInventory() {
    this.chest.getInventory().clear();
  }

  @Override
  public void run() {

    // 集計ロジック
    int amount = 0;
    Inventory inventory = chest.getInventory();
    for (ItemStack itemStack : inventory) {
      if (itemStack != null) {
        if (itemStack.getType().equals(ItemCollectionCompetition.config.targetItem.value())) {
          amount += itemStack.getAmount();
        }
      }
    }
    this.currentAmount = amount;
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (event.getBlock().getLocation().equals(this.chest.getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockDestroy(BlockDestroyEvent event) {
    if (event.getBlock().getLocation().equals(this.chest.getLocation())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onExplosionPrime(ExplosionPrimeEvent event) {
    if (isInSafeArea(event.getEntity().getLocation())) {
      event.setCancelled(true);
    }
  }
  
}
