package aji.carpetajiaddition.commands;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation;
import aji.carpetajiaddition.translations.TranslationsKey;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(
               literal("mods")
                       .requires(commandSource -> CommandHelper.canUseCommand(commandSource, CarpetAjiAdditionSettings.commandModlist))
                       .then(
                               literal("list")
                                    .executes(ModsCommand::list)
                       )
                       .then(
                               argument("mods", StringArgumentType.greedyString())
                                       .suggests((CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) -> {
                                           FabricLoader.getInstance().getAllMods().forEach(mod -> builder.suggest(mod.getMetadata().getName()));
                                           return builder.buildFuture();
                                       })
                                       .executes(ModsCommand::mods)
                       )
        );
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
        Set<Text> set = new HashSet<>();
        for (ModContainer mod : mods) {
            String name = mod.getMetadata().getName();
            MutableText text = Text.literal(name);
            text.setStyle(
                    Style
                            .EMPTY
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "list.hover")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mods " + name))
            );
            set.add(text);
        }
        context.getSource().sendFeedback(
                () -> set
                        .stream()
                        .reduce((text1, text2) -> text1.copy().append("\n").append(text2))
                        .orElse(Text.empty()),
                false
        );
        return 1;
    }

    private static int mods(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String modName = StringArgumentType.getString(context, "mods");
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();
            if (metadata.getName().equals(modName)) {
                LinkedList<Text> list = new LinkedList<>();
                list.add(Text.literal("----------").append(Text.literal(metadata.getName()).setStyle(Style.EMPTY.withColor(Formatting.AQUA))).append("----------"));
                list.add(Text.literal(metadata.getDescription()));
                list.add(Text.literal("============================="));

                String unknown = CarpetAjiAdditionTranslation.tr(TranslationsKey.CMD_MODS + "mods.feedback.unknown");

                String type = metadata.getType();
                list.add(CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.type", type == null ? unknown : type));

                String id = metadata.getId();
                list.add(CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.id", id == null ? unknown : id));

                Version version = metadata.getVersion();
                list.add(CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.version", version == null ? unknown : version.getFriendlyString()));

                list.add(
                        switch (metadata.getEnvironment()) {
                            case CLIENT -> CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.environment.client");
                            case SERVER -> CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.environment.server");
                            case UNIVERSAL -> CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.environment.universal");
                            case null -> CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.environment.null");
                        }
                );

                list.add(CarpetAjiAdditionTranslation.trText(
                        TranslationsKey.CMD_MODS + "mods.feedback.author",
                        metadata.getAuthors() != null && !metadata.getAuthors().isEmpty()
                                ? metadata.getAuthors().stream()
                                .map(Person::getName)
                                .collect(Collectors.joining(", "))
                                : unknown
                ));

                list.add(CarpetAjiAdditionTranslation.trText(
                        TranslationsKey.CMD_MODS + "mods.feedback.contributors",
                        metadata.getContributors() != null && !metadata.getContributors().isEmpty()
                                ? metadata.getContributors().stream()
                                .map(Person::getName)
                                .collect(Collectors.joining(", "))
                                : unknown
                        ));

                ContactInformation contact = metadata.getContact();
                if (contact == null || contact.asMap().isEmpty()){
                    list.add(CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.contact.root", unknown));
                } else {
                    Map<String, String> map = contact.asMap();
                    Set<Text> texts = new HashSet<>();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String key = entry.getKey();
                        MutableText text = Text.literal(
                                switch (key) {
                                    case "homepage" -> CarpetAjiAdditionTranslation.tr(TranslationsKey.CMD_MODS + "mods.feedback.contact.homepage");
                                    case "sources" -> CarpetAjiAdditionTranslation.tr(TranslationsKey.CMD_MODS + "mods.feedback.contact.sources");
                                    case "issues" -> CarpetAjiAdditionTranslation.tr(TranslationsKey.CMD_MODS + "mods.feedback.contact.issues");
                                    default -> key;
                                }
                        );
                        text.setStyle(
                                Style
                                        .EMPTY
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.feedback.contact.hover")))
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, entry.getValue()))
                        );
                        texts.add(text);
                    }
                    list.add(CarpetAjiAdditionTranslation.trText(
                            TranslationsKey.CMD_MODS + "mods.feedback.contact.root",
                            texts
                            .stream()
                            .reduce((text1, text2) -> text1.copy().append(", ").append(text2))
                            .orElse(Text.empty())
                    ));
                }

                list.add(CarpetAjiAdditionTranslation.trText(
                        TranslationsKey.CMD_MODS + "mods.feedback.license",
                        metadata.getLicense() != null && !metadata.getLicense().isEmpty()
                                ? metadata.getLicense().stream()
                                .collect(Collectors.joining(", "))
                                : unknown
                        )
                );

                source.sendFeedback(
                        () -> list
                        .stream()
                        .reduce((text1, text2) -> text1.copy().append("\n").append(text2))
                        .orElse(Text.empty()),
                        false
                );
                return 1;
            }
        }
        source.sendError(CarpetAjiAdditionTranslation.trText(TranslationsKey.CMD_MODS + "mods.error", modName));
        return 0;
    }
}
