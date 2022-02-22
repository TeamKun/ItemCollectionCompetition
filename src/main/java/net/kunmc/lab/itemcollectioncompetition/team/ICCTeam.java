package net.kunmc.lab.itemcollectioncompetition.team;

import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.config.DisplayType.DisplayTypeEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class ICCTeam implements Listener {

  private final Team team;
  private DeliveryChest deliveryChest;
  private Location respawnPoint;

  public ICCTeam(Team team) {
    this.team = team;
    Bukkit.getPluginManager().registerEvents(this, ItemCollectionCompetition.plugin);
  }

  public String name() {
    return this.team.getName();
  }

  public int currentAmount() {
    return this.deliveryChest.currentAmount();
  }

  public Component displayAmount(DisplayTypeEnum displayTypeEnum) {
    int current = this.currentAmount();

    if (displayTypeEnum.equals(DisplayTypeEnum.CURRENT)) {
      return Component.text(current);
    }

    return Component.text(ItemCollectionCompetition.config.targetAmount.value() - current);
  }

  public boolean isVictory() {
    return this.deliveryChest.currentAmount()
        >= ItemCollectionCompetition.config.targetAmount.value();
  }

  public void clearSettings() {
    this.deliveryChest = null;
    this.respawnPoint = null;
  }

  public void setDeliveryChest(Chest chest) {
    this.deliveryChest = new DeliveryChest(chest);
  }

  public void setRespawnPoint(Location location) {
    this.respawnPoint = location;
  }

  public boolean isCompleteSetting() {
    return this.deliveryChest != null && this.respawnPoint != null;
  }

  public void setGameMode(GameMode gameMode) {
    for (OfflinePlayer offlinePlayer : team.getPlayers()) {
      if (offlinePlayer.isOnline()) {
        Player player = (Player) offlinePlayer;
        player.setGameMode(gameMode);
      }
    }
  }

  public void clearDeliveryChestInventory() {
    this.deliveryChest.clearInventory();
  }

  public boolean equalsTeam(Team team) {
    return this.team.equals(team);
  }

  public void reject(Player player) {
    // プレイヤーがこのチームのメンバーか
    if (team.hasEntry(player.getName())) {
      return;
    }

    // セーフティエリアの中か
    if (!this.deliveryChest.isInSafeArea(player.getLocation())) {
      return;
    }

    Location playerLocation = player.getLocation();
    Location center = this.deliveryChest.location();

    // プレイヤーがチェストより下の場合
    if (playerLocation.getBlockY() >= center.getBlockY()) {
      center.setY(playerLocation.getY());
    }

    if (player.getVehicle() != null) {
      new BukkitRunnable() {
        @Override
        public void run() {
          player.leaveVehicle();
        }
      }.runTask(ItemCollectionCompetition.plugin);
    }

    double velocityRate = 0.1;

    if (player.isGliding()) {
      velocityRate = 1;
    }

    Vector velocity = playerLocation.subtract(center).toVector().normalize().multiply(velocityRate);
    player.setVelocity(player.getVelocity().add(velocity));

    this.deliveryChest.spawnSafeBorderParticle(player);
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    if (this.respawnPoint == null) {
      return;
    }
    Player player = event.getPlayer();
    if (!this.team.hasEntry(player.getName())) {
      return;
    }

    event.setRespawnLocation(this.respawnPoint);
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();

    if (!this.team.hasEntry(player.getName())) {
      return;
    }

    // セーフティエリアの中か
    if (!this.deliveryChest.isInSafeArea(player.getLocation())) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (!ItemCollectionCompetition.config.enableSafetyArea.value()) {
      return;
    }
    Player player = event.getPlayer();

    if (this.team.hasEntry(player.getName())) {
      return;
    }

    // セーフティエリアの中か
    if (!this.deliveryChest.isInSafeArea(event.getBlock().getLocation())) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockCanBuild(BlockCanBuildEvent event) {
    if (!ItemCollectionCompetition.config.enableSafetyArea.value()) {
      return;
    }
    Player player = event.getPlayer();

    if (this.team.hasEntry(player.getName())) {
      return;
    }

    // セーフティエリアの中か
    if (!this.deliveryChest.isInSafeArea(event.getBlock().getLocation())) {
      return;
    }

    event.setBuildable(false);
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryOpen(InventoryOpenEvent event) {
    if (!ItemCollectionCompetition.config.enableSafetyArea.value()) {
      return;
    }
    if (!(event.getPlayer() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getPlayer();

    if (this.team.hasEntry(player.getName())) {
      return;
    }

    Location targetLocation = event.getInventory().getLocation();
    if (this.deliveryChest.location().equals(targetLocation)) {
      event.setCancelled(true);
    }
  }
}
