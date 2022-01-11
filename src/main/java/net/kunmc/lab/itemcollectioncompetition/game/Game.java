package net.kunmc.lab.itemcollectioncompetition.game;

import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Game extends BukkitRunnable implements Listener {

  public Game() {
    Plugin plugin = ItemCollectionCompetition.plugin;
    this.runTaskTimerAsynchronously(plugin, 0, 1);
  }

  @Override
  public void run() {
    // 情報表示
    sendInfo();

    // セーフティエリアからプレイヤーをはじく処理
    if (ItemCollectionCompetition.config.enableSafetyArea.value()) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        GameMode gamemode = player.getGameMode();
        if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
          continue;
        }
        GameManager.executeSafetyArea(player);
      }
    }

    // 勝敗判定
    GameManager.judgeVictory();
  }

  private void sendInfo() {
    for (Player player : Bukkit.getOnlinePlayers()) {

      player.sendActionBar(GameManager.currentAmountInfo());
    }
  }
}
