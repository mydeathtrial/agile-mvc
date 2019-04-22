package com.agile.common.mybatis;

import com.agile.common.mvc.model.dao.Dao;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 描述：
 * <p>创建时间：2018/12/17<br>
 *
 * @param <T> 分页内容类型
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class Page<T> extends LinkedList<T> {
    private PageRequest pageRequest;
    private long total;

    public Page(Collection<? extends T> c, PageRequest pageRequest, long total) {
        super(c);
        Dao.validatePageInfo(pageRequest.getPageNumber(), pageRequest.getPageSize());
        this.pageRequest = pageRequest;
        this.total = total;
    }

    public org.springframework.data.domain.Page<T> getPage() {
        return new PageImpl<T>(this, PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort()), total);
    }
}
