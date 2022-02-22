package net.kunmc.lab.itemcollectioncompetition.game.statistics;

import java.util.UUID;
import org.bukkit.entity.Player;

public class KillCount extends BaseStatisticsItem {

  public KillCount() {
    super(ItemType.KILL);
  }

  public void put(Player player) {
    UUID uuid = player.getUniqueId();
    if (!this.playerInfo.containsKey(player.getUniqueId())) {
      this.playerInfo.put(uuid, 0);
    }

    this.playerInfo.put(uuid, this.playerInfo.get(uuid) + 1);
  }

  @Override
  public String header() {
    return this.itemType().header;
  }

}
