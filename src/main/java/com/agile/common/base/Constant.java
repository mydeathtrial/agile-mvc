package com.agile.common.base;

/**
 * Created by 佟盟 on 2017/9/2
 */
public class Constant {
    /**
     * 响应信息相关
     */
    public static class ResponseAbout {
        public final static String HEAD = "head";
        public final static String STATE = "state";
        public final static String MSG = "msg";
        public final static String CODE = "code";
        public final static String INFO = "info";
        public final static String APP = "app";
        public final static String SERVICE = "service";
        public final static String METHOD = "method";
        public final static String IP = "ip";
        public final static String URL = "url";
        public final static String RETURN = "return";
        public final static String RESULT = "result";
        public final static String BODY = "body";
    }

    /**
     * 文件相关
     */
    public static class FileAbout {
        public final static String FILE_NAME = "fileName";
        public final static String FILE_SIZE = "fileSize";
        public final static String CONTENT_TYPE = "contentType";
        public final static String UP_LOUD_FILE_INFO = "upLoadFileInfo";
        public final static String SERVICE_LOGGER_FILE = "service";
    }

    /**
     * 响应头信息相关
     */
    public static class HeaderAbout {
        public final static String ATTACHMENT = "attachment";
    }

    /**
     * 正则表达式
     */
    public static class RegularAbout {
        public final static String NULL = "";
        public final static String SEMICOLON = ";";
        public final static String COLON = ":";
        public final static String SPOT = ".";
        public final static String COMMA = ",";
        public final static String UP_COMMA = "'";
        public final static String UP_DOUBLE_COMMA = "\"";
        public final static String QUESTION_MARK = "?";
        public final static String SLASH = "/";
        public final static String BACKSLASH = "\\";
        public final static String AND = "&";
        public final static String EQUAL = "=";
        public final static String HUMP = "((?=[\\x21-\\x7e]+)[^A-Za-z0-9])";
        public final static String UPER = "[A-Z]";
        public final static String URL_REGEX ="[\\W_]?";
        public final static String HTTP = "http";
        public final static String HTTPS = "https";
        public final static String FORWARD = "forward";
        public final static String REDIRECT = "redirect";
        public final static String URL_PARAM = "(?<=\\{)[\\w]+(?=[:\\}])";
        public final static String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        public final static String DOMAIN = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(/.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+/.?";
        public final static String INTERNET_UTL = "^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
        public final static String MOBILE_PHONE = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        public final static String PHONE = "^(\\(\\d{3,4}-)|\\d{3.4}-)?\\d{7,8}$";
        public final static String CHINA_PHONE = "\\d{3}-\\d{8}|\\d{4}-\\d{7}";
        public final static String ID_CARD = "^\\d{15}|\\d{18}$";
        public final static String SHORT_ID_CARD = "^\\d{8,18}|[0-9x]{8,18}|[0-9X]{8,18}?$";
        public final static String ACCOUNT = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
        public final static String PASSWORD = "^[a-zA-Z]\\w{5,17}$";
        public final static String STRONG_PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$";
        public final static String DATE_YYYY_MM_DD = "^\\d{4}-\\d{1,2}-\\d{1,2}";
        public final static String MONTH = "^(0?[1-9]|1[0-2])$";
        public final static String DAY = "^((0?[1-9])|((1|2)[0-9])|30|31)$";
        public final static String MONEY = "^(0|-?[1-9][0-9]*)$";
        public final static String XML_FILE_NAME = "^([a-zA-Z]+-?)+[a-zA-Z0-9]+\\\\.[x|X][m|M][l|L]$";
        public final static String CHINESE_LANGUAGE = "[\\u4e00-\\u9fa5]";
        public final static String TWO_CHAR = "[^\\x00-\\xff]";
        public final static String QQ = "[1-9][0-9]{4,}";
        public final static String MAIL_NO = "[1-9]\\d{5}(?!\\d)";
        public final static String IP = "((?:(?:25[0-5]|2[0-4]\\\\d|[01]?\\\\d?\\\\d)\\\\.){3}(?:25[0-5]|2[0-4]\\\\d|[01]?\\\\d?\\\\d))";
        public final static String NUMBER = "^[0-9]*$";
        public final static String FLOAT = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
        public final static String ENGLISH_NUMBER = "^[A-Za-z0-9]+$ 或 ^[A-Za-z0-9]{4,40}$";
    }
}
