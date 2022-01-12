package net.kunmc.lab.itemcollectioncompetition.statistics;

public enum ItemType {
  KILL("キル数"),
  CRAFT("クラフト"),
  BLOCK_BREAK("採掘"),
  ROBBERY("強奪");

  public String header;

  ItemType(String header) {
    this.header = header;
  }
}
