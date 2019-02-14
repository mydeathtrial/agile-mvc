package com.agile.common.generator;

/**
 * @author mydeathtrial on 2017/4/20
 */
public class AgileEntityGenerator {

    public static void main(String[] args) {
        try {
            AgileGenerator.init();
            AgileGenerator.generator(AgileGenerator.TYPE.ENTITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


}
