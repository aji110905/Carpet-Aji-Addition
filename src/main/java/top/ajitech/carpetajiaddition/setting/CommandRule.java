package top.ajitech.carpetajiaddition.setting;

import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.RuleCategory;
import carpet.utils.CommandHelper;
import net.minecraft.commands.CommandSourceStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommandRule extends Rule<CommandRuleValue>{

    public CommandRule(String name, CommandRuleValue defaultValue) {
        super(name, List.of(RuleCategory.COMMAND), defaultValue, false);
    }

    @Override
    protected CommandRuleValue parse(String value) throws InvalidRuleValueException {
        CommandRuleValue commandRuleValue = CommandRuleValue.fromString(value);
        if (commandRuleValue == null) {
            throw new InvalidRuleValueException();
        }
        return commandRuleValue;
    }

    @Override
    public Collection<String> suggestions() {
        return Arrays.stream(CommandRuleValue.values())
                .map(CommandRuleValue::toString)
                .toList();
    }

    @Override
    public Class<CommandRuleValue> type() {
        return CommandRuleValue.class;
    }

    public boolean canUseCommand(CommandSourceStack source) {
        return CommandHelper.canUseCommand(source, value());
    }
}
