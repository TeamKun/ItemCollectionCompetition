package net.kunmc.lab.itemcollectioncompetition.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.itemcollectioncompetition.game.GameManager;
import org.jetbrains.annotations.NotNull;

public class Pause extends Command {

  public Pause(@NotNull String name) {
    super(name);
  }

  @Override
  public void execute(@NotNull CommandContext ctx) {
    GameManager.Pause(ctx);
  }
}
