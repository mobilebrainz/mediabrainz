package app.mediabrainz.api.core;

import java.util.List;
import java.util.StringTokenizer;


public class ApiUtils {

    public static String getStringFromArray(Object[] array, String separator) {
        StringBuilder builder = new StringBuilder("");
        if (array != null) {
            int length = array.length;
            for (int i = 0; i < length; ++i) {
                if (array[i] != null && !array[i].toString().equals("")) {
                    if (builder.length() > 0) {
                        builder.append(separator);
                    }
                    builder.append(array[i].toString());
                }
            }
        }
        return builder.toString();
    }

    public static String getStringFromList(List<? extends Object> list, String separator) {
        if (list == null || list.isEmpty()) return "";

        StringBuilder builder = new StringBuilder("");
        for (Object obj : list) {
            if (obj != null && !obj.toString().equals("")) {
                if (builder.length() > 0) {
                    builder.append(separator);
                }
                builder.append(obj.toString());
            }
        }
        return builder.toString();
    }

    public static String initialCaps(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text, " ", true);
        StringBuilder sb = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = String.format("%s%s", Character.toUpperCase(token.charAt(0)), token.substring(1));
            sb.append(token);
        }
        return sb.toString();
    }

}
