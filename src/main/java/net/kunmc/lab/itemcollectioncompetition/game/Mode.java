package net.kunmc.lab.itemcollectioncompetition.game;

import java.util.Locale;

public enum Mode {
  TIME(),
  AMOUNT();

  public String modeName() {
    return this.name().toLowerCase(Locale.ROOT);
  }

}
