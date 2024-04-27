package demoapache;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

// Ví dụ về cách chuyển từ số giây từ Epoch sang LocalDateTime
// In ra ngày giờ tương ứng với số giây từ Epoch
public class test {
	public static void main(String [] args) {
	long epochSecond = 1619689123; // Ví dụ về số giây từ Epoch
	LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
	System.out.println(dateTime); // In ra ngày giờ tương ứng 
}
}
