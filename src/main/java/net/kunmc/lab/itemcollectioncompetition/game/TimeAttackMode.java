package net.kunmc.lab.itemcollectioncompetition.game;

import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.Util;
import net.kunmc.lab.itemcollectioncompetition.config.DisplayType.DisplayTypeEnum;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeam;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeamList;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class TimeAttackMode extends Game {

  double currentTick = 0;
  BossBar bossBar;

  public TimeAttackMode(ICCTeamList iccTeamList) {
    super(iccTeamList);
    bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
    bossBar.setVisible(true);
  }

  @Override
  public void run() {
    progress();

    // 情報表示
    sendInfo();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!bossBar.getPlayers().contains(player)) {
        bossBar.addPlayer(player);
      }
    }

    // タイムアップ
    if (this.currentTick >= minutes(ItemCollectionCompetition.config.timeLimit.value())) {
      ICCTeam topTeam = this.iccTeamList.getTopTeam();
      Util.sendTitleAll(topTeam.name() + "の勝利!", "", 20, 60, 20);
      this.stop();
    }
  }

  private double minutes(double tick) {
    return tick * 20 * 60;
  }

  @Override
  public synchronized void cancel() throws IllegalStateException {
    this.bossBar.removeAll();
    super.cancel();
  }

  private void progress() {
    this.currentTick++;

    Util.log("1");
    double remainingSecond = (minutes(
        ItemCollectionCompetition.config.timeLimit.value()) - this.currentTick) / 20;

    String hour = String.valueOf((int) remainingSecond / 3600);
    String minute = String.valueOf((int) (remainingSecond % 3600) / 60);
    String second = String.valueOf((int) remainingSecond % 60);

    String barTitle = "残り ";

    Util.log("2");
    if (hour.length() == 1) {
      hour = "0".concat(hour);
    }

    barTitle = barTitle.concat(hour).concat(":");

    if (minute.length() == 1) {
      minute = "0".concat(minute);
    }

    barTitle = barTitle.concat(minute).concat(":");

    if (second.length() == 1) {
      second = "0".concat(second);
    }

    barTitle = barTitle.concat(second);

    this.bossBar.setTitle(barTitle);

    double progress = 1 -
        (this.currentTick / minutes(ItemCollectionCompetition.config.timeLimit.value()));

    Util.log(String.valueOf(progress));
    this.bossBar.setProgress(progress);

    if (progress < 0.2) {
      this.bossBar.setColor(BarColor.RED);
    } else if (progress <= 0.5) {
      this.bossBar.setColor(BarColor.YELLOW);
    } else {
      this.bossBar.setColor(BarColor.GREEN);
    }
  }

  @Override
  public void sendInfo() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.sendActionBar(this.iccTeamList.currentAmountInfo(DisplayTypeEnum.CURRENT));
    }
  }
}
