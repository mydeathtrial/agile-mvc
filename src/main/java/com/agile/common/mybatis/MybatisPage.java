package com.agile.common.mybatis;

/**
 * @author 佟盟
 * 日期 2019/3/13 18:38
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface MybatisPage {
    /**
     * 页号
     *
     * @return 页号
     */
    int getPageNum();

    /**
     * 页大小
     *
     * @return 大小
     */
    int getPageSize();
}
