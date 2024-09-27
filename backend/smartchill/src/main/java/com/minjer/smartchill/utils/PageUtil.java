package com.minjer.smartchill.utils;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {
    public static <T> List<T> paginate(int pageNumber, int pageSize, ArrayList<T> originalList) {
        // 如果原始列表为空，返回空集合
        if (originalList == null || originalList.isEmpty()) {
            return new ArrayList<>();
        }

        // 计算起始索引
        int startIndex = (pageNumber - 1) * pageSize;

        // 如果起始索引超出总数，返回空集合
        if (startIndex >= originalList.size()) {
            return new ArrayList<>();
        }

        // 计算结束索引
        int endIndex = Math.min(startIndex + pageSize, originalList.size());

        // 返回分页结果
        return new ArrayList<>(originalList.subList(startIndex, endIndex));
    }
}
