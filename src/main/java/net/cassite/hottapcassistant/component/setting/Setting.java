package net.cassite.hottapcassistant.component.setting;

import net.cassite.hottapcassistant.util.Utils;

import java.util.function.Function;

public class Setting {
    public String name;
    public SettingType type;
    public Object value;

    public String source;
    public int lineIndex = -1;
    public Function<Object, Boolean> additionalCheck = null;

    @Override
    public String toString() {
        return name + "=" + formatValue();
    }

    public String formatValue() {
        return switch (type) {
            case INT -> Integer.toString((int) value);
            case FLOAT -> Utils.floatValueFormat.format((double) value);
            case BOOL -> Utils.boolToString((boolean) value);
            case BOOL_0_1 -> ((boolean) value) ? "1" : "0";
            case RESOLUTION -> (String) value;
        };
    }
}
