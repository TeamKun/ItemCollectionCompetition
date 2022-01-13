package net.kunmc.lab.itemcollectioncompetition.statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.Util;
import net.kunmc.lab.itemcollectioncompetition.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class ICCStatistics implements Listener {

  List<StatisticsItem> items = new ArrayList<>();

  public ICCStatistics() {
    Bukkit.getPluginManager().registerEvents(this, ItemCollectionCompetition.plugin);
    items.add(new KillCount());
    Config config = ItemCollectionCompetition.config;

    // 採掘
    for (Material material : config.breakTargetMaterials.value()) {
      items.add(new CollectionItemStack(ItemType.BLOCK_BREAK, material));
    }

    // クラフト
    for (Material material : config.craftTargetMaterials.value()) {
      items.add(new CollectionItemStack(ItemType.CRAFT, material));
    }

    // 強奪
    for (Material material : config.robberyTargetMaterials.value()) {
      items.add(new CollectionItemStack(ItemType.ROBBERY, material));
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player killer = event.getEntity().getKiller();

    if (killer == null) {
      return;
    }

    // キルカウント
    KillCount killCount = (KillCount) getItems(ItemType.KILL).get(0);
    killCount.put(killer);

    // アイテム強奪
    List<ItemStack> dropItemList = event.getDrops();
    List<StatisticsItem> robberyItemList = getItems(ItemType.ROBBERY);
    for (StatisticsItem item : robberyItemList) {
      if (item instanceof CollectionItemStack) {
        CollectionItemStack robberyItem = (CollectionItemStack) item;
        robberyItem.put(killer, dropItemList);
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();

    List<StatisticsItem> blockBreakItemList = getItems(ItemType.BLOCK_BREAK);
    for (StatisticsItem item : blockBreakItemList) {
      if (item instanceof CollectionItemStack) {
        CollectionItemStack blockBreakItem = (CollectionItemStack) item;
        blockBreakItem.put(player, event.getBlock());
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onCraftItem(CraftItemEvent event) {
    HumanEntity entity = event.getWhoClicked();
    if (!(entity instanceof Player)) {
      return;
    }
    Player player = (Player) entity;
    ItemStack resultItemStack = event.getRecipe().getResult();

    int craftResultAmount = 0;

    // シフトクリック時
    if (event.isShiftClick()) {
      // クラフト結果の個数を取得
      craftResultAmount = Arrays.stream(event.getInventory().getMatrix())
          .filter(Objects::nonNull)
          .map(ItemStack::getAmount)
          .min(Integer::compare)
          .get() * resultItemStack.getAmount();

      Util.log("L108" + craftResultAmount);

      // プレイヤーのインベントリに空きがない場合
      int space = getSpaceSize(resultItemStack, player);
      Util.log("space" + space);
      if (space == 0) {
        return;
      }

      // 空きスペースよりもクラフトアイテム数が多かった場合
      if (space < craftResultAmount) {
        craftResultAmount = space;
        Util.log("L119" + craftResultAmount);
      }
    } else {
      craftResultAmount = resultItemStack.getAmount();
    }

    List<StatisticsItem> blockBreakItemList = getItems(ItemType.CRAFT);
    for (StatisticsItem item : blockBreakItemList) {
      if (item instanceof CollectionItemStack) {
        CollectionItemStack craftItem = (CollectionItemStack) item;
        craftItem.put(player, craftResultAmount, resultItemStack.getType());
      }
    }
  }

  private List<StatisticsItem> getItems(ItemType type) {
    List<StatisticsItem> list = new ArrayList<>();

    for (StatisticsItem item : items) {
      if (item.itemType().equals(type)) {
        list.add(item);
      }
    }
    return list;
  }

  public void outputCSV() {
    PrintWriter printWriter = null;
    try {
      FileWriter fileWriter = new FileWriter("Statistics.csv", false);
      printWriter = new PrintWriter(new BufferedWriter(fileWriter));

      // ヘッダー
      printWriter.print("team");
      printWriter.print(",");
      printWriter.print("player");

      for (StatisticsItem item : this.items) {
        printWriter.print(",");
        printWriter.print(item.header());
      }
      printWriter.println();

      // 値
      for (Player player : Bukkit.getOnlinePlayers()) {
        // チーム
        String teamName = "";
        Team team = Util.affiliatedTeam(player);
        if (team != null) {
          teamName = team.getName();
        }
        printWriter.print(teamName);
        printWriter.print(",");

        // プレイヤー名
        printWriter.print(player.getName());

        //　各項目
        for (StatisticsItem item : this.items) {
          printWriter.print(",");
          printWriter.print(item.get(player));
        }
        printWriter.println();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      printWriter.close();
    }
  }

  // クラフトしたアイテムがインベントリに入る最大量の計算
  private int getSpaceSize(ItemStack target, Player player) {
    int size = 0;
    Inventory inventory = player.getInventory();

    for (int i = 0; i < 35; i++) {
      ItemStack stack = inventory.getItem(i);
      if (stack == null) {
        size += target.getMaxStackSize();
        continue;
      }

      if (stack.getType() == target.getType()) {
        size += target.getMaxStackSize() - stack.getAmount();
        continue;
      }
    }
    return size;
  }
}
