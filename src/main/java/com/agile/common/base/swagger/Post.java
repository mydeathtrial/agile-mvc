package com.agile.common.base.swagger;

import java.util.List;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/10/5
 */
public class Post extends Path {

    public Post(Map.Entry<String, Object> entity, List<Param> parameters, Map<String, Map<String, String>> responses, String description) {
        super(entity, parameters, responses, description);
    }
}
