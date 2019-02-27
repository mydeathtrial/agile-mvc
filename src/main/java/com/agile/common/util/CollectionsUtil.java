package com.agile.common.util;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/12/11
 */
public class CollectionsUtil extends CollectionUtils {
    public static <T> void sort(List<T> list, String propertiy) {
        list.sort((o1, o2) -> {
            try {
                if (Map.class.isAssignableFrom(o1.getClass())) {
                    return Integer.parseInt(String.valueOf(((Map) o1).get(propertiy))) - Integer.parseInt(String.valueOf(((Map) o2).get(propertiy)));
                } else {
                    Class<?> clazz = o1.getClass();
                    Field field = clazz.getDeclaredField(propertiy);
                    field.setAccessible(true);

                    return Integer.parseInt(field.get(o1).toString()) - Integer.parseInt(field.get(o2).toString());
                }

            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }
}
