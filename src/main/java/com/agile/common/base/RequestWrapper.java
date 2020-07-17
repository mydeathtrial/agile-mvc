package com.agile.common.base;

import com.agile.common.util.ArrayUtil;
import com.agile.common.util.stream.StreamUtil;
import com.google.common.collect.Maps;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author 佟盟 on 2018/3/26
 * HttpServletRequest扩展对象
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    private final ByteArrayOutputStream body;
    private final Map<String, String[]> params;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.params = Maps.newHashMap();
        params.remove(Constant.ResponseAbout.SERVICE);
        params.remove(Constant.ResponseAbout.METHOD);
        params.putAll(request.getParameterMap());

        body = new ByteArrayOutputStream();
        InputStream ins = request.getInputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = ins.read(buffer)) > -1) {
            body.write(buffer, 0, len);
        }
        body.flush();
    }


    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    /**
     * 为request添加parameter参数
     *
     * @param key key值
     * @param o   value值
     */
    public void addParameter(String key, String o) {
        if (this.params.containsKey(key)) {
            String[] value = params.get(key);
            params.put(key, ArrayUtil.add(value, o));
        }
        this.params.put(key, new String[]{o});
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.toByteArray());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }
}
