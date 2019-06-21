package com.agile.common.container;

import com.agile.common.util.PrintUtil;
import com.agile.common.util.PropertiesUtil;
import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * @author 佟盟 on 2018/11/21
 */
public class AgileBanner implements Banner {

    private static final String[] BANNER = {"",
            "  █████╗     ██████╗  ██╗ ██╗          ███████╗",
            "██╔----██╗ ██╔--------╝  ██║ ██║          ██╔--------╝",
            "███████║ ██║   ███╗ ██║ ██║          █████╗  ",
            "██╔----██║ ██║     ██║ ██║ ██║          ██╔----╝  ",
            "██║    ██║╚██████╔╝ ██║ ███████╗███████╗",
            "╚--╝    ╚--╝ ╚----------╝    ╚--╝ ╚------------╝╚------------╝"};

    private static final String AGILE_FRAMEWORK = " :: 敏捷开发框架 Agile Framework :: ";

    private static final int STRAP_LINE_SIZE = 42;

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
        for (String line : BANNER) {
            PrintUtil.writeln(line, PrintUtil.CYAN);
        }
        String version = PropertiesUtil.getProperty("agile.version");
        version = (version != null) ? " (version:" + version + ")" : "";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE
                - (version.length() + AGILE_FRAMEWORK.length())) {
            padding.append(" ");
        }

        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, AGILE_FRAMEWORK, AnsiColor.DEFAULT, padding.toString(), AnsiStyle.FAINT, version));
        printStream.println();
    }

}