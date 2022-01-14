package net.kunmc.lab.itemcollectioncompetition.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.itemcollectioncompetition.ItemCollectionCompetition;
import net.kunmc.lab.itemcollectioncompetition.game.GameManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class SetRespawn extends Command {

  public SetRespawn(@NotNull String name) {
    super(name);
    usage(usageBuilder -> {
      usageBuilder.stringArgument("team", suggestionBuilder -> {
        for (Team team : ItemCollectionCompetition.config.teams) {
          suggestionBuilder.suggest(team.getName());
        }
      }, null);
    });
  }

  @Override
  public void execute(@NotNull CommandContext ctx) {
    if (ctx.getArgs().size() == 0) {
      ctx.sendHelp();
      return;
    }
    GameManager.setRespawn(ctx);
  }
}
