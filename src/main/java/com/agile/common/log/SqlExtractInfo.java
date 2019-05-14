package com.agile.common.log;

import com.agile.common.base.Constant;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2019/5/13 18:25
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
public class SqlExtractInfo {
    /**
     * 操作类型
     */
    public enum OperationType {
        /**
         * 新增
         */
        Insert,
        /**
         * 删除
         */
        Delete,
        /**
         * 修改
         */
        Update,
        /**
         * 查询
         */
        SELECT
    }

    private String from;
    private String sql;
    private OperationType type;
    private Map<String, String> columnInfo;

    public void add(String column, String value) {
        if (this.columnInfo == null) {
            this.columnInfo = new HashMap<>(Constant.NumberAbout.TWELVE);
        }
        this.columnInfo.put(column, value);
    }
}
