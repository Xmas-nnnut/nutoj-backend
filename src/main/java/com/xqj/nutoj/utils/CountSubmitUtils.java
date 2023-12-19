package com.xqj.nutoj.utils;

import com.xqj.nutoj.model.vo.QuestionSubmitVO;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工具类
 *
 */
public class CountSubmitUtils {

        public static List<Map<String, Object>> countByCreateTime(List<QuestionSubmitVO> questionSubmitVOList) {
            // 创建一个Map用于统计相同createTime的数据
            Map<String, Integer> countMap = new HashMap<>();

            // 遍历questionSubmitVOList
            for (QuestionSubmitVO questionSubmitVO : questionSubmitVOList) {
                // 获取createTime并转换为字符串，精度到同一天
                String createTime = formatCreateTime(questionSubmitVO.getCreateTime());

                // 更新countMap中的统计信息
                countMap.put(createTime, countMap.getOrDefault(createTime, 0) + 1);
            }

            // 将统计结果整理成Array
            List<Map<String, Object>> resultArray = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                Map<String, Object> resultItem = new HashMap<>();
                resultItem.put("date", entry.getKey());
                resultItem.put("count", entry.getValue());
                resultArray.add(resultItem);
            }

            return resultArray;
        }

        private static String formatCreateTime(Date createTime) {
            // 使用SimpleDateFormat将Date转换为精度到同一天的字符串
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(createTime);
        }
}
