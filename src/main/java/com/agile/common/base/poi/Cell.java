package com.agile.common.base.poi;

/**
 * Created by 佟盟 on 2018/10/17
 */
public class Cell {
    private int sort;
    private String key;
    private String showName;

    private Cell(Builder builder) {
        this.sort = builder.sort;
        this.key = builder.key;
        this.showName = builder.showName;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public static class Builder{
        private int sort;
        private String key;
        private String showName;

        public Builder setSort(int sort) {
            this.sort = sort;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setShowName(String showName) {
            this.showName = showName;
            return this;
        }
        public Cell build(){
            return new Cell(this);
        }
    }

    public static Builder builder(){
        return new Builder();
    }
}
