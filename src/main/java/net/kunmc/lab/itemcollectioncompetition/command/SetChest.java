package net.kunmc.lab.itemcollectioncompetition.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.game.GameManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class SetChest extends Command {

  public SetChest(@NotNull String name) {
    super(name);
    usage(usageBuilder -> {
      usageBuilder.textArgument("team", suggestionBuilder -> {
        for (Team team : ItemCollectionCompetition.config.teams) {
          suggestionBuilder.suggest(team.getName());
        }
      });
    });
  }

  @Override
  public void execute(@NotNull CommandContext ctx) {
    GameManager.setDeliveryChest(ctx);
  }
}
