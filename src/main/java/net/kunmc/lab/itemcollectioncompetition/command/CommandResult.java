package net.kunmc.lab.itemcollectioncompetition.command;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.itemcollectioncompetition.DecolationConst;
import net.kunmc.lab.itemcollectioncompetition.Util;


public class CommandResult {

  private final String message;
  private final boolean isSucceed;
  private final CommandContext ctx;

  public CommandResult(String message, boolean isSucceed, CommandContext ctx) {
    this.message = message;
    this.isSucceed = isSucceed;
    this.ctx = ctx;
  }

  public void sendFeedback() {
    if (isSucceed) {
      this.ctx.success(this.message);
      return;
    }

    this.ctx.fail(this.message);
  }

  public void broadcastFeedback() {
    if (isSucceed) {
      Util.broadcast(DecolationConst.GREEN + this.message);
      return;
    }

    Util.broadcast(DecolationConst.RED + this.message);
  }
}
