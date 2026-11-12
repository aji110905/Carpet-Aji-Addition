package top.ajitech.carpetajiaddition.setting;

import carpet.api.settings.RuleCategory;
import carpet.utils.CommandHelper;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class CommandRule extends EnumRule<CommandRuleValue> {

    public CommandRule(String name, CommandRuleValue defaultValue) {
        super(name, List.of(RuleCategory.COMMAND), defaultValue, false);
    }

    @Override
    protected Collection<CommandRuleValue> allowedValues() {
        return EnumSet.allOf(CommandRuleValue.class);
    }

    @Override
    protected void onValueChanged(CommandSourceStack source, CommandRuleValue value) {
        if (source != null) {
            CommandHelper.notifyPlayersCommandsChanged(source.getServer());
        }
    }

    @Override
    public Class<CommandRuleValue> type() {
        return CommandRuleValue.class;
    }

    public boolean canUseCommand(CommandSourceStack source) {
        return CommandHelper.canUseCommand(source, value());
    }
}
