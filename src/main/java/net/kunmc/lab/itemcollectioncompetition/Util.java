package net.kunmc.lab.itemcollectioncompetition;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import xyz.haoshoku.nick.api.NickAPI;

public class Util {

  public static Team getTeam(String teamName) {
    return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
  }

  /**
   * 全員にタイトルを表示
   */
  public static void sendTitleAll(String title, String subtitle, int fadeIn, int stay,
      int fadeOut) {
    Bukkit.getOnlinePlayers().forEach(player -> {
      player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    });
  }

  /**
   * タイトルをクリア
   */
  public static void clearTitle() {
    Bukkit.getOnlinePlayers().forEach(player -> {
      player.sendTitle("", "", 0, 0, 0);
    });
  }

  /**
   * 全員にメッセージを表示
   */
  public static void broadcast(String message) {
    Bukkit.broadcast(Component.text(message));
  }

  /**
   * ログ出力
   */
  public static void log(String message) {
    Bukkit.getLogger().info(message);
  }

  /**
   * スキンを変更する
   */
  public static void changeSkin(Player player, String toSkinPlayerName) {
    NickAPI.setSkin(player, toSkinPlayerName);
    NickAPI.refreshPlayer(player);
  }

  /**
   * 全員のスキンをリセットする
   */
  public static void clearSkin() {
    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
      if (offlinePlayer.isOnline()) {
        Player player = (Player) offlinePlayer;
        NickAPI.resetSkin(player);
        NickAPI.refreshPlayer(player);
      }
    }
  }

  /**
   * プレイヤーの所属チームを取得
   */
  public static Team affiliatedTeam(Player player) {
    for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
      if (team.hasEntry(player.getName())) {
        return team;
      }
    }

    return null;
  }
}
