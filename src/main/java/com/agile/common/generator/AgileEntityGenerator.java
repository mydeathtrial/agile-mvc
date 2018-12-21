package com.agile.common.generator;

import java.util.Map;

/**
 * @author mydeathtrial on 2017/4/20
 */
public class AgileEntityGenerator {

    public static void main(String[] args) {
        try {
            for (Map<String, Object> table : AgileGenerator.getTableInfo()) {
                AgileGenerator.generateFile(AgileGenerator.tableHandle(table), "agile.generator.entity_url", "entityClassName", "Entity.ftl", "entityPackage");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


}
