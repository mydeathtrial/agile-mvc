package com.agile.common.mybatis;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 描述：
 * <p>创建时间：2018/12/17<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class Page<T> extends LinkedList<T> {
    private PageRequest pageRequest;
    private long total;
    public org.springframework.data.domain.Page<T> getPage(){
        return new PageImpl(this,pageRequest,total);
    }

    public Page(@NotNull Collection<? extends T> c, PageRequest pageRequest, long total) {
        super(c);
        this.pageRequest = pageRequest;
        this.total = total;
    }
}
