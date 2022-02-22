package net.kunmc.lab.itemcollectioncompetition.game.statistics;

import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CollectionItemStack extends BaseStatisticsItem {

  private final Material targetMaterial;

  public CollectionItemStack(ItemType type, Material targetMaterial) {
    super(type);
    this.targetMaterial = targetMaterial;
  }

  public void put(Player player, List<ItemStack> itemStackList) {
    int count = 0;
    for (ItemStack itemStack : itemStackList) {
      if (itemStack.getType().equals(this.targetMaterial)) {
        count += itemStack.getAmount();
      }
    }

    if (count == 0) {
      return;
    }

    UUID uuid = player.getUniqueId();
    if (!this.playerInfo.containsKey(uuid)) {
      this.playerInfo.put(uuid, 0);
    }

    this.playerInfo.put(uuid, this.playerInfo.get(uuid) + count);
  }

  public void put(Player player, Block block) {
    if (!block.getType().equals(this.targetMaterial)) {
      return;
    }
    UUID uuid = player.getUniqueId();
    if (!this.playerInfo.containsKey(uuid)) {
      this.playerInfo.put(uuid, 0);
    }

    this.playerInfo.put(uuid, this.playerInfo.get(uuid) + 1);
  }

  public void put(Player player, int amount, Material itemType) {
    if (!itemType.equals(this.targetMaterial)) {
      return;
    }

    UUID uuid = player.getUniqueId();
    if (!this.playerInfo.containsKey(uuid)) {
      this.playerInfo.put(uuid, 0);
    }

    this.playerInfo.put(uuid, this.playerInfo.get(uuid) + amount);
  }

  @Override
  public String header() {
    return this.itemType().header + ":" + this.targetMaterial.name();
  }
}