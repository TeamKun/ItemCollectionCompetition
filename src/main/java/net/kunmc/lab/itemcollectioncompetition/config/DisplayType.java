package net.kunmc.lab.itemcollectioncompetition.config;

import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.value.StringValue;

public class DisplayType extends StringValue {

  public DisplayType(String value) {
    super(value);
  }

  public DisplayType(String value, int min, int max) {
    super(value, min, max);
  }
  
  public DisplayTypeEnum enumValue() {
    return DisplayTypeEnum.get(this.value());
  }

  @Override
  public void appendArgument(UsageBuilder builder) {
    builder.stringArgument("type", suggestionBuilder -> {
      for (DisplayTypeEnum displayTypeEnum : DisplayTypeEnum.values()) {
        suggestionBuilder.suggest(displayTypeEnum.value());
      }
    }, null);
  }

  public enum DisplayTypeEnum {
    CURRENT("現在"),
    REMAINING("残り");

    public String jName;

    DisplayTypeEnum(String jName) {
      this.jName = jName;
    }

    public String value() {
      return this.name().toLowerCase();
    }

    public static DisplayTypeEnum get(String value) {
      for (DisplayTypeEnum displayTypeEnum : DisplayTypeEnum.values()) {
        if (displayTypeEnum.value().equalsIgnoreCase(value)) {
          return displayTypeEnum;
        }
      }

      return CURRENT;
    }
  }
}

