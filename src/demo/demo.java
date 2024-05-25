package demo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class demo {
    public static void main(String[] args) throws IOException {
        String text = "This is a simple example Donald Trump demonstrating how to remove stop words using Lucene.";

        // Đọc danh sách stop words từ tệp JSON bằng Gson
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> stopWords = gson.fromJson(new FileReader("stop_words_english.json"), type);
       

        // Tạo CharArraySet từ danh sách stop words
        CharArraySet stopSet = new CharArraySet(stopWords, true);

        // Tạo Analyzer với StopFilter
        try (Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
                TokenStream tokenStream = new StopFilter(tokenizer, stopSet);
                return new TokenStreamComponents(tokenizer, tokenStream);
            }
        }) {
            // Phân tích văn bản và loại bỏ stop words
            try (TokenStream tokenStream = analyzer.tokenStream("field", new StringReader(text))) {
                CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();

                while (tokenStream.incrementToken()) {
                    String term = charTermAttribute.toString();
                    System.out.println(term);
                }

                tokenStream.end();
            }
        }
    }
}
