package nl.fm.downline.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ruud de Jong
 */
public final class Utils {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;
    private static final int INDEX_NOT_FOUND = -1;
    private static final String EMPTY = "";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private Utils() {
        // Utility class
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    public static String toString(InputStream input) throws IOException {
        StringWriter writer = new StringWriter();
        InputStreamReader in = new InputStreamReader(input);
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int n = 0;
        while (EOF != (n = in.read(buffer))) {
            writer.write(buffer, 0, n);
        }
        return writer.toString();
    }

    public static String join(String[] array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<array.length; i++) {
            builder.append(array[i]);
            if (i != (array.length-1)) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public static String[] split(String str, String separator) {
        if (str == null) {
            return null;
        }
        if (separator == null || separator.length() != 1) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        // Optimise 1 character case
        char sep = separator.charAt(0);
        while (i < len) {
            if (str.charAt(i) == sep) {
                if (match) {
                    if (sizePlus1++ == 0) {
                        i = len;
                    }
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.length() == 0) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static String[] substringsBetween(String str, String open, String close) {
        if (str == null || isEmpty(open) || isEmpty(close)) {
            return null;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return EMPTY_STRING_ARRAY;
        }
        int closeLen = close.length();
        int openLen = open.length();
        List list = new ArrayList();
        int pos = 0;
        while (pos < (strLen - closeLen)) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.isEmpty()) {
            return null;
        }
        return (String[]) list.toArray(new String [list.size()]);
    }

    public static float parseGetal(String getal) throws ParseException {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(dfs);
        Number number = decimalFormat.parse(getal);
        return number.floatValue();
    }

}
