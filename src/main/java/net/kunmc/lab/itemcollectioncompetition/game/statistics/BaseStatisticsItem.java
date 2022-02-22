package net.kunmc.lab.itemcollectioncompetition.game.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public abstract class BaseStatisticsItem implements StatisticsItem {

  private final ItemType type;
  protected final Map<UUID, Integer> playerInfo = new HashMap<>();

  protected BaseStatisticsItem(ItemType type) {
    this.type = type;
  }

  @Override
  public ItemType itemType() {
    return this.type;
  }

  @Override
  public int get(Player player) {
    Integer value = this.playerInfo.get(player.getUniqueId());
    if (value == null) {
      return 0;
    }

    return value;
  }
}
