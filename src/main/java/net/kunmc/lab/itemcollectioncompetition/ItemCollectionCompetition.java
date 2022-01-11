package net.kunmc.lab.itemcollectioncompetition;

import dev.kotx.flylib.FlyLib;
import net.kunmc.lab.configlib.command.ConfigCommandBuilder;
import net.kunmc.lab.itemcollectioncompetition.command.Main;
import net.kunmc.lab.itemcollectioncompetition.config.Config;
import net.kunmc.lab.itemcollectioncompetition.game.BedLimiter;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemCollectionCompetition extends JavaPlugin {

  public static ItemCollectionCompetition plugin;
  public static Config config;

  @Override
  public void onEnable() {
    plugin = this;

    config = new Config(this);
    config.saveConfig();
    config.loadConfig();

    FlyLib.create(this, builder -> {
      builder.command(new Main("icc", new ConfigCommandBuilder(config).build()));
    });

    //event
    getServer().getPluginManager().registerEvents(new BedLimiter(), this);
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}
