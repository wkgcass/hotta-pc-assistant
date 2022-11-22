package net.cassite.hottapcassistant.component.setting;

public enum SettingType {
    INT,
    FLOAT,
    BOOL,
    BOOL_0_1,
    RESOLUTION,
    ;

    public boolean check(String value) {
        switch (this) {
            case INT:
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            case FLOAT:
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            case BOOL:
                return value.equals("True") || value.equals("False");
            case BOOL_0_1:
                return value.equals("0") || value.equals("1");
            case RESOLUTION:
                if (!value.contains("x")) {
                    return false;
                }
                String[] split = value.split("x");
                if (split.length != 2) {
                    return false;
                }
                int x;
                int y;
                try {
                    x = Integer.parseInt(split[0]);
                    y = Integer.parseInt(split[1]);
                } catch (NumberFormatException ex) {
                    return false;
                }
                if (x == -1 && y == -1) {
                    return true;
                }
                return x > 0 && y > 0;
            default:
                return false;
        }
    }

    public Object parse(String value) {
        if (!check(value)) {
            throw new IllegalArgumentException(value);
        }
        return switch (this) {
            case INT -> Integer.parseInt(value);
            case FLOAT -> Double.parseDouble(value);
            case BOOL -> value.equals("True");
            case BOOL_0_1 -> value.equals("1");
            case RESOLUTION -> value;
        };
    }
}
