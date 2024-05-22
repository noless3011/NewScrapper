//package demoapache;
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.ling.CoreLabel;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.util.CoreMap;
//
//import java.util.List;
//import java.util.Properties;
//
//public class test {
//    public static void main(String[] args) {
//        // Thiết lập các thuộc tính cho Stanford CoreNLP
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
//        props.setProperty("ner.model", "src/edu/stanford/nlp/models/ner/english.all.3class.caseless.distsim.crf.ser.gz");
//        // Khởi tạo Stanford CoreNLP với các thuộc tính đã thiết lập
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//        
//        // Văn bản đầu vào
//        String text = "Barack Obama was born on August 4, 1961 in Honolulu, Hawaii.";
//
//        // Tạo đối tượng Annotation
//        Annotation document = new Annotation(text);
//
//        // Chạy pipeline trên văn bản
//        pipeline.annotate(document);
//
//        // Lấy danh sách các câu trong văn bản
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//
//        // Lặp qua các câu và in ra các thực thể được nhận diện
//        for (CoreMap sentence : sentences) {
//            // Lấy danh sách các từ trong câu
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // Lấy loại thực thể của từ
//                String nerTag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                // In ra các thực thể có nhãn không rỗng
//                if (!nerTag.equals("O")) {
//                    String word = token.get(CoreAnnotations.TextAnnotation.class);
//                    System.out.println("Entity: " + word + ", Type: " + nerTag);
//                }
//            }
//        }
//    }
//}