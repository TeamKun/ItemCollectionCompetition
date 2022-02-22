package net.kunmc.lab.itemcollectioncompetition.game.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.Util;
import net.kunmc.lab.itemcollectioncompetition.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Team;

public class ICCStatistics implements Listener {

  List<StatisticsItem> items = new ArrayList<>();
  private final UUID GAME_ID = UUID.randomUUID();

  public ICCStatistics() {
    Bukkit.getPluginManager().registerEvents(this, ItemCollectionCompetition.plugin);
    items.add(new KillCount());
    items.add(new DeathCount());
    Config config = ItemCollectionCompetition.config;

    // 採掘
    for (Material material : config.breakTargetMaterials.value()) {
      items.add(new CollectionItemStack(ItemType.BLOCK_BREAK, material));
    }

    // クラフト
    for (Material material : config.craftTargetMaterials.value()) {
      items.add(new CollectionItemStack(ItemType.CRAFT, material));
    }

    // 強奪した
    for (Material material : config.robberyTargetMaterials.value()) {
      items.add(new CollectionItemStack(ItemType.ROBBERY, material));
    }

    // 強奪された
    for (Material material : config.robbedTargetMaterials.value()) {
      items.add(new CollectionItemStack(ItemType.ROBBED, material));
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {

    // キルカウント
    Player killer = event.getEntity().getKiller();

    if (killer == null) {
      return;
    }

    // キルカウント
    KillCount killCount = (KillCount) getItems(ItemType.KILL).get(0);
    killCount.put(killer);

    // アイテム強奪した
    List<ItemStack> dropItemList = event.getDrops();
    List<StatisticsItem> robberyItemList = getItems(ItemType.ROBBERY);
    for (StatisticsItem item : robberyItemList) {
      if (item instanceof CollectionItemStack) {
        CollectionItemStack robberyItem = (CollectionItemStack) item;
        robberyItem.put(killer, dropItemList);
      }
    }

    // デスカウント
    Player dead = event.getEntity();
    DeathCount deathCount = (DeathCount) getItems(ItemType.DEATH).get(0);
    deathCount.put(dead);

    // アイテム強奪された
    List<StatisticsItem> robbedItemList = getItems(ItemType.ROBBED);
    for (StatisticsItem item : robbedItemList) {
      if (item instanceof CollectionItemStack) {
        CollectionItemStack robberyItem = (CollectionItemStack) item;
        robberyItem.put(dead, dropItemList);
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();

    // 設置されたブロックか判定
    if (!block.getMetadata(this.GAME_ID.toString()).isEmpty()) {
      return;
    }

    List<StatisticsItem> blockBreakItemList = getItems(ItemType.BLOCK_BREAK);
    for (StatisticsItem item : blockBreakItemList) {
      if (item instanceof CollectionItemStack) {
        CollectionItemStack blockBreakItem = (CollectionItemStack) item;
        blockBreakItem.put(player, block);
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onCraftItem(CraftItemEvent event) {
    HumanEntity entity = event.getWhoClicked();
    if (!(entity instanceof Player)) {
      return;
    }

    if (event.getClick() == ClickType.MIDDLE) {
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

      // プレイヤーのインベントリに空きがない場合
      int space = getSpaceSize(resultItemStack, player);
      if (space == 0) {
        return;
      }

      // 空きスペースよりもクラフトアイテム数が多かった場合
      if (space < craftResultAmount) {
        craftResultAmount = space;
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
      //CSVデータファイル
      File csv = new File(ItemCollectionCompetition.plugin.getDataFolder(), "statistics.csv");

      printWriter = new PrintWriter(csv);
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
    } catch (FileNotFoundException e) {
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

  @EventHandler(ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    Block block = event.getBlock();
    block.setMetadata(this.GAME_ID.toString(),
        new FixedMetadataValue(ItemCollectionCompetition.plugin, null));
  }
}