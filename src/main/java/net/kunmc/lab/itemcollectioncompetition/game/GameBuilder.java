package net.kunmc.lab.itemcollectioncompetition.game;

import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.team.ICCTeamList;

public class GameBuilder {

  static Game build(ICCTeamList iccTeamList) {
    String modeName = ItemCollectionCompetition.config.mode.value();

    if (modeName.equalsIgnoreCase(Mode.AMOUNT.modeName())) {
      return new AmountMode(iccTeamList);
    }

    return new TimeAttackMode(iccTeamList);
  }
}
