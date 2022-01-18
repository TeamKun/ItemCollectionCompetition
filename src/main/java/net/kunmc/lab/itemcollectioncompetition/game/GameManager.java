package net.kunmc.lab.itemcollectioncompetition.game;

import dev.kotx.flylib.command.CommandContext;
import java.util.List;
import net.kunmc.lab.itemcollectioncompetition.Util;
import net.kunmc.lab.itemcollectioncompetition.command.CommandResult;
import net.kunmc.lab.itemcollectioncompetition.statistics.ICCStatistics;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeam;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeamList;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;

public class GameManager {

  private static Game game;
  private static final ICCTeamList iccTeamList = new ICCTeamList();
  private static ICCStatistics statistics;

  public static void start(CommandContext ctx) {
    if (game != null) {
      new CommandResult("ゲームが実行中です", false, ctx).sendFeedback();
      return;
    }
    List<String> incompleteTeamList = iccTeamList.incompleteSettingList();
    if (incompleteTeamList.size() == 0) {
      iccTeamList.clearDeliveryChestInventory();
      game = new Game();
      statistics = new ICCStatistics();
      Util.sendTitleAll("ゲーム開始", null, 20, 60, 20);
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

    // 視線の先のチェストを取得
    BlockState blockState = getChestFromRayTrace(ctx.getPlayer().rayTraceBlocks(16));
    if (blockState == null) {
      new CommandResult("視線にチェストを入れてコマンドを入力してください", false, ctx).sendFeedback();
      return;
    }

    iccTeamList.setDeliveryChest(team, (Chest) blockState, ctx);
  }

  private static BlockState getChestFromRayTrace(RayTraceResult rayTraceResult) {
    if (rayTraceResult == null) {
      return null;
    }

    Block target = rayTraceResult.getHitBlock();

    if (target == null) {
      return null;
    }

    BlockState blockState = target.getState();
    if (!(blockState instanceof Chest)) {
      return null;
    }

    return blockState;
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
