package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter;

    public LocalDateTimeAdapter() {
        // Khởi tạo định dạng cho LocalDateTime, ví dụ: yyyy-MM-dd HH:mm:ss
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        // Ghi LocalDateTime thành chuỗi theo định dạng và gửi xuống JSON
        if (value != null) {
            out.value(formatter.format(value));
        } else {
            out.nullValue();
        }
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