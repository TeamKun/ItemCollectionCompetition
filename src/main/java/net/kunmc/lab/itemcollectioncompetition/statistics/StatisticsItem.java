package net.kunmc.lab.itemcollectioncompetition.statistics;

import org.bukkit.entity.Player;

public interface StatisticsItem {

  ItemType itemType();

  String header();

  int get(Player player);
}
