package com.agile.mvc.service;

import com.agile.common.util.ObjectUtil;

/**
 * Created by 佟盟 on 2018/10/17
 */
public class POITest {
    private String first1;
    private String first2;
    private String first3;
    private String first4;
    private String first5;
    private String first6;

    public POITest(Builder builder) {
        ObjectUtil.copyProperties(builder,this);
    }

    public String getFirst1() {
        return first1;
    }

    public void setFirst1(String first1) {
        this.first1 = first1;
    }

    public String getFirst2() {
        return first2;
    }

    public void setFirst2(String first2) {
        this.first2 = first2;
    }

    public String getFirst3() {
        return first3;
    }

    public void setFirst3(String first3) {
        this.first3 = first3;
    }

    public String getFirst4() {
        return first4;
    }

    public void setFirst4(String first4) {
        this.first4 = first4;
    }

    public String getFirst5() {
        return first5;
    }

    public void setFirst5(String first5) {
        this.first5 = first5;
    }

    public String getFirst6() {
        return first6;
    }

    public void setFirst6(String first6) {
        this.first6 = first6;
    }

    public static class Builder{
        private String first1;
        private String first2;
        private String first3;
        private String first4;
        private String first5;
        private String first6;

        public Builder setFirst1(String first1) {
            this.first1 = first1;
            return this;
        }

        public Builder setFirst2(String first2) {
            this.first2 = first2;
            return this;
        }

        public Builder setFirst3(String first3) {
            this.first3 = first3;
            return this;
        }

        public Builder setFirst4(String first4) {
            this.first4 = first4;
            return this;
        }

        public Builder setFirst5(String first5) {
            this.first5 = first5;
            return this;
        }

        public Builder setFirst6(String first6) {
            this.first6 = first6;
            return this;
        }

        public POITest build(){
            return new POITest(this);
        }
    }

    public static Builder builder(){
        return new Builder();
    }
}
