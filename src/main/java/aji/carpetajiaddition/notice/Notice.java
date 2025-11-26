package aji.carpetajiaddition.notice;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Notice {
    private final boolean isSend;
    private final LinkedList<NoticeLine> lines;

    public Notice(JsonElement jsonElement){
        if (jsonElement == null){
            isSend = false;
            lines = null;
        }else {
            isSend = true;
            LinkedList<NoticeLine> lines = new LinkedList<>();
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
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

    public void send(NoticeContext context){
        if (!isSend) return;
        for (NoticeLine line : lines) {
            context.player().sendMessage(line.toText(context));
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
                            elements.add(NoticeElement.of(stringTemp.toString()));
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
                        elements.add(NoticeElement.of(elementTemp.toString()));
                        elementTemp = new StringBuilder();
                    } else {
                        elementTemp.append(charArray[i]);
                    }
                }
            }
            if (!stringTemp.isEmpty()) {
                elements.add(NoticeElement.of(stringTemp.toString()));
            }
            if (!bl) {
                throw new IllegalArgumentException("Unclosed '{' at end of string");
            }
            this.elements = elements;;
        }

        public Text toText(NoticeContext context){
            StringBuilder stringBuilder = new StringBuilder();
            for (NoticeElement element : elements) {
                stringBuilder.append(element.parse(context));
            }
            return Text.literal(stringBuilder.toString());
        }
    }
}
