package com.agile.common.base;

import com.agile.common.annotation.Remark;
import com.agile.common.util.MapUtil;
import com.agile.common.util.StringUtil;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * Created by 佟盟 on 2018/11/2
 */
public abstract class AbstractResponseFormat extends LinkedHashMap<String,Object> {
    public AbstractResponseFormat buildResponse(){
        MapUtil.coverMap(this,this);
        return this;
    }
    public ModelAndView buildResponse(Head head,Object result){
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
            Remark remark = field.getAnnotation(Remark.class);
            if(remark!=null){
                try {
                    String param = remark.value();
                    if(Constant.ResponseAbout.RESULT.equals(param)){
                        field.set(this,result);
                    }else{
                        if(head==null) {
                            continue;
                        }
                        Field f = Head.class.getDeclaredField(remark.value());
                        f.setAccessible(true);
                        try {
                            field.set(this,f.get(head));
                        }catch (Exception e){
                            Method set = this.getClass().getDeclaredMethod("set" + StringUtil.toUpperName(field.getName()), f.getType());
                            set.setAccessible(true);
                            set.invoke(this,f.get(head));
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.addAllObjects(buildResponse());
        return mv;
    }
}
