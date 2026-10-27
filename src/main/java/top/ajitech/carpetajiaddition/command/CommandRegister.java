package top.ajitech.carpetajiaddition.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.HashSet;
import java.util.Set;

public class CommandRegister {
    public static final CommandRegister INSTANCE = new CommandRegister();

    private final Set<Command> commands = new HashSet<>();

    private CommandRegister() {
        commands.add(new FollowCommand());
        commands.add(new ModsCommand());
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        commands.forEach(command -> command.register(dispatcher, context));
    }
}
