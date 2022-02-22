package net.kunmc.lab.itemcollectioncompetition.game;

import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.Util;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeam;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeamList;

public class AmountMode extends Game {

  public AmountMode(ICCTeamList iccTeamList) {
    super(iccTeamList);
  }

  @Override
  public void run() {
    // 情報表示
    sendInfo();

    // 勝敗判定
    ICCTeam topTeam = this.iccTeamList.getTopTeam();

    this.executeSafetyArea();

    if (topTeam.currentAmount() >= ItemCollectionCompetition.config.targetAmount.value()) {
      Util.sendTitleAll(topTeam.name() + "の勝利!", "", 20, 60, 20);
      this.stop();
    }
  }
}
