package com.agile.common.mybatis;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 描述：
 * <p>创建时间：2018/12/17<br>
 *
 * @param <T> 分页内容类型
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Data
public class Page<T> extends ArrayList<T> {
    private MybatisPage pageRequest;
    private long total;
    private final List<T> content = new ArrayList<>();

    public Page(Collection<? extends T> c, MybatisPage pageRequest, long total) {
        super(c);
        this.pageRequest = pageRequest;
        this.total = total;
        this.content.addAll(c);
    }
}
