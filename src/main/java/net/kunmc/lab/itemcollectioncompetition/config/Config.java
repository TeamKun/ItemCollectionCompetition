package net.kunmc.lab.itemcollectioncompetition.config;

import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.value.BooleanValue;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.configlib.value.MaterialValue;
import net.kunmc.lab.configlib.value.TeamSetValue;
import net.kunmc.lab.itemcollectioncompetition.config.DisplayType.DisplayTypeEnum;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config extends BaseConfig {

  public MaterialValue targetItem = new MaterialValue(Material.IRON_BLOCK);
  public IntegerValue targetAmount = new IntegerValue(1728);
  public TeamSetValue teams = new TeamSetValue();
  public DisplayType displayType = new DisplayType(DisplayTypeEnum.CURRENT.value());
  public IntegerValue safetyAreaHalfRange = new IntegerValue(8);
  public IntegerValue safetyAreaUnderRange = new IntegerValue(5);
  public BooleanValue enableSafetyArea = new BooleanValue(true);

  public Config(@NotNull Plugin plugin) {
    super(plugin);
  }
}
