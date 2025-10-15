package aji.carpetajiaddition.commands;

import aji.carpetajiaddition.CarpetAjiAdditionMod;
import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.data.FollowCommandData;
import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation;
import aji.carpetajiaddition.translations.TranslationsKey;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import static aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation.trText;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FollowCommand {
    public static FollowCommandData data;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext){
        dispatcher.register(
                literal("follow")
                        .requires(commandSource -> CommandHelper.canUseCommand(commandSource, CarpetAjiAdditionSettings.commandFollow))
                        .then(literal("add")
                                .then(argument("item", ItemStackArgumentType.itemStack(commandBuildContext))
                                        .executes(FollowCommand::add)))
                        .then(literal("remove")
                                .then(argument("followItem", ItemStackArgumentType.itemStack(commandBuildContext)))
                                        .executes(FollowCommand::remove))
                        .then(literal("list")
                                .executes(FollowCommand::list))
                        .then(literal("color")
                                .then(literal("set")
                                        .then(argument("color", ColorArgumentType.color())
                                                .executes(FollowCommand::setColor)))
                                .then(literal("show")
                                        .executes(FollowCommand::showColor)))
        );
    }

    private static int add(CommandContext<ServerCommandSource> context){
        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        boolean bl = data.addToFollowItems(item);
        if (bl){
            context.getSource().sendFeedback(() -> trText(TranslationsKey.CMD_FOLLOW + "add.feedback", item.getDefaultStack().toHoverableText()), true);
            return 1;
        }else {
            context.getSource().sendError(trText(TranslationsKey.CMD_FOLLOW + "add.error", item.getDefaultStack().toHoverableText().copy().setStyle(item.getDefaultStack().toHoverableText().getStyle().withColor(Formatting.RED))));
            return 0;
        }
    }

    private static int remove(CommandContext<ServerCommandSource> context){
        Item item = ItemStackArgumentType.getItemStackArgument(context, "followItem").getItem();
        boolean bl = data.removeFromFollowItems(item);
        if (bl){
            context.getSource().sendFeedback(() -> trText(TranslationsKey.CMD_FOLLOW + "remove.feedback", item.getDefaultStack().toHoverableText()), true);
            return 1;
        }else {
            context.getSource().sendError(trText(TranslationsKey.CMD_FOLLOW + "remove.error", item.getDefaultStack().toHoverableText().copy().setStyle(item.getDefaultStack().toHoverableText().getStyle().withColor(Formatting.RED))));
            return 0;
        }
    }

    private static int list(CommandContext<ServerCommandSource> context){
        if (data.getFollowItems().isEmpty()) {
            context.getSource().sendError(trText(TranslationsKey.CMD_FOLLOW + "list.error"));
            return 0;
        }else {
            context.getSource().sendFeedback(() -> trText(TranslationsKey.CMD_FOLLOW + "list.feedback"), false);
            for (Item item : data.getFollowItems()) {
                context.getSource().sendFeedback(() -> item.getDefaultStack().toHoverableText(), false);
            }
            return 1;
        }
    }

    private static int setColor(CommandContext<ServerCommandSource> context){
        if (data.getColor().equals(ColorArgumentType.getColor(context, "color"))) {
            context.getSource().sendError(trText(TranslationsKey.CMD_FOLLOW + "color.set.error", CarpetAjiAdditionTranslation.trColor.trText(data.getColor(), false)));
            return 0;
        } else {
            data.setColor(ColorArgumentType.getColor(context, "color"));
            context.getSource().sendFeedback(() -> trText(TranslationsKey.CMD_FOLLOW + "color.set.feedback", CarpetAjiAdditionTranslation.trColor.trText(data.getColor(), true)), true);
            return 1;
        }
    }

    private static int showColor(CommandContext<ServerCommandSource> context){
        context.getSource().sendFeedback(() -> trText(TranslationsKey.CMD_FOLLOW + "color.show.feedback", CarpetAjiAdditionTranslation.trColor.trText(data.getColor(), true)), true);
        return 1;
    }

    public static void init(){
        Team followItems = CarpetAjiAdditionMod.minecraftServer.getScoreboard().addTeam("followItems");
        data = (FollowCommandData) CarpetAjiAdditionMod.data.getData(FollowCommandData.DATA_NAME);
        followItems.setColor(data.getColor());
    }
}
