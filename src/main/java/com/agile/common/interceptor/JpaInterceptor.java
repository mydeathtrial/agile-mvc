package com.agile.common.interceptor;

import com.agile.common.factory.LoggerFactory;
import org.apache.commons.logging.Log;
import org.hibernate.EmptyInterceptor;
import org.springframework.stereotype.Component;

/**
 * Created by 佟盟 on 2017/11/3
 */
@Component
public class JpaInterceptor extends EmptyInterceptor {
    private static final long serialVersionUID = -4455619920711458111L;

    @Override
    public String onPrepareStatement(String sql) {
        if(LoggerFactory.DAO_LOG.isInfoEnabled()){
            LoggerFactory.DAO_LOG.info("\n[SQL语句:]"+sql+"\n");
        }
        return super.onPrepareStatement(sql);
    }
}
