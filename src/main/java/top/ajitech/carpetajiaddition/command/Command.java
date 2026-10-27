package top.ajitech.carpetajiaddition.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public abstract class Command {
    protected abstract LiteralArgumentBuilder<CommandSourceStack> getCommand(CommandBuildContext buildContext);

    void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(getCommand(buildContext));
    }
}
