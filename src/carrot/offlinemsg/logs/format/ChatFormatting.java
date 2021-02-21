package carrot.offlinemsg.logs.format;

public class ChatFormatting {
    public static String Apostrophise(String content){
        return Surround("'", content, "'");
    }

    public static String Bracketise(String content){
        return Surround("[", content, "]");
    }

    public static String Surround(String left, String content, String right){
        return left + content + right;
    }
}
