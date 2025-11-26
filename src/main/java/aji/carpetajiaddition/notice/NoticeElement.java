package aji.carpetajiaddition.notice;

import net.minecraft.world.World;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;

public class NoticeElement {
    private static final Set<NoticeElementInfo> NOTICE_ELEMENT_INFOS = new HashSet<>();

    private final Set<Parameter> parameters;
    private final BiFunction<NoticeContext, NoticeElement, String> parser;

    private NoticeElement(String[] parameters, NoticeElementInfo NoticeElementInfo){
        Set<Parameter> inputParameters = new HashSet<>();
        for (String parameter : parameters) {
            String[] split = parameter.split("=");
            for (ParameterInfo parameterInfo : NoticeElementInfo.parameters()) {
                if (split[0].equals(parameterInfo.name())) {
                    LinkedList<StringBuilder> valueTemp = new LinkedList<>();
                    StringBuilder stringTemp = new StringBuilder();
                    StringBuilder elementTemp = new StringBuilder();
                    boolean bl = true;
                    char[] valueCharArray = split[1].toCharArray();
                    for (int i = 0; i < valueCharArray.length; i++) {
                        if (bl){
                            if (valueCharArray[i] == '('){
                                bl = false;
                                if (!stringTemp.isEmpty()){
                                    valueTemp.add(stringTemp);
                                    stringTemp = new StringBuilder();
                                }
                                elementTemp.append('(');
                            }else if(valueCharArray[i] == ')'){
                                throw new IllegalArgumentException("Unexpected ')' at position " + i);
                            }else {
                                stringTemp.append(valueCharArray[i]);
                            }
                        }else {
                            if (valueCharArray[i] == ')'){
                                bl = true;
                                elementTemp.append(')');
                                valueTemp.add(elementTemp);
                                elementTemp = new StringBuilder();
                            }else {
                                elementTemp.append(valueCharArray[i]);
                            }
                        }
                    }

                    if (!stringTemp.isEmpty()) {
                        valueTemp.add(stringTemp);
                    }

                    if (!bl) {
                        throw new IllegalArgumentException("Unclosed '(' at end of string");
                    }

                    StringBuilder value = new StringBuilder();
                    for (StringBuilder stringBuilder : valueTemp) {
                        String string = stringBuilder.toString();
                        switch (string){
                            case "(left_brace)" -> value.append("(");
                            case "(right_brace)" -> value.append(")");
                            case "(space)" -> value.append(" ");
                            case "(equal_sign)" -> value.append("=");
                            default -> value.append(string);
                        }
                    }

                    inputParameters.add(new Parameter(parameterInfo.name(), value.toString()));
                    break;
                }
            }
        }

        NoticeElementInfo.parameters().stream()
                .filter(ParameterInfo::must)
                .forEach(paramInfo -> {
                    if (inputParameters.stream().noneMatch(p -> p.name().equals(paramInfo.name()))) {
                        throw new IllegalArgumentException("Must parameter " + paramInfo.name + " is not provided");
                    }
                });

        NoticeElementInfo.parameters().stream()
                .filter(paramInfo -> !paramInfo.must())
                .filter(paramInfo -> inputParameters.stream().noneMatch(p -> p.name().equals(paramInfo.name())))
                .forEach(paramInfo -> inputParameters.add(new Parameter(paramInfo.name(), paramInfo.defaultValue())));


        NoticeElementInfo.parameters().stream()
                .filter(parameterInfo -> inputParameters.stream()
                        .anyMatch(parameter -> parameter.name().equals(parameterInfo.name()) &&
                                !parameterInfo.parameterValidator.isValidValue(parameter.value)))
                .findFirst()
                .ifPresent(parameterInfo -> {
                    Parameter parameter = inputParameters.stream()
                            .filter(p -> p.name().equals(parameterInfo.name()))
                            .findFirst()
                            .orElseThrow();
                    throw new IllegalArgumentException(parameterInfo.parameterValidator.errorMessage(parameterInfo, parameter.value));
                });

        this.parameters = inputParameters;
        this.parser = NoticeElementInfo.parser;
    }

    public String parse(NoticeContext context) {
        return parser.apply(context, this);
    }

    public static NoticeElement of(String string){
        if (!(string.startsWith("{") && string.endsWith("}"))) {
            return new NoticeElement(new String[0], new NoticeElementInfo("", Set.of(), (context, noticeElement) -> string));
        }
        String[] split = string.substring(1, string.length() - 1).split(" ");
        for (NoticeElementInfo NoticeElementInfo : NOTICE_ELEMENT_INFOS) {
            if (NoticeElementInfo.name().equals(split[0])) {
                String[] parameters = new String[split.length - 1];
                System.arraycopy(split, 1, parameters, 0, parameters.length);
                return new NoticeElement(parameters, NoticeElementInfo);
            }
        }
        throw new IllegalArgumentException("Unknown notice element: " + string);
    }

    public static void registerNoticeElements(){
        registerNoticeElement(
                new NoticeElement.NoticeElementInfo(
                        "online_users",
                        Set.of(
                                new NoticeElement.ParameterInfo("include", false, "true", string -> (string.equals("true") || string.equals("false")))
                        ),
                        (context, noticeElement) -> {
                            NoticeElement.Parameter include = getParameter("include", noticeElement.parameters);
                            int size = context.server().getPlayerManager().getPlayerList().size();
                            if (include.value().equals("true")) return Integer.toString(size);
                            else return Integer.toString(size - 1);
                        }
                )
        );
        registerNoticeElement(
                new NoticeElement.NoticeElementInfo(
                        "now_time",
                        Set.of(
                                new NoticeElement.ParameterInfo("format", false, "YYYY-MM-dd HH:mm:ss", string -> !isThrowException(() -> DateTimeFormatter.ofPattern(string))),
                                new NoticeElement.ParameterInfo("zone", false, ZoneId.systemDefault().getId(), string -> !isThrowException(() -> ZoneId.of(string)))
                        ),
                        (context, noticeElement) -> {
                            NoticeElement.Parameter format = getParameter("format", noticeElement.parameters);
                            NoticeElement.Parameter zone = getParameter("zone", noticeElement.parameters);
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format.value());
                            ZoneId zoneId = ZoneId.of(zone.value());
                            return LocalDateTime.now(zoneId).format(dateTimeFormatter);
                        }
                )
        );
        registerNoticeElement(
                new NoticeElement.NoticeElementInfo(
                        "day_to_today",
                        Set.of(
                                new NoticeElement.ParameterInfo("day", true, null, string -> !isThrowException(() -> LocalDate.parse(string))),
                                new NoticeElement.ParameterInfo("zone", false, ZoneId.systemDefault().getId(), string -> !isThrowException(() -> ZoneId.of(string)))
                        ),
                        (context, noticeElement) -> {
                            NoticeElement.Parameter day = getParameter("day", noticeElement.parameters);
                            NoticeElement.Parameter zone = getParameter("zone", noticeElement.parameters);
                            ZoneId zoneId = ZoneId.of(zone.value());
                            LocalDate now = LocalDate.now(zoneId);
                            LocalDate parse = LocalDate.parse(day.value());
                            return Long.toString(ChronoUnit.DAYS.between(parse, now));
                        }
                )
        );
        registerNoticeElement(
                new NoticeElement.NoticeElementInfo(
                        "left_curly_brace",
                        Set.of(),
                        (context, noticeElement) -> "{"
                )
        );
        registerNoticeElement(
                new NoticeElement.NoticeElementInfo(
                        "right_curly_brace",
                        Set.of(),
                        (context, noticeElement) -> "}"
                )
        );
        registerNoticeElement(
                new NoticeElement.NoticeElementInfo(
                        "player_name",
                        Set.of(),
                        (context, noticeElement) -> context.player().getName().getString()
                )
        );
        registerNoticeElement(
                new NoticeElement.NoticeElementInfo(
                        "seed",
                        Set.of(),
                        (context, noticeElement) -> Long.toString(context.server().getWorld(World.OVERWORLD).getSeed())
                )
        );
        registerNoticeElement(
                new NoticeElement.NoticeElementInfo(
                        "minecraft_version",
                        Set.of(),
                        (context, noticeElement) -> context.server().getVersion()
                )
        );
    }

    private static void registerNoticeElement(NoticeElement.NoticeElementInfo newNoticeElementInfo) throws IllegalArgumentException{
        if (NoticeElement
                .NOTICE_ELEMENT_INFOS
                .stream()
                .filter(noticeElementInfo -> noticeElementInfo.name().equals(newNoticeElementInfo.name()))
                .toArray()
                .length == 0){
            NoticeElement.NOTICE_ELEMENT_INFOS.add(newNoticeElementInfo);
        } else {
            throw new IllegalArgumentException("Notice element " + newNoticeElementInfo.name() + " already exists");
        }
    }

    private static Parameter getParameter(String name, Set<NoticeElement.Parameter> parameters){
        for (NoticeElement.Parameter parameter : parameters) {
            if (parameter.name().equals(name)) {
                return parameter;
            }
        }
        throw new IllegalArgumentException("Unknown parameter: " + name);
    }

    private static boolean isThrowException(Runnable runnable){
        try {
            runnable.run();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private record NoticeElementInfo(String name, Set<ParameterInfo>  parameters, BiFunction<NoticeContext, NoticeElement, String> parser){

    }

    private record ParameterInfo(String name, boolean must, String defaultValue, ParameterValidator parameterValidator){

    }

    private interface ParameterValidator {
        boolean isValidValue(String value);

        default String errorMessage(NoticeElement.ParameterInfo parameterInfo, String wrongValue){
            return "Invalid value for parameter " + parameterInfo.name() + ": " + wrongValue;
        }
    }

    private record Parameter(String name, String value){

    }
}