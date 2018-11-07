package com.agile.common.properties;

/**
 * Created by 佟盟 on 2018/1/19
 */
public class JPAConfigProperty {
    private String hbm2ddl = "update";
    private String showSql = "false";
    private String formatSql = "true";
    private String generateDdl = "true";
    private String currentSessionContextClass = "thread";
    private String useSecondLevelCache = "true";
    private String useStructuredEntries = "false";
    private String useQueryCache = "true";
    private String useSqlComments = "true";

    public String getUseSqlComments() {
        return useSqlComments;
    }

    public void setUseSqlComments(String useSqlComments) {
        this.useSqlComments = useSqlComments;
    }

    public String getHbm2ddl() {
        return hbm2ddl;
    }

    public void setHbm2ddl(String hbm2ddl) {
        this.hbm2ddl = hbm2ddl;
    }

    public String getShowSql() {
        return showSql;
    }

    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }

    public String getFormatSql() {
        return formatSql;
    }

    public void setFormatSql(String formatSql) {
        this.formatSql = formatSql;
    }

    public String getGenerateDdl() {
        return generateDdl;
    }

    public void setGenerateDdl(String generateDdl) {
        this.generateDdl = generateDdl;
    }

    public String getCurrentSessionContextClass() {
        return currentSessionContextClass;
    }

    public void setCurrentSessionContextClass(String currentSessionContextClass) {
        this.currentSessionContextClass = currentSessionContextClass;
    }

    public String getUseSecondLevelCache() {
        return useSecondLevelCache;
    }

    public void setUseSecondLevelCache(String useSecondLevelCache) {
        this.useSecondLevelCache = useSecondLevelCache;
    }

    public String getUseStructuredEntries() {
        return useStructuredEntries;
    }

    public void setUseStructuredEntries(String useStructuredEntries) {
        this.useStructuredEntries = useStructuredEntries;
    }

    public String getUseQueryCache() {
        return useQueryCache;
    }

    public void setUseQueryCache(String useQueryCache) {
        this.useQueryCache = useQueryCache;
    }
}
