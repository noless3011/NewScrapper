package searchengine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class EntityRecognition {
	public EntityRecognition() {

	}

	private String deleteStopWords(String text) throws IOException {
		Gson gson = new Gson();
		Type type = new TypeToken<List<String>>() {
		}.getType();
		List<String> stopWords = new ArrayList<>();
		List<String> verbs = new ArrayList<>();

		// Đọc dữ liệu các từ stopWords từ file JSON
		try {
			stopWords = gson.fromJson(new FileReader("stop_words_english.json"), type);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}

		// Đọc dữ liêu các động từ thường gặp trong Tiếng Anh
		try {
			verbs = gson.fromJson(new FileReader("verbs.json"), type);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}

		// Xóa tất cả các dấu ngoài lề trừ các ký tự kết thúc câu
		text = text.replaceAll("[^\\p{L}\\p{N}\\.\\!\\?\\,\\:\\s]", "");

		String[] words = text.split("\\s+");
		List<String> cleanedWords = new ArrayList<>();
		for (String word : words) {
			String cleanedWord = word.toLowerCase();
			if (!stopWords.contains(cleanedWord) && !verbs.contains(cleanedWord)) {
				cleanedWords.add(word);
			}
		}

		return String.join(" ", cleanedWords);
	}

	public List<String> SimpleEntityRecognition(String text) throws IOException {
		String newtext = deleteStopWords(text);
		String[] words = newtext.split("\\s+");
		List<String> entities = new ArrayList<>();
		boolean isStartOfSentence = true;
		StringBuilder currentEntity = new StringBuilder();

		for (String word : words) {
			if (word.isEmpty()) {
				continue; // Bỏ qua các từ rỗng
			}
			boolean isCapitalized = Character.isUpperCase(word.charAt(0));
			if (isCapitalized && !isStartOfSentence) {
				// Nếu từ viết hoa và không phải đầu câu, thêm vào thực thể hiện tại
				if (currentEntity.length() > 0) {
					currentEntity.append(" ");
				}
				currentEntity.append(word);
			} else {
				// Nếu không, kiểm tra xem có thực thể hiện tại không
				if (currentEntity.length() > 0) {
					entities.add(currentEntity.toString().trim());
					currentEntity.setLength(0);
				}
				// Đặt lại biến đầu câu
				isStartOfSentence = word.endsWith(".") || word.endsWith("!") || word.endsWith("?") || word.endsWith(",")
						|| word.endsWith(":");
				if (isCapitalized) {
					currentEntity.append(word);
				}
			}
		}

		// Thêm thực thể cuối cùng nếu có
		if (currentEntity.length() > 0) {
			entities.add(currentEntity.toString().trim());
		}
		entities = removeSimilarStrings(entities);
		for (String entity: entities) {
			entity = entity.replaceAll("[^A-Za-z0-9]", "");
		}
		return entities;
	}

	private List<String> removeSimilarStrings(List<String> stringList) {
		List<String> cleanedList = new ArrayList<>();
		for (String str : stringList) {
			boolean isSimilar = false;
			for (String cleanedStr : cleanedList) {
				// So sánh chuỗi đã được làm sạch với chuỗi hiện tại
				if (areStringsSimilar(cleanedStr, str)) {
					isSimilar = true;
					break;
				}
			}
			if (!isSimilar) {
				cleanedList.add(str);
			}
		}
		return cleanedList;
	}

	private boolean areStringsSimilar(String str1, String str2) {
		// So sánh hai chuỗi bằng cách loại bỏ dấu cách và dấu câu,
		// và chuyển đổi tất cả thành chữ hoa hoặc chữ thường để so sánh
		String cleanStr1 = str1.replaceAll("[\\s\\p{Punct}]", "").toLowerCase();
		String cleanStr2 = str2.replaceAll("[\\s\\p{Punct}]", "").toLowerCase();
		return cleanStr1.equals(cleanStr2);
	}
}