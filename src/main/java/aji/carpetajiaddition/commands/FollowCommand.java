package aji.carpetajiaddition.commands;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.data.FollowCommandData;
import aji.carpetajiaddition.util.translations.TranslationUtil;
import aji.carpetajiaddition.util.translations.TranslationsKey;
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

import java.util.HashSet;

import static aji.carpetajiaddition.util.translations.TranslationUtil.trComponent;
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
        CommandSourceStack source = context.getSource();
        if (bl){
            source.sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "add.feedback", displayName), true);
            return 1;
        }else {
            source.sendFailure(trComponent(TranslationsKey.CMD_FOLLOW + "add.error", displayName.copy().setStyle(displayName.getStyle().withColor(ChatFormatting.RED))));
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
            CommandSourceStack source = context.getSource();
            if (bl){
                source.sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "remove.feedback", displayName), true);
                return 1;
            }else {
                source.sendFailure(trComponent(TranslationsKey.CMD_FOLLOW + "remove.error", displayName.copy().setStyle(displayName.getStyle().withColor(ChatFormatting.RED))));
                return 0;
            }
        }
        return 0;
    }

    private static int list(CommandContext<CommandSourceStack> context){
        CommandSourceStack source = context.getSource();
        if (data.getFollowItems().isEmpty()) {
            source.sendFailure(trComponent(TranslationsKey.CMD_FOLLOW + "list.error"));
            return 0;
        }else {
            HashSet<Component> set = new HashSet<>();
            for (Item item : data.getFollowItems()) {
                set.add(item.getDefaultInstance().getDisplayName());
            }
            source.sendSuccess(
                    () -> trComponent(TranslationsKey.CMD_FOLLOW + "list.feedback").copy().append(
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
        CommandSourceStack source = context.getSource();
        if (data.getColor().equals(ColorArgument.getColor(context, "color"))) {
            source.sendFailure(trComponent(TranslationsKey.CMD_FOLLOW + "color.set.error", TranslationUtil.trComponent(data.getColor(), false)));
            return 0;
        } else {
            data.setColor(ColorArgument.getColor(context, "color"));
            source.sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "color.set.feedback", TranslationUtil.trComponent(data.getColor(), true)), true);
            return 1;
        }
    }

    private static int showColor(CommandContext<CommandSourceStack> context){
        context.getSource().sendSuccess(() -> trComponent(TranslationsKey.CMD_FOLLOW + "color.show.feedback", TranslationUtil.trComponent(data.getColor(), true)), true);
        return 1;
    }

    public static void init(){
        PlayerTeam followItems = CarpetAjiAdditionSettings.minecraftServer.getScoreboard().addPlayerTeam("followItems");
        data = (FollowCommandData) CarpetAjiAdditionSettings.data.getData(FollowCommandData.DATA_NAME);
        followItems.setColor(data.getColor());
    }
}
