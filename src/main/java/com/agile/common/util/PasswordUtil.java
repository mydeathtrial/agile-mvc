package com.agile.common.util;

import com.agile.common.base.Constant;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/2/24 13:30
 * @Description TODO
 * @since 1.0
 */
public class PasswordUtil {
    /**
     * 密码强度
     */
    public enum LEVEL {
        /**
         * 密码强度
         */
        SO_EASY, EASY, ORDINARY, STRONG, SO_STRONG
    }

    private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder(Constant.NumberAbout.FOUR);

    private static final int MIN_NUM = 48;
    private static final int MAN_NUM = 57;
    private static final int MIN_CAPITAL_LETTER = 65;
    private static final int MAN_CAPITAL_LETTER = 90;
    private static final int MIN_SMALL_LETTER = 97;
    private static final int MAN_SMALL_LETTER = 122;

    /**
     * NUM 数字
     * SMALL_LETTER 小写字母
     * CAPITAL_LETTER 大写字母
     * OTHER_CHAR  特殊字符
     */
    private static final int NUM = Constant.NumberAbout.ONE;
    private static final int SMALL_LETTER = Constant.NumberAbout.TWO;
    private static final int CAPITAL_LETTER = Constant.NumberAbout.THREE;
    private static final int OTHER_CHAR = Constant.NumberAbout.FOUR;

    /**
     * 简单的密码字典
     */
    private static final String[] DICTIONARY = {"password", "iloveyou", "sunshine",
            "1314", "520", "a1b2c3", "admin"};

    /**
     * 检查字符类型，包括num、大写字母、小写字母和其他字符。
     *
     * @param c 字符
     * @return 字符类型
     */
    private static int checkCharacterType(char c) {
        if (c >= MIN_NUM && c <= MAN_NUM) {
            return NUM;
        }
        if (c >= MIN_CAPITAL_LETTER && c <= MAN_CAPITAL_LETTER) {
            return CAPITAL_LETTER;
        }
        if (c >= MIN_SMALL_LETTER && c <= MAN_SMALL_LETTER) {
            return SMALL_LETTER;
        }
        return OTHER_CHAR;
    }

    /**
     * 按不同类型计算密码的数量
     *
     * @param password 密码
     * @param type     字符类型
     * @return 数量
     */
    private static int countLetter(String password, int type) {
        int count = 0;
        if (null != password && password.length() > 0) {
            for (char c : password.toCharArray()) {
                if (checkCharacterType(c) == type) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 检查密码的强度
     *
     * @param password 密码
     * @return strength level
     */
    public static double checkPasswordStrength(String password) {
        if (StringUtil.isBlank(password)) {
            throw new IllegalArgumentException("password is empty");
        }
        double level;

        /**
         * 长度加分
         */
        level = parsingLength(password);

        /**
         * 特殊字符加分
         */
        level = parsingSpecialChar(password, level);

        /**
         * 内容长度加分
         */
        level = parsingContext(password, level);

        /**
         * 减分
         */
        level = parsingRegex(password, level);

        /**
         * 减分
         */
        level = parsingBirthday(password, level);

        /**
         * 减分
         */
        level = parsingKeyWord(password, level);

        if (level < 0) {
            level = 0;
        }

        return level;
    }

    /**
     * 处理特殊字符串
     *
     * @param password 密码
     * @param level    强度
     * @return 强度
     */
    private static double parsingSpecialChar(String password, double level) {
        int other = countLetter(password, OTHER_CHAR);
        return other + level;
    }

    /**
     * 判断包含内容种类
     *
     * @param password 密码
     * @param level    强度
     * @return 强度
     */
    private static double parsingContext(String password, double level) {
        int num = countLetter(password, NUM);
        int small = countLetter(password, SMALL_LETTER);
        int capital = countLetter(password, CAPITAL_LETTER);
        int other = countLetter(password, OTHER_CHAR);
        final double weight = 0.2;

        level += (num + small + capital + other) * weight;
        return level;
    }

    /**
     * 处理长度
     *
     * @param password 密码
     * @return 强度
     */
    private static double parsingLength(String password) {
        final double weight = 0.2;
        return password.length() * weight;
    }

    /**
     * 验证关键字
     *
     * @param password 密码
     * @param level    强度
     * @return 强度
     */
    private static double parsingKeyWord(String password, double level) {
        final double weight = 0.8;
        if (null != DICTIONARY) {
            for (String s : DICTIONARY) {
                if (password.contains(s)) {
                    level -= weight;
                    break;
                }
            }
        }
        return level;
    }

    /**
     * 验证生日
     *
     * @param password 密码
     * @param level    强度
     * @return 强度
     */
    private static double parsingBirthday(String password, double level) {
        final String regex = Constant.RegularAbout.DATE_YYYYMMDD;
        final double weight = 0.5;
        if (StringUtil.findMatchedString(regex, password)) {
            level -= weight;
        }
        return level;
    }

    /**
     * 正则匹配
     *
     * @param password 密码
     * @param level    强度
     * @return 强度
     */
    private static double parsingRegex(String password, double level) {

        /**
         * 单字符 三至多次重复 aaa bbb ccccc
         */
        final String regex1 = "(?:([\\da-zA-Z])\\1{2,})";
        if (StringUtil.findMatchedString(regex1, password)) {
            final double weight = 3.0;
            level -= weight;
        }
        /**
         * 双至多重字符段 重复 aabbcc aaabbbccc
         */
        final String regex2 = "(?:([\\da-zA-Z])\\1+){2,}";
        if (StringUtil.findMatchedString(regex2, password)) {
            final double weight = 2.5;
            level -= weight;
        }
        /**
         * 二至多字符段 重复 abab abcabc
         */
        final String regex3 = "([\\da-zA-Z]{2,})\\1+";
        if (StringUtil.findMatchedString(regex3, password)) {
            final double weight = 2.0;
            level -= weight;
        }
        /**
         * 至少三位递增顺/逆数 123 567 3456
         */
        final String regex4 = "(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){2,}+\\d";
        final String regex5 = "(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){2,}+\\d";
        if (StringUtil.findMatchedString(regex4, password)
                || StringUtil.findMatchedString(regex5, password)) {
            final double weight = 2.2;
            level -= weight;
        }
        /**
         * 至少三位递增顺字母 abc bcde
         */
        final String regex6 = "(?:a(?=b)|b(?=c)|c(?=d)|d(?=e)|e(?=f)|f(?=g)|g(?=h)|h(?=i)|i(?=j)|j(?=k)|k(?=l)|l(?=m)|m(?=n)|n(?=o)|o(?=p)|p(?=q)|q(?=r)|r(?=s)|s(?=t)|t(?=u)|u(?=v)|v(?=w)|w(?=x)|x(?=y)|y(?=z)){2,}+[a-z]";
        final String regex7 = "(?:A(?=B)|B(?=C)|C(?=D)|D(?=E)|E(?=F)|F(?=G)|G(?=H)|H(?=I)|I(?=J)|J(?=K)|K(?=L)|L(?=M)|M(?=N)|N(?=O)|O(?=P)|P(?=Q)|Q(?=R)|R(?=S)|S(?=T)|T(?=U)|U(?=V)|V(?=W)|W(?=X)|X(?=Y)|Y(?=Z)){2,}+[A-Z]";
        if (StringUtil.findMatchedString(regex6, password)
                || StringUtil.findMatchedString(regex7, password)) {
            final double weight = 2.2;
            level -= weight;
        }
        /**
         * 至少三位递增顺键盘字母 qwe rtyu
         */
        final String regex8 = "(?:q(?=w)|w(?=e)|e(?=r)|r(?=t)|t(?=y)|y(?=u)|u(?=i)|i(?=o)|o(?=p)){2,}+[a-z]";
        final String regex9 = "(?:Q(?=W)|W(?=E)|E(?=R)|R(?=T)|T(?=Y)|Y(?=U)|U(?=I)|I(?=O)|O(?=P)){2,}+[A-Z]";
        final String regex10 = "(?:a(?=s)|s(?=d)|d(?=f)|f(?=g)|g(?=h)|h(?=j)|j(?=k)|k(?=l)){2,}+[a-z]";
        final String regex11 = "(?:A(?=S)|S(?=D)|D(?=F)|F(?=G)|G(?=H)|H(?=J)|J(?=K)|K(?=L)){2,}+[A-Z]";
        final String regex12 = "(?:z(?=x)|x(?=c)|c(?=v)|v(?=b)|b(?=n)|n(?=m)){2,}+[a-z]";
        final String regex13 = "(?:Z(?=X)|X(?=C)|C(?=V)|V(?=B)|B(?=N)|N(?=M)){2,}+[A-Z]";
        if (StringUtil.findMatchedString(regex8, password)
                || StringUtil.findMatchedString(regex9, password)
                || StringUtil.findMatchedString(regex10, password)
                || StringUtil.findMatchedString(regex11, password)
                || StringUtil.findMatchedString(regex12, password)
                || StringUtil.findMatchedString(regex13, password)) {
            final double weight = 2.2;
            level -= weight;
        }

        return level;
    }

    /**
     * 获得密码强度等级，包括弱、较弱、中、较强、强
     *
     * @param password 密码
     * @return 强度
     */
    public static LEVEL getPasswordLevel(String password) {
        final double weight = 3.6;
        double level = checkPasswordStrength(password);
        if (level <= weight) {
            return LEVEL.SO_EASY;
        } else if (level <= Constant.NumberAbout.NINE * Constant.NumberAbout.THREE) {
            return LEVEL.EASY;
        } else if (level <= Constant.NumberAbout.NINE * Constant.NumberAbout.FOUR) {
            return LEVEL.ORDINARY;
        } else if (level <= Constant.NumberAbout.NINE * Constant.NumberAbout.FIVE) {
            return LEVEL.STRONG;
        } else {
            return LEVEL.SO_STRONG;
        }
    }

    /**
     * 加密
     *
     * @param clear 明文
     * @return 密文
     */
    public static String encryption(String clear) {
        return B_CRYPT_PASSWORD_ENCODER.encode(clear);
    }

    /**
     * 密码匹配
     *
     * @param cipher 预想匹配的密码明文
     * @return 是否匹配
     */
    public static boolean decryption(String clear, String cipher) {
        return B_CRYPT_PASSWORD_ENCODER.matches(clear, cipher);
    }
}
