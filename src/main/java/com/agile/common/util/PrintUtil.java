package com.agile.common.util;

/**
 * Created by 佟盟 on 2018/11/9
 */
public class PrintUtil {

    public static final int WHITE = 30;             // 白色
    public static final int WHITE_BACKGROUND = 40;  // 白色背景
    public static final int RED = 31;               // 红色
    public static final int RED_BACKGROUND = 41;    // 红色背景
    public static final int GREEN = 32;             // 绿色
    public static final int GREEN_BACKGROUND = 42;  // 绿色背景
    public static final int YELLOW = 33;            // 黄色
    public static final int YELLOW_BACKGROUND = 43; // 黄色背景
    public static final int BLUE = 34;              // 蓝色
    public static final int BLUE_BACKGROUND = 44;   // 蓝色背景
    public static final int MAGENTA = 35;           // 品红（洋红）
    public static final int MAGENTA_BACKGROUND = 45;// 品红背景
    public static final int CYAN = 36;              // 蓝绿
    public static final int CYAN_BACKGROUND = 46;   // 蓝绿背景
    public static final int BLACK = 37;             // 黑色
    public static final int BLACK_BACKGROUND = 47;  // 黑色背景
    public static final int BOLD = 1;       // 粗体
    public static final int ITATIC = 3;     // 斜体
    public static final int UNDERLINE = 4;  // 下划线
    public static final int REVERSE = 7;    // 反转

    public static void main(String[] args) {
        PrintUtil.write("黑色", PrintUtil.BLACK);
        PrintUtil.write("白色", PrintUtil.WHITE);
        PrintUtil.write("红色", PrintUtil.RED);
        PrintUtil.write("绿色", PrintUtil.GREEN);
        PrintUtil.write("黄色", PrintUtil.YELLOW);
        PrintUtil.write("蓝色", PrintUtil.BLUE);
        PrintUtil.write("品红", PrintUtil.MAGENTA);
        PrintUtil.write("蓝绿", PrintUtil.CYAN);
        PrintUtil.write("黑底白字", PrintUtil.WHITE, PrintUtil.BLACK_BACKGROUND);
        PrintUtil.write("白底黑字", PrintUtil.BLACK, PrintUtil.WHITE_BACKGROUND);
        PrintUtil.write("蓝底红字", PrintUtil.RED, PrintUtil.BLUE_BACKGROUND);
        PrintUtil.write("加粗倾斜", PrintUtil.BOLD, PrintUtil.ITATIC);
        PrintUtil.write("黄底白字下划线", PrintUtil.WHITE, PrintUtil.YELLOW_BACKGROUND, PrintUtil.UNDERLINE);
        PrintUtil.write("红字颜色反转", PrintUtil.RED, PrintUtil.REVERSE);
    }

    private static String FMT(String txt, int... codes) {
        StringBuilder sb = new StringBuilder();
        for (int code : codes) {
            sb.append(code).append(";");
        }
        String _code = sb.toString();
        if (_code.endsWith(";")) {
            _code = _code.substring(0, _code.length() - 1);
        }
        return (char) 27 + "[" + _code + "m" + txt + (char) 27 + "[0m";
    }

    /**
     * 打印不换行
     */
    public static void P(String txt, int... codes) {
        System.out.print(FMT(txt, codes));
    }

    /**
     * 打印并换行
     */
    public static void writeln(String txt, int... codes) {
        System.out.println(FMT(txt, codes));
    }

    /**
     * 打印并换行
     */
    public static void write(String txt, int... codes) {
        System.out.print(FMT(txt, codes));
    }

    /**
     * 默认打印红色文字
     */
    public static void write(String txt) {
        System.out.println(FMT(txt, RED));
    }

}
