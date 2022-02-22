package net.kunmc.lab.itemcollectioncompetition.team;

import dev.kotx.flylib.command.CommandContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.command.CommandResult;
import net.kunmc.lab.itemcollectioncompetition.config.DisplayType.DisplayTypeEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class ICCTeamList {

  private final List<ICCTeam> iccTeamList = new ArrayList<>();

  private ICCTeam get(Team team) {
    for (ICCTeam iccTeam : iccTeamList) {
      if (iccTeam.equalsTeam(team)) {
        return iccTeam;
      }
    }
    return null;
  }

  public void setDeliveryChest(Team team, Chest chest, CommandContext ctx) {
    ICCTeam iccTeam = get(team);
    if (iccTeam == null) {
      iccTeam = new ICCTeam(team);
      iccTeamList.add(iccTeam);
    }

    iccTeam.setDeliveryChest(chest);
    Location location = chest.getLocation();
    new CommandResult(
        "x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ()
            + "のチェストを" + team.getName() + "チームの納品チェストに設定しました", true, ctx).broadcastFeedback();
  }

  public void setRespawn(Team team, Location location, CommandContext ctx) {
    ICCTeam iccTeam = get(team);
    if (iccTeam == null) {
      iccTeam = new ICCTeam(team);
      iccTeamList.add(iccTeam);
    }

    iccTeam.setRespawnPoint(location);
    new CommandResult(
        "x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ()
            + "を" + team.getName() + "チームのリスポーン地点に設定しました", true, ctx).sendFeedback();
  }

  public List<String> incompleteSettingList() {
    Set<Team> teamSet = ItemCollectionCompetition.config.teams.value();

    List<String> incompleteTeamNameList = new ArrayList<>();
    // 全てのチームがインスタンス化されているか
    for (Team team : teamSet) {
      if (get(team) == null) {
        incompleteTeamNameList.add(team.getName());
      }
    }

    // 全てのチームの準備が完了しているか
    for (ICCTeam iccTeam : iccTeamList) {
      if (!iccTeam.isCompleteSetting()) {
        incompleteTeamNameList.add(iccTeam.name());
      }
    }

    return incompleteTeamNameList;
  }

  public Component currentAmountInfo(DisplayTypeEnum displayTypeEnum) {
    Component component = Component.text(
        displayTypeEnum.jName + " ");

    for (ICCTeam iccTeam : this.iccTeamList) {
      component = component.append(Component.text(iccTeam.name())).append(Component.text(":"))
          .append(iccTeam.displayAmount(displayTypeEnum)).append(Component.text(" "));
    }

    return component;
  }

  public void executeSafetyArea(Player player) {
    for (ICCTeam iccTeam : this.iccTeamList) {
      iccTeam.reject(player);
    }
  }

  public ICCTeam getVictoryTeam() {
    for (ICCTeam iccTeam : this.iccTeamList) {
      if (iccTeam.isVictory()) {
        return iccTeam;
      }
    }
    return null;
  }

  public ICCTeam getTopTeam() {
    ICCTeam topTeam = null;

    for (ICCTeam iccTeam : this.iccTeamList) {
      if (topTeam == null) {
        topTeam = iccTeam;
      }

      if (iccTeam.currentAmount() > topTeam.currentAmount()) {
        topTeam = iccTeam;
      }
    }

    return topTeam;
  }

  public void clearDeliveryChestInventory() {
    for (ICCTeam iccTeam : this.iccTeamList) {
      iccTeam.clearDeliveryChestInventory();
    }
  }

  public void clearSettings() {
    for (ICCTeam iccTeam : this.iccTeamList) {
      iccTeam.clearSettings();
    }
  }

  public void setGameMode(GameMode gameMode) {
    for (ICCTeam team : this.iccTeamList) {
      team.setGameMode(gameMode);
    }
  }
}
