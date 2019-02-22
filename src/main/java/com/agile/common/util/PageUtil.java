package com.agile.common.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/2/22 15:06
 * 描述： TODO
 * @since 1.0
 */
public class PageUtil {
    public static <T, P> Page<P> setContent(Page<T> page, List<P> content) {
        if (page.getContent().size() == content.size()) {
            return new PageImpl<P>(content, page.getPageable(), page.getTotalElements());
        } else {
            throw new IllegalArgumentException("修改后的分页条数应该与修改前的分页条数相同");
        }
    }
}
