package net.kunmc.lab.itemcollectioncompetition.game;

import dev.kotx.flylib.command.CommandContext;
import java.util.List;
import net.kunmc.lab.itemcollectioncompetition.Util;
import net.kunmc.lab.itemcollectioncompetition.command.CommandResult;
import net.kunmc.lab.itemcollectioncompetition.statistics.ICCStatistics;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeam;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeamList;
import net.kyori.adventure.text.Component;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class GameManager {

  private static Game game;
  private static final ICCTeamList iccTeamList = new ICCTeamList();
  private static ICCStatistics statistics;

  public static void start(CommandContext ctx) {
    List<String> incompleteTeamList = iccTeamList.incompleteSettingList();
    if (incompleteTeamList.size() == 0) {

      game = new Game();
      statistics = new ICCStatistics();
      iccTeamList.clearDeliveryChestInventory();
      Util.sendTitleAll("ゲーム開始", null, 20, 100, 20);
      new CommandResult("ゲームを開始します", true, ctx).sendFeedback();
      return;
    }

    String msg = "以下のチームの準備が整っていません。";
    for (String teamName : incompleteTeamList) {
      msg += teamName + " ";
    }
    new CommandResult(msg, false, ctx).sendFeedback();
    return;
  }

  public static void stop(CommandContext ctx) {
    if (game == null) {
      new CommandResult("ゲーム実行中ではありません", false, ctx).sendFeedback();
      return;
    }

    game.cancel();
    game = null;
    new CommandResult("ゲームを強制終了しました", true, ctx).sendFeedback();
    return;
  }

  public static void setDeliveryChest(CommandContext ctx) {
    Team team = Util.getTeam(ctx.getArgs().get(0));
    if (team == null) {
      new CommandResult("存在しないチームです", false, ctx).sendFeedback();
      return;
    }

    // 視線の再起のチェストを取得
    BlockState blockState = ctx.getPlayer().rayTraceBlocks(64).getHitBlock().getState();
    if (!(blockState instanceof Chest)) {
      new CommandResult("視線にチェストを入れてコマンドを入力してください", false, ctx).sendFeedback();
      return;
    }

    iccTeamList.setDeliveryChest(team, (Chest) blockState, ctx);
  }

  public static void setRespawn(CommandContext ctx) {
    Team team = Util.getTeam(ctx.getArgs().get(0));
    if (team == null) {
      new CommandResult("参加リストに存在しないチームです", false, ctx).sendFeedback();
      return;
    }

    iccTeamList.setRespawn(team, ctx.getPlayer().getLocation(), ctx);
  }

  public static void clearSettings(CommandContext ctx) {
    iccTeamList.clearSettings();
    ctx.success("各位チームの設定をクリアしました");
  }

  static Component currentAmountInfo() {
    return iccTeamList.currentAmountInfo();
  }

  public static void executeSafetyArea(Player player) {
    iccTeamList.executeSafetyArea(player);
  }

  public static void judgeVictory() {
    ICCTeam team = iccTeamList.getVictoryTeam();
    if (team == null) {
      return;
    }

    Util.sendTitleAll(team.name() + "の勝利!", "", 20, 60, 20);
    game.cancel();
    statistics.outputCSV();
    game = null;
  }
}
