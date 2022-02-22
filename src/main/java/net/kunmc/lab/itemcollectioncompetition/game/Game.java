package net.kunmc.lab.itemcollectioncompetition.game;

import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.game.statistics.ICCStatistics;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeamList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Game extends BukkitRunnable {

  protected final ICCTeamList iccTeamList;
  private final ICCStatistics statistics;

  boolean isRunning = true;

  public Game(ICCTeamList iccTeamList) {
    this.iccTeamList = iccTeamList;
    Plugin plugin = ItemCollectionCompetition.plugin;
    this.iccTeamList.setGameMode(GameMode.SURVIVAL);
    this.statistics = new ICCStatistics();
    this.runTaskTimerAsynchronously(plugin, 10, 1);
  }

  /**
   * セーフティエリアからプレイヤーをはじく処理
   */
  protected void executeSafetyArea() {
    if (!ItemCollectionCompetition.config.enableSafetyArea.value()) {
      return;
    }

    for (Player player : Bukkit.getOnlinePlayers()) {
      GameMode gamemode = player.getGameMode();
      if (gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR)) {
        continue;
      }
      this.iccTeamList.executeSafetyArea(player);
    }
  }

  protected void sendInfo() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.sendActionBar(
          this.iccTeamList.currentAmountInfo(
              ItemCollectionCompetition.config.displayType.enumValue()));
    }
  }

  boolean isRunning() {
    return this.isRunning;
  }

  void stop() {
    this.cancel();
    this.isRunning = false;
    statistics.outputCSV();
  }
}
