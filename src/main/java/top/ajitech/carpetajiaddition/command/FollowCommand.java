package top.ajitech.carpetajiaddition.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import top.ajitech.carpetajiaddition.data.FollowCommandData;
import top.ajitech.carpetajiaddition.util.ResourceLocationUtil;
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

import java.util.HashSet;

import static top.ajitech.carpetajiaddition.translate.TranslationsKey.CMD_FOLLOW;
import static top.ajitech.carpetajiaddition.util.TranslateUtil.trc;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FollowCommand extends Command{
    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> getCommand(CommandBuildContext buildContext) {
        return literal("follow")
                .requires(CarpetAjiAdditionRules.COMMAND_FOLLOW::canUseCommand)
                .then(literal("add")
                        .then(argument("item", ItemArgument.item(buildContext))
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
                                .then(argument("color", ColorArgument.color())
                                        .executes(FollowCommand::setColor)))
                        .then(literal("show")
                                .executes(FollowCommand::showColor)));
    }

    private static int add(CommandContext<CommandSourceStack> context){
        //#if MC < 260100
        Item item = ItemArgument.getItem(context, "item").getItem();
        //#else
        //$$ Item item = ItemArgument.getItem(context, "item").item().value();
        //#endif
        Component displayName = item.getDefaultInstance().getDisplayName();
        CommandSourceStack source = context.getSource();
        if (FollowCommandData.getInstance().addToFollowItems(item)){
            source.sendSuccess(() -> trc(CMD_FOLLOW + "add.feedback", displayName), true);
            return 1;
        }else {
            source.sendFailure(trc(CMD_FOLLOW + "add.error", displayName.copy().setStyle(displayName.getStyle().withColor(ChatFormatting.RED))));
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
            Component displayName = item.getDefaultInstance().getDisplayName();
            CommandSourceStack source = context.getSource();
            if (FollowCommandData.getInstance().removeFromFollowItems(item)){
                source.sendSuccess(() -> trc(CMD_FOLLOW + "remove.feedback", displayName), true);
                return 1;
            }else {
                source.sendFailure(trc(CMD_FOLLOW + "remove.error", displayName.copy().setStyle(displayName.getStyle().withColor(ChatFormatting.RED))));
                return 0;
            }
        }
        return 0;
    }

    private static int list(CommandContext<CommandSourceStack> context){
        FollowCommandData data = FollowCommandData.getInstance();
        CommandSourceStack source = context.getSource();
        if (data.getFollowItems().isEmpty()) {
            source.sendFailure(trc(CMD_FOLLOW + "list.error"));
            return 0;
        }else {
            HashSet<Component> set = new HashSet<>();
            for (Item item : data.getFollowItems()) {
                set.add(item.getDefaultInstance().getDisplayName());
            }
            source.sendSuccess(
                    () -> trc(CMD_FOLLOW + "list.feedback").copy().append(
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
        ChatFormatting color = ColorArgument.getColor(context, "color");
        if (data.getColor().equals(color)) {
            source.sendFailure(trc(CMD_FOLLOW + "color.set.error", trc(data.getColor(), false)));
            return 0;
        } else {
            data.setColor(source.getServer(), color);
            source.sendSuccess(() -> trc(CMD_FOLLOW + "color.set.feedback", trc(data.getColor(), true)), true);
            return 1;
        }
    }

    private static int showColor(CommandContext<CommandSourceStack> context){
        context.getSource().sendSuccess(() -> trc(CMD_FOLLOW + "color.show.feedback", trc(FollowCommandData.getInstance().getColor(), true)), true);
        return 1;
    }
}
