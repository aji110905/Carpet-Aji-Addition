package aji.carpetajiaddition.commands;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.data.FollowCommandData;
import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation;
import aji.carpetajiaddition.translations.TranslationsKey;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.scores.PlayerTeam;

import static aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation.trComponent;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FollowCommand {
    public static FollowCommandData data;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext){
        dispatcher.register(
                literal("follow")
                        .requires(commandSource -> CommandHelper.canUseCommand(commandSource, CarpetAjiAdditionSettings.commandFollow))
                        .then(literal("add")
                                .then(argument("item", ItemArgument.item(commandBuildContext))
                                        .executes(FollowCommand::add)))
                        .then(literal("remove")
                                .then(argument("followItem", StringArgumentType.greedyString())
                                        .suggests((CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                                            data.getFollowItems().forEach(item -> builder.suggest(BuiltInRegistries.ITEM.getKey(item).toString()));
                                            return builder.buildFuture();
                                        })
                                        .executes(FollowCommand::remove)))
                        .then(literal("list")
                                .executes(FollowCommand::list))
                        .then(literal("color")
                                .then(literal("set")
                                        .then(argument("color", ColorArgument.color())
                                                .executes(FollowCommand::setColor)))
                                .then(literal("show")
                                        .executes(FollowCommand::showColor)))
        );
    }

    private static int add(CommandContext<CommandSourceStack> context){
        Item item = ItemArgument.getItem(context, "item").getItem();
        boolean bl = data.addToFollowItems(item);
        Component displayName = item.getDefaultInstance().getDisplayName();
        if (bl){
            context.getSource().sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "add.feedback", displayName), true);
            return 1;
        }else {
            context.getSource().sendFailure(trComponent(TranslationsKey.CMD_FOLLOW + "add.error", displayName.copy().setStyle(displayName.getStyle().withColor(ChatFormatting.RED))));
            return 0;
        }
    }

    private static int remove(CommandContext<CommandSourceStack> context){
        ResourceLocation resourceLocation = ResourceLocation.parse(StringArgumentType.getString(context, "followItem"));
        if (BuiltInRegistries.ITEM.containsKey(resourceLocation)){
            //#if MC < 12102
            Item item = BuiltInRegistries.ITEM.get(resourceLocation);
            //#else
            //$$ Item item = BuiltInRegistries.ITEM.get(resourceLocation).get().value();
            //#endif
            boolean bl = data.removeFromFollowItems(item);
            Component displayName = item.getDefaultInstance().getDisplayName();
            if (bl){
                context.getSource().sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "remove.feedback", displayName), true);
                return 1;
            }else {
                context.getSource().sendFailure(trComponent(TranslationsKey.CMD_FOLLOW + "remove.error", displayName.copy().setStyle(displayName.getStyle().withColor(ChatFormatting.RED))));
                return 0;
            }
        }
        return 0;
    }

    private static int list(CommandContext<CommandSourceStack> context){
        if (data.getFollowItems().isEmpty()) {
            context.getSource().sendFailure(trComponent(TranslationsKey.CMD_FOLLOW + "list.error"));
            return 0;
        }else {
            context.getSource().sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "list.feedback"), false);
            for (Item item : data.getFollowItems()) {
                context.getSource().sendSuccess(() -> item.getDefaultInstance().getDisplayName(), false);
            }
            return 1;
        }
    }

    private static int setColor(CommandContext<CommandSourceStack> context){
        if (data.getColor().equals(ColorArgument.getColor(context, "color"))) {
            context.getSource().sendFailure(trComponent(TranslationsKey.CMD_FOLLOW + "color.set.error", CarpetAjiAdditionTranslation.trColor.trText(data.getColor(), false)));
            return 0;
        } else {
            data.setColor(ColorArgument.getColor(context, "color"));
            context.getSource().sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "color.set.feedback", CarpetAjiAdditionTranslation.trColor.trText(data.getColor(), true)), true);
            return 1;
        }
    }

    private static int showColor(CommandContext<CommandSourceStack> context){
        context.getSource().sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "color.show.feedback", CarpetAjiAdditionTranslation.trColor.trText(data.getColor(), true)), true);
        return 1;
    }

    public static void init(){
        PlayerTeam followItems = CarpetAjiAdditionSettings.minecraftServer.getScoreboard().addPlayerTeam("followItems");
        data = (FollowCommandData) CarpetAjiAdditionSettings.data.getData(FollowCommandData.DATA_NAME);
        followItems.setColor(data.getColor());
    }
}
