package aji.carpetajiaddition.command;

import aji.carpetajiaddition.CarpetAjiAdditionRules;
import aji.carpetajiaddition.data.FollowCommandData;
import aji.carpetajiaddition.util.ResourceLocationUtil;
import aji.carpetajiaddition.constant.TranslationsKey;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TeamColorArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.item.Item;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;

import java.util.HashSet;
import java.util.Optional;

import static aji.carpetajiaddition.util.TranslateUtil.trc;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FollowCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext){
        dispatcher.register(
                literal("follow")
                        .requires(commandSource -> CommandHelper.canUseCommand(commandSource, CarpetAjiAdditionRules.commandFollow))
                        .then(literal("add")
                                .then(argument("item", ItemArgument.item(commandBuildContext))
                                        .executes(FollowCommand::add)))
                        .then(literal("remove")
                                .then(argument("followItem", StringArgumentType.greedyString())
                                        .suggests((CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                                            FollowCommandData.getInstance().getFollowItems().forEach(item -> builder.suggest(ResourceLocationUtil.getItemRegistryName(item)));
                                            return builder.buildFuture();
                                        })
                                        .executes(FollowCommand::remove)))
                        .then(literal("list")
                                .executes(FollowCommand::list))
                        .then(literal("color")
                                .then(literal("set")
                                        .then(argument("color", TeamColorArgument.teamColor())
                                                .executes(FollowCommand::setColor)))
                                .then(literal("show")
                                        .executes(FollowCommand::showColor)))
        );
    }

    private static int add(CommandContext<CommandSourceStack> context){
        Item item = ItemArgument.getItem(context, "item").item().value();
        Component displayName = item.getDefaultInstance().getDisplayName();
        CommandSourceStack source = context.getSource();
        if (FollowCommandData.getInstance().addToFollowItems(item)){
            source.sendSuccess(() -> trc(TranslationsKey.CMD_FOLLOW + "add.feedback", displayName), true);
            return 1;
        }else {
            source.sendFailure(trc(TranslationsKey.CMD_FOLLOW + "add.error", displayName.copy().setStyle(displayName.getStyle().withColor(ChatFormatting.RED))));
            return 0;
        }
    }

    private static int remove(CommandContext<CommandSourceStack> context){
        Identifier resourceLocation = Identifier.parse(StringArgumentType.getString(context, "followItem"));
        if (BuiltInRegistries.ITEM.containsKey(resourceLocation)){
            Item item = BuiltInRegistries.ITEM.get(resourceLocation).get().value();
            Component displayName = item.getDefaultInstance().getDisplayName();
            CommandSourceStack source = context.getSource();
            if (FollowCommandData.getInstance().removeFromFollowItems(item)){
                source.sendSuccess(() -> trc(TranslationsKey.CMD_FOLLOW + "remove.feedback", displayName), true);
                return 1;
            }else {
                source.sendFailure(trc(TranslationsKey.CMD_FOLLOW + "remove.error", displayName.copy().setStyle(displayName.getStyle().withColor(ChatFormatting.RED))));
                return 0;
            }
        }
        return 0;
    }

    private static int list(CommandContext<CommandSourceStack> context){
        FollowCommandData data = FollowCommandData.getInstance();
        CommandSourceStack source = context.getSource();
        if (data.getFollowItems().isEmpty()) {
            source.sendFailure(trc(TranslationsKey.CMD_FOLLOW + "list.error"));
            return 0;
        }else {
            HashSet<Component> set = new HashSet<>();
            for (Item item : data.getFollowItems()) {
                set.add(item.getDefaultInstance().getDisplayName());
            }
            source.sendSuccess(
                    () -> trc(TranslationsKey.CMD_FOLLOW + "list.feedback").copy().append(
                            set
                                    .stream()
                                    .reduce((text1, text2) -> text1.copy().append(", ").append(text2))
                                    .orElse(Component.empty())
                    ),
                    false
            );
            return 1;
        }
    }

    private static int setColor(CommandContext<CommandSourceStack> context){
        FollowCommandData data = FollowCommandData.getInstance();
        CommandSourceStack source = context.getSource();
        TeamColor color = TeamColorArgument.getTeamColor(context, "color");
        if (data.getColor().equals(color)) {
            source.sendFailure(trc(TranslationsKey.CMD_FOLLOW + "color.set.error", trc(data.getColor(), false)));
            return 0;
        } else {
            data.setColor(source.getServer(), color);
            source.sendSuccess(() -> trc(TranslationsKey.CMD_FOLLOW + "color.set.feedback", trc(data.getColor(), true)), true);
            return 1;
        }
    }

    private static int showColor(CommandContext<CommandSourceStack> context){
        context.getSource().sendSuccess(() -> trc(TranslationsKey.CMD_FOLLOW + "color.show.feedback", trc(FollowCommandData.getInstance().getColor(), true)), true);
        return 1;
    }

    public static void init(MinecraftServer server){
        ServerScoreboard scoreboard = server.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam("followItems");
        if (team == null){
            team = scoreboard.addPlayerTeam("followItems");
        }
        team.setColor(Optional.of(FollowCommandData.getInstance().getColor()));
    }
}