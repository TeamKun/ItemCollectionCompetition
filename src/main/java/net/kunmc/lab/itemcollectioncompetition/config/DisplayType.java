package net.kunmc.lab.itemcollectioncompetition.config;

import dev.kotx.flylib.command.UsageBuilder;
import java.util.function.Consumer;
import net.kunmc.lab.configlib.value.StringValue;

public class DisplayType extends StringValue {

  public DisplayType(String value) {
    super(value);
  }

  public DisplayType(String value, Consumer<String> onSet) {
    super(value, onSet);
  }

  public DisplayType(String value, int min, int max) {
    super(value, min, max);
  }

  public DisplayType(String value, int min, int max, Consumer<String> onSet) {
    super(value, min, max, onSet);
  }

  @Override
  public void appendArgument(UsageBuilder builder) {
    builder.textArgument("type", suggestionBuilder -> {
      for (DisplayTypeEnum displayTypeEnum : DisplayTypeEnum.values()) {
        suggestionBuilder.suggest(displayTypeEnum.value());
      }
    });
  }

  public enum DisplayTypeEnum {
    CURRENT,
    REMAINING;

    public String value() {
      return this.name().toLowerCase();
    }
  }
}

