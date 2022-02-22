package net.kunmc.lab.itemcollectioncompetition.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.configlib.command.ConfigCommand;
import org.jetbrains.annotations.NotNull;

public class Main extends Command {

  public Main(@NotNull String name, ConfigCommand configCommand) {
    super(name);
    children(new Start("start"),
        new Stop("stop"),
        new SetChest("setChest"),
        new SetRespawn("setRespawn"),
        new ClearSettings("clearSettings"),
        new Pause("pause"),
        new Restart("restart"),
        configCommand);
  }
}
