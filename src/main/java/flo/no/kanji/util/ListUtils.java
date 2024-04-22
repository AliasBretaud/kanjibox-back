package flo.no.kanji.util;

import java.util.List;

public class ListUtils {
    public static <T> List<T> truncateList(final List<T> list, final int limit) {
        return list != null ? list.stream().limit(limit).toList() : null;
    }
}
