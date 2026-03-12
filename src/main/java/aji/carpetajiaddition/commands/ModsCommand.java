package aji.carpetajiaddition.commands;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.util.translations.TranslationUtil;
import aji.carpetajiaddition.util.translations.TranslationsKey;
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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;

//#if MC >= 12105
//$$ import java.net.URI;
//$$ import java.net.URISyntaxException;
//#endif
import java.util.*;
import java.util.stream.Collectors;

import static aji.carpetajiaddition.util.translations.TranslationUtil.tr;
import static aji.carpetajiaddition.util.translations.TranslationUtil.trComponent;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ModsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext) {
        dispatcher.register(
               literal("mods")
                       .requires(commandSource -> CommandHelper.canUseCommand(commandSource, CarpetAjiAdditionSettings.commandMods))
                       .then(
                               literal("list")
                                    .executes(ModsCommand::list)
                       )
                       .then(
                               argument("mods", StringArgumentType.greedyString())
                                       .suggests((CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                                           FabricLoader.getInstance().getAllMods().forEach(mod -> builder.suggest(mod.getMetadata().getName()));
                                           return builder.buildFuture();
                                       })
                                       .executes(ModsCommand::mods)
                       )
        );
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
        Set<Component> set = new HashSet<>();
        for (ModContainer mod : mods) {
            String name = mod.getMetadata().getName();
            MutableComponent component = Component.literal(name);
            component.setStyle(
                    Style
                            .EMPTY
                            //#if MC < 12105
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, trComponent(TranslationsKey.CMD_MODS + "list.hover")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mods " + name))
                            //#else
                            //$$ .withHoverEvent(new HoverEvent.ShowText(trComponent(TranslationsKey.CMD_MODS + "list.hover")))
                            //$$ .withClickEvent(new ClickEvent.RunCommand("/mods " + name))
                            //#endif
            );
            set.add(component);
        }
        context.getSource().sendSuccess(
                () -> set
                        .stream()
                        .reduce((text1, text2) -> text1.copy().append("\n").append(text2))
                        .orElse(Component.empty()),
                false
        );
        return 1;
    }

    private static int mods(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String modName = StringArgumentType.getString(context, "mods");
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();
            if (metadata.getName().equals(modName)) {
                LinkedList<Component> list = new LinkedList<>();
                list.add(Component.literal("----------").append(Component.literal(metadata.getName()).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))).append("----------"));
                list.add(Component.literal(metadata.getDescription()));
                list.add(Component.literal("============================="));

                String unknown = tr(TranslationsKey.CMD_MODS + "mods.feedback.unknown");

                String type = metadata.getType();
                list.add(trComponent(TranslationsKey.CMD_MODS + "mods.feedback.type", type == null ? unknown : type));

                String id = metadata.getId();
                list.add(trComponent(TranslationsKey.CMD_MODS + "mods.feedback.id", id == null ? unknown : id));

                Version version = metadata.getVersion();
                list.add(trComponent(TranslationsKey.CMD_MODS + "mods.feedback.version", version == null ? unknown : version.getFriendlyString()));

                list.add(
                        switch (metadata.getEnvironment()) {
                            case CLIENT -> trComponent(TranslationsKey.CMD_MODS + "mods.feedback.environment.client");
                            case SERVER -> trComponent(TranslationsKey.CMD_MODS + "mods.feedback.environment.server");
                            case UNIVERSAL -> trComponent(TranslationsKey.CMD_MODS + "mods.feedback.environment.universal");
                            case null -> trComponent(TranslationsKey.CMD_MODS + "mods.feedback.environment.null");
                        }
                );

                list.add(trComponent(
                        TranslationsKey.CMD_MODS + "mods.feedback.author",
                        metadata.getAuthors() != null && !metadata.getAuthors().isEmpty()
                                ? metadata.getAuthors().stream()
                                .map(Person::getName)
                                .collect(Collectors.joining(", "))
                                : unknown
                ));

                list.add(trComponent(
                        TranslationsKey.CMD_MODS + "mods.feedback.contributors",
                        metadata.getContributors() != null && !metadata.getContributors().isEmpty()
                                ? metadata.getContributors().stream()
                                .map(Person::getName)
                                .collect(Collectors.joining(", "))
                                : unknown
                        ));

                ContactInformation contact = metadata.getContact();
                if (contact == null || contact.asMap().isEmpty()){
                    list.add(trComponent(TranslationsKey.CMD_MODS + "mods.feedback.contact.root", unknown));
                } else {
                    Map<String, String> map = contact.asMap();
                    Set<Component> texts = new HashSet<>();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String key = entry.getKey();
                        MutableComponent text = Component.literal(
                                switch (key) {
                                    case "homepage" -> tr(TranslationsKey.CMD_MODS + "mods.feedback.contact.homepage");
                                    case "sources" -> tr(TranslationsKey.CMD_MODS + "mods.feedback.contact.sources");
                                    case "issues" -> tr(TranslationsKey.CMD_MODS + "mods.feedback.contact.issues");
                                    default -> key;
                                }
                        );
                        //#if MC < 12105
                        text.setStyle(
                                Style
                                        .EMPTY
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, trComponent(TranslationsKey.CMD_MODS + "mods.feedback.contact.hover")))
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, entry.getValue()))
                        );
                        //#else
                        //$$ Style style = Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(trComponent(TranslationsKey.CMD_MODS + "mods.feedback.contact.hover")));
                        //$$ URI uri;
                        //$$ try {
                        //$$     uri = new URI(entry.getValue());
                        //$$ } catch (URISyntaxException e) {
                        //$$     uri = null;
                        //$$ }
                        //$$ if (uri == null){
                        //$$     text.setStyle(style);
                        //$$ } else {
                        //$$     text.setStyle(style.withClickEvent(new ClickEvent.OpenUrl(uri)));
                        //$$ }
                        //#endif
                        texts.add(text);
                    }
                    list.add(trComponent(
                            TranslationsKey.CMD_MODS + "mods.feedback.contact.root",
                            texts
                            .stream()
                            .reduce((text1, text2) -> text1.copy().append(", ").append(text2))
                            .orElse(Component.empty())
                    ));
                }

                list.add(trComponent(
                        TranslationsKey.CMD_MODS + "mods.feedback.license",
                        metadata.getLicense() != null && !metadata.getLicense().isEmpty()
                                ? metadata.getLicense().stream()
                                .collect(Collectors.joining(", "))
                                : unknown
                        )
                );

                source.sendSuccess(
                        () -> list
                        .stream()
                        .reduce((text1, text2) -> text1.copy().append("\n").append(text2))
                        .orElse(Component.empty()),
                        false
                );
                return 1;
            }
        }
        source.sendFailure(trComponent(TranslationsKey.CMD_MODS + "mods.error", modName));
        return 0;
    }
}
