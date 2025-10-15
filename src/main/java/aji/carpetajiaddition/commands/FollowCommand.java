package aji.carpetajiaddition.commands;

import aji.carpetajiaddition.CarpetAjiAdditionMod;
import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.data.FollowCommandData;
import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation;
import aji.carpetajiaddition.translations.TranslationsKey;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
                                .then(argument("followItem", FollowItemStackArgumentType.itemStack(commandBuildContext))
                                        .executes(FollowCommand::remove)))
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
        try {
            Method readSettingsFromConfMethod = SettingsManager.class.getDeclaredMethod("readSettingsFromConf", Path.class);
            Method getFileMethod = SettingsManager.class.getDeclaredMethod("getFile");
            readSettingsFromConfMethod.setAccessible(true);
            getFileMethod.setAccessible(true);
            Object o = readSettingsFromConfMethod.invoke(CarpetServer.settingsManager, getFileMethod.invoke(CarpetServer.settingsManager));
            Method ruleMapMethod = o.getClass().getDeclaredMethod("ruleMap");
            ruleMapMethod.setAccessible(true);
            Map<String, String> ruleMap = (Map<String, String>) (ruleMapMethod.invoke(o));
            if (CarpetAjiAdditionSettings.glowingHopperMinecart || !(ruleMap.get("glowingHopperMinecart") == null || !ruleMap.get("glowingHopperMinecart").equals("true"))) {
                context.getSource().sendError(trText(TranslationsKey.CMD_FOLLOW + "add.conflict.0"));
                MutableText text = CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_FOLLOW + "add.conflict.1.here").copy().setStyle(
                        Style.EMPTY
                                .withColor(Formatting.AQUA)
                                .withHoverEvent(
                                        new HoverEvent(
                                                HoverEvent.Action.SHOW_TEXT,
                                                trText(
                                                        TranslationsKey.CMD_FOLLOW + "add.conflict.1.hoverEvent"
                                                )
                                        )
                                )
                                .withClickEvent(
                                        new ClickEvent(
                                                ClickEvent.Action.SUGGEST_COMMAND,
                                                "/carpet setDefault glowingHopperMinecart false"
                                        )
                                )
                );
                context.getSource().sendFeedback(() -> trText(TranslationsKey.CMD_FOLLOW + "add.conflict.1.text", text), false);
                return 0;
            }
        } catch (Exception e) {
            //我不可能出错
            throw new RuntimeException(e);
        }
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
        Item item = FollowItemStackArgumentType.getItemStackArgument(context, "followItem").getItem();
        boolean bl = data.removeFromFollowItems(item);
        if (bl){
            context.getSource().sendFeedback(() -> trText(TranslationsKey.CMD_FOLLOW + "remove.feedback", item.getDefaultStack().toHoverableText()), true);
            return 1;
        }else {
            context.getSource().sendError(trText(TranslationsKey.CMD_FOLLOW + "remove.error", item.getDefaultStack().toHoverableText().copy().setStyle(item.getDefaultStack().toHoverableText().getStyle().withColor(Formatting.RED))));
            return 0;
        }
    }

    public static class FollowItemStackArgumentType implements ArgumentType<ItemStackArgument> {
        private final ItemStringReader reader;

        private FollowItemStackArgumentType(CommandRegistryAccess commandRegistryAccess){
            this.reader = new ItemStringReader(commandRegistryAccess);
        }

        public static FollowItemStackArgumentType itemStack(CommandRegistryAccess commandRegistryAccess){
            return new FollowItemStackArgumentType(commandRegistryAccess);
        }

        public static <S> ItemStackArgument getItemStackArgument(CommandContext<S> context, String name) {
            return context.getArgument(name, ItemStackArgument.class);
        }

        @Override
        public ItemStackArgument parse(StringReader stringReader) throws CommandSyntaxException {
            ItemStringReader.ItemResult itemResult = this.reader.consume(stringReader);
            return new ItemStackArgument(itemResult.item(), itemResult.components());
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            String remaining = builder.getRemaining().toLowerCase();
            for (Item item : data.getFollowItems()) {
                String itemId = item.toString();
                if (itemId.contains(remaining)) {
                    builder.suggest(itemId);
                }
            }
            return builder.buildFuture();
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
