package aji.carpetajiaddition.config;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;

public class NoticeConfig implements Config{
    public static final String CONFIG_NAME = "notice";
    private static final Set<NoticeElementInfo> NOTICE_ELEMENT_INFOS = new HashSet<>();

    private final List<Notice> notices = new ArrayList<>();

    @Override
    public String name() {
        return CONFIG_NAME;
    }

    @Override
    public void initConfigFile(JsonWriter writer) throws IOException{
        writer.name(CONFIG_NAME);
        writer.beginArray();
        writer.beginObject();
        writer.name("alsoNotifyEntrant").value(false);
        writer.name("priority").value("entrant");
        writer.name("weight").value(5);
        writer.name("info");
        writer.beginObject();
        writer.name("entrant");
        writer.beginObject();
        writer.name("0").value("欢迎");
        writer.endObject();
        writer.name("others");
        writer.beginObject();
        writer.name("0").value("{player_name}进入游戏");
        writer.endObject();
        writer.endObject();
        writer.endObject();
        writer.beginObject();
        writer.name("alsoNotifyEntrant").value(false);
        writer.name("priority").value("entrant");
        writer.name("weight").value(5);
        writer.name("info");
        writer.beginObject();
        writer.name("entrant").nullValue();
        writer.name("others").nullValue();
        writer.endObject();
        writer.endObject();
        writer.endArray();
    }

    @Override
    public void load(JsonElement element) {
        for (JsonElement jsonElement : element.getAsJsonArray()) {
            notices.add(new Notice(jsonElement.getAsJsonObject()));
        }
    }

    public void send(ServerPlayerEntity player){
        if (notices.isEmpty()) return;
        int totalWeight = notices.stream().mapToInt(notice -> notice.weight).sum();
        int random = new Random().nextInt(totalWeight);
        int currentWeight = 0;
        for (Notice notice : notices) {
            currentWeight += notice.weight;
            if (random < currentWeight) {
                notice.send(player);
                break;
            }
        }
    }

    public static void registerNoticeElements(){
        registerNoticeElement(
                new NoticeElementInfo(
                        "online_users",
                        Set.of(
                                new NoticeElementParameterInfo("include", false, "true", string -> (string.equals("true") || string.equals("false")))
                        ),
                        (player, noticeElement) -> {
                            NoticeElementParameter include = getParameter("include", noticeElement.noticeElementParameters);
                            int size = CarpetAjiAdditionSettings.minecraftServer.getPlayerManager().getPlayerList().size();
                            if (include.value().equals("true")) return Integer.toString(size);
                            else return Integer.toString(size - 1);
                        }
                )
        );
        registerNoticeElement(
                new NoticeElementInfo(
                        "now_time",
                        Set.of(
                                new NoticeElementParameterInfo("format", false, "YYYY-MM-dd HH:mm:ss", string -> !isThrowException(() -> DateTimeFormatter.ofPattern(string))),
                                new NoticeElementParameterInfo("zone", false, ZoneId.systemDefault().getId(), string -> !isThrowException(() -> ZoneId.of(string)))
                        ),
                        (player, noticeElement) -> {
                            NoticeElementParameter format = getParameter("format", noticeElement.noticeElementParameters);
                            NoticeElementParameter zone = getParameter("zone", noticeElement.noticeElementParameters);
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format.value());
                            ZoneId zoneId = ZoneId.of(zone.value());
                            return LocalDateTime.now(zoneId).format(dateTimeFormatter);
                        }
                )
        );
        registerNoticeElement(
                new NoticeElementInfo(
                        "day_to_today",
                        Set.of(
                                new NoticeElementParameterInfo("day", true, null, string -> !isThrowException(() -> LocalDate.parse(string))),
                                new NoticeElementParameterInfo("zone", false, ZoneId.systemDefault().getId(), string -> !isThrowException(() -> ZoneId.of(string)))
                        ),
                        (player, noticeElement) -> {
                            NoticeElementParameter day = getParameter("day", noticeElement.noticeElementParameters);
                            NoticeElementParameter zone = getParameter("zone", noticeElement.noticeElementParameters);
                            ZoneId zoneId = ZoneId.of(zone.value());
                            LocalDate now = LocalDate.now(zoneId);
                            LocalDate parse = LocalDate.parse(day.value());
                            return Long.toString(ChronoUnit.DAYS.between(parse, now));
                        }
                )
        );
        registerNoticeElement(
                new NoticeElementInfo(
                        "left_curly_brace",
                        Set.of(),
                        (player, noticeElement) -> "{"
                )
        );
        registerNoticeElement(
                new NoticeElementInfo(
                        "right_curly_brace",
                        Set.of(),
                        (player, noticeElement) -> "}"
                )
        );
        registerNoticeElement(
                new NoticeElementInfo(
                        "player_name",
                        Set.of(),
                        (player, noticeElement) -> player.getName().getString()
                )
        );
        registerNoticeElement(
                new NoticeElementInfo(
                        "seed",
                        Set.of(),
                        (player, noticeElement) -> Long.toString(CarpetAjiAdditionSettings.minecraftServer.getWorld(World.OVERWORLD).getSeed())
                )
        );
        registerNoticeElement(
                new NoticeElementInfo(
                        "minecraft_version",
                        Set.of(),
                        (player, noticeElement) -> CarpetAjiAdditionSettings.minecraftServer.getVersion()
                )
        );
    }

    private static class Notice {
        private final boolean alsoNotifyEntrant;
        private final String priority;
        private final int weight;
        private final NoticeEntity entrant;
        private final NoticeEntity others;

        public Notice(JsonObject jsonObject){
            alsoNotifyEntrant = jsonObject.get("alsoNotifyEntrant").getAsBoolean();
            priority = jsonObject.get("priority").getAsString();
            int v = jsonObject.get("weight").getAsInt();
            if (v >= 1 && v <= 10) weight = v;
            else weight = 5;
            JsonObject info = jsonObject.getAsJsonObject("info");
            entrant = new NoticeEntity(info.get("entrant").isJsonNull() ? null : (info.getAsJsonObject("entrant")));
            others = new NoticeEntity(info.get("others").isJsonNull() ? null : (info.getAsJsonObject("others")));
        }

        public void send(ServerPlayerEntity player){
            if (priority.equals("others")){
                sendOthers(player);
                entrant.send(player);
            } else {
                entrant.send(player);
                sendOthers(player);
            }
        }

        private void sendOthers(ServerPlayerEntity player){
            for (ServerPlayerEntity playerEntity : CarpetAjiAdditionSettings.minecraftServer.getPlayerManager().getPlayerList()) {
                if (!playerEntity.getUuid().equals(player.getUuid()) || alsoNotifyEntrant) others.send(playerEntity);
            }
        }
    }

    private static class NoticeEntity {
        private final boolean isSend;
        private final LinkedList<NoticeLine> lines;

        public NoticeEntity(JsonObject jsonObject){
            if (jsonObject == null){
                isSend = false;
                lines = null;
            }else {
                isSend = true;
                LinkedList<NoticeLine> lines = new LinkedList<>();
                try {
                    Map<Integer, NoticeLine> temp = new HashMap<>();
                    int size = 0;
                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        String key = entry.getKey();
                        if (!key.matches("\\d+")) throw new IllegalArgumentException("key must be a.java number");
                        int index = Integer.parseInt(key);
                        NoticeLine noticeLine = new NoticeLine(entry.getValue().getAsString());
                        temp.put(index, noticeLine);
                        size++;
                    }
                    for (int i = 0; i < size; i++) {
                        NoticeLine noticeLine = temp.get(i);
                        if (noticeLine == null) throw new IllegalArgumentException("key must be continuous");
                        lines.add(noticeLine);
                    }
                }catch (Exception e){
                    throw new IllegalArgumentException(e);
                }
                this.lines = lines;
            }
        }

        public void send(ServerPlayerEntity player){
            if (!isSend) return;
            for (NoticeLine line : lines) {
                player.sendMessage(line.toText(player));
            }
        }
    }

    private static class NoticeLine {
        private final LinkedList<NoticeElement> elements;

        public NoticeLine(String string){
            LinkedList<NoticeElement> elements = new LinkedList<>();
            char[] charArray = string.toCharArray();
            StringBuilder elementTemp = new StringBuilder();
            StringBuilder stringTemp = new StringBuilder();
            boolean bl = true;
            for (int i = 0; i < charArray.length; i++) {
                if (bl) {
                    if (charArray[i] == '{') {
                        bl = false;
                        if (!stringTemp.isEmpty()) {
                            elements.add(ofNoticeElement(stringTemp.toString()));
                            stringTemp = new StringBuilder();
                        }
                        elementTemp.append('{');
                    } else if (charArray[i] == '}') {
                        throw new IllegalArgumentException("Unexpected '}' at position " + i);
                    } else {
                        stringTemp.append(charArray[i]);
                    }
                } else {
                    if (charArray[i] == '}') {
                        bl = true;
                        elementTemp.append('}');
                        elements.add(ofNoticeElement(elementTemp.toString()));
                        elementTemp = new StringBuilder();
                    } else {
                        elementTemp.append(charArray[i]);
                    }
                }
            }
            if (!stringTemp.isEmpty()) {
                elements.add(ofNoticeElement(stringTemp.toString()));
            }
            if (!bl) {
                throw new IllegalArgumentException("Unclosed '{' at end of string");
            }
            this.elements = elements;
        }

        public Text toText(ServerPlayerEntity player){
            StringBuilder stringBuilder = new StringBuilder();
            for (NoticeElement element : elements) {
                stringBuilder.append(element.parse(player));
            }
            return Text.literal(stringBuilder.toString());
        }
    }

    private static class NoticeElement {
        private final Set<NoticeElementParameter> noticeElementParameters;
        private final BiFunction<ServerPlayerEntity, NoticeElement, String> parser;

        private NoticeElement(String[] parameters, NoticeElementInfo NoticeElementInfo){
            Set<NoticeElementParameter> inputNoticeElementParameters = new HashSet<>();
            for (String parameter : parameters) {
                String[] split = parameter.split("=");
                for (NoticeElementParameterInfo parameterInfo : NoticeElementInfo.NoticeElementParameters()) {
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

                        inputNoticeElementParameters.add(new NoticeElementParameter(parameterInfo.name(), value.toString()));
                        break;
                    }
                }
            }

            NoticeElementInfo.NoticeElementParameters().stream()
                    .filter(NoticeElementParameterInfo::must)
                    .forEach(paramInfo -> {
                        if (inputNoticeElementParameters.stream().noneMatch(p -> p.name().equals(paramInfo.name()))) {
                            throw new IllegalArgumentException("Must parameter " + paramInfo.name + " is not provided");
                        }
                    });

            NoticeElementInfo.NoticeElementParameters().stream()
                    .filter(paramInfo -> !paramInfo.must())
                    .filter(paramInfo -> inputNoticeElementParameters.stream().noneMatch(p -> p.name().equals(paramInfo.name())))
                    .forEach(paramInfo -> inputNoticeElementParameters.add(new NoticeElementParameter(paramInfo.name(), paramInfo.defaultValue())));


            NoticeElementInfo.NoticeElementParameters().stream()
                    .filter(NoticeElementparameterInfo -> inputNoticeElementParameters.stream()
                            .anyMatch(noticeElementParameter -> noticeElementParameter.name().equals(NoticeElementparameterInfo.name()) &&
                                    !NoticeElementparameterInfo.noticeElementParameterValidator.isValidValue(noticeElementParameter.value)))
                    .findFirst()
                    .ifPresent(parameterInfo -> {
                        NoticeElementParameter noticeElementParameter = inputNoticeElementParameters.stream()
                                .filter(p -> p.name().equals(parameterInfo.name()))
                                .findFirst()
                                .orElseThrow();
                        throw new IllegalArgumentException(parameterInfo.noticeElementParameterValidator.errorMessage(parameterInfo, noticeElementParameter.value));
                    });

            this.noticeElementParameters = inputNoticeElementParameters;
            this.parser = NoticeElementInfo.parser;
        }

        public String parse(ServerPlayerEntity player) {
            return parser.apply(player, this);
        }
    }

    private static NoticeElement ofNoticeElement(String string){
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

    private static void registerNoticeElement(NoticeElementInfo newNoticeElementInfo) throws IllegalArgumentException{
        if (NOTICE_ELEMENT_INFOS
                .stream()
                .filter(noticeElementInfo -> noticeElementInfo.name().equals(newNoticeElementInfo.name()))
                .toArray()
                .length == 0){
            NOTICE_ELEMENT_INFOS.add(newNoticeElementInfo);
        } else {
            throw new IllegalArgumentException("NoticeEntity element " + newNoticeElementInfo.name() + " already exists");
        }
    }

    private static NoticeElementParameter getParameter(String name, Set<NoticeElementParameter> noticeElementParameters){
        for (NoticeElementParameter noticeElementParameter : noticeElementParameters) {
            if (noticeElementParameter.name().equals(name)) {
                return noticeElementParameter;
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

    private record NoticeElementInfo(String name, Set<NoticeElementParameterInfo> NoticeElementParameters, BiFunction<ServerPlayerEntity, NoticeElement, String> parser){

    }

    private record NoticeElementParameterInfo(String name, boolean must, String defaultValue, NoticeElementParameterValidator noticeElementParameterValidator){

    }

    private interface NoticeElementParameterValidator {
        boolean isValidValue(String value);

        default String errorMessage(NoticeElementParameterInfo parameterInfo, String wrongValue){
            return "Invalid value for parameter " + parameterInfo.name() + ": " + wrongValue;
        }
    }

    private record NoticeElementParameter(String name, String value){

    }
}