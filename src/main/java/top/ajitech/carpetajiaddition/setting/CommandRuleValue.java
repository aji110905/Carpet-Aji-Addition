package top.ajitech.carpetajiaddition.setting;

public enum CommandRuleValue {
    TRUE("true"),
    FALSE("false"),
    OPS("ops"),
    P0("0"),
    P1("1"),
    P2("2"),
    P3("3"),
    P4("4");

    private final String string;

    CommandRuleValue(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public static CommandRuleValue fromString(String string) {
        for (CommandRuleValue value : CommandRuleValue.values()) {
            if (value.string.equalsIgnoreCase(string)) {
                return value;
            }
        }
        return null;
    }
}
