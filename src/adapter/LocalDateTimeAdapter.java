package adapter;



import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;



public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private final DateTimeFormatter formatter1;

    public LocalDateTimeAdapter() {
        // Khởi tạo định dạng cho LocalDateTime, ví dụ: yyyy-MM-dd HH:mm:ss
        this.formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        // Ghi LocalDateTime thành chuỗi theo định dạng và gửi xuống JSON
        if (value != null) {
            out.value(formatter1.format(value));
        } else {
            out.nullValue();
        }
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(src));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        // Đọc giá trị từ JSON và chuyển đổi thành LocalDateTime
        String dateString = in.nextString();
        if (dateString != null) {
            return LocalDateTime.parse(dateString, formatter);
        } else {
            return null;
        }
    }
}