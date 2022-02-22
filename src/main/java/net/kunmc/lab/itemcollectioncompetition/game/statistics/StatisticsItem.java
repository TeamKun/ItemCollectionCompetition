package net.kunmc.lab.itemcollectioncompetition.game.statistics;

import org.bukkit.entity.Player;

public interface StatisticsItem {

  ItemType itemType();

  String header();

  int get(Player player);
}
