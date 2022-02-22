package net.kunmc.lab.itemcollectioncompetition.game.statistics;

public enum ItemType {
  KILL("キル"),
  DEATH("デス"),
  CRAFT("クラフト"),
  BLOCK_BREAK("採掘"),
  ROBBERY("強奪した"),
  ROBBED("強奪された");


  public String header;

  ItemType(String header) {
    this.header = header;
  }
}
