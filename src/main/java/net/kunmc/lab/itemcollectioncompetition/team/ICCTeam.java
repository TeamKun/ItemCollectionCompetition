package net.kunmc.lab.itemcollectioncompetition.team;

import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.config.DisplayType.DisplayTypeEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
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

  public Component displayAmount() {
    int current = this.deliveryChest.currentAmount();

    if (ItemCollectionCompetition.config.displayType.enumValue().equals(DisplayTypeEnum.CURRENT)) {
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
    Vector velocity = playerLocation.subtract(center).toVector().normalize();
    player.setVelocity(player.getVelocity().add(velocity));

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
}