package com.ey.util.csv;

import com.opencsv.ICSVParser;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//重写ICSVParser方法
public class CustomCSVParser implements ICSVParser {
    private final char separator; // 字段分隔符
    private final char quotechar; // 引号字符
    private final char escapechar; // 转义字符
    private final boolean strictQuotes; // 是否严格处理引号
    private final boolean ignoreLeadingWhitespaces; // 是否忽略前导空格

    // 缓存未完成的字段内容
    private StringBuilder pendingField = new StringBuilder(); // 用于缓存跨行字段
    private boolean inQuotes = false; // 当前是否在引号内

    public CustomCSVParser(char separator, char quotechar, char escapechar, boolean strictQuotes, boolean ignoreLeadingWhitespaces) {
        this.separator = separator;
        this.quotechar = quotechar;
        this.escapechar = escapechar;
        this.strictQuotes = strictQuotes;
        this.ignoreLeadingWhitespaces = ignoreLeadingWhitespaces;
    }

    @Override
    public char getSeparator() {
        return separator;
    }

    @Override
    public char getQuotechar() {
        return quotechar;
    }

    @Override
    public boolean isPending() {
        return inQuotes || pendingField.length() > 0;
    }

    @Override
    public String[] parseLineMulti(String part) {
        inQuotes= false;
        if (part == null) {
            return new String[0];
        }

        // 每次调用时重新初始化
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();

        String line = part;
        // 如果有未完成的字段，先将其内容追加到当前字段
        if (pendingField.length() > 0) {
            line = pendingField.append(part).toString();
        }
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == escapechar) {
                i++; // 跳过转义字符后的字符
                if (i < line.length()) {
                    field.append(line.charAt(i));
                }
            } else if (c == quotechar) {
                inQuotes = !inQuotes; // 切换引号状态
                if (!strictQuotes || !inQuotes) {
                    field.append(c);
                }
            } else if (c == separator && !inQuotes) {
                fields.add(field.toString().trim());
                field.setLength(0); // 清空 StringBuilder 以处理下一个字段
            } else {
                field.append(c);
            }
        }

        // 如果当前字段尚未结束（字段被引号包裹且尚未结束）
        if (inQuotes) {
            if(pendingField.length() == 0){
                pendingField.append(line); // 缓存未完成的字段
            }
            return new String[0]; // 返回空数组，表示需要继续解析下一行
        } else {
            fields.add(field.toString().trim());
            pendingField.setLength(0); // 清空缓存
            return fields.toArray(new String[0]);
        }
    }

    @Override
    public String[] parseLine(String nextLine) throws IOException {
        return parseLineMulti(nextLine); // 直接调用 parseLineMulti
    }

//    private String cleanField(String fieldValue) {
//        // 替换英文逗号为中文逗号
//        return fieldValue.replace(",", "，");
//    }

    @Override
    public String parseToLine(String[] strings, boolean b) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public CSVReaderNullFieldIndicator nullFieldIndicator() {
        return CSVReaderNullFieldIndicator.EMPTY_SEPARATORS;
    }

    @Override
    public String getPendingText() {
        return pendingField.toString();
    }

    @Override
    public void setErrorLocale(Locale locale) {
        // 保留空实现
    }
}
