package cn.epay.common.enums;

/**
 * @Description //日期标志枚举类
 * @Author 一叶知秋
 * @Date 2019/8/23 17:53
 * @Param
 * @return
 **/
public enum DateFlagEnum {
    TY_ALL(-1,"全部"),
    TO_DAY(0,"今天"),
    TO_YES(6,"昨天"),
    TO_WEEK(1,"本周"),
    TO_MONTH(2,"本月"),
    TO_YEAR(3,"本年"),
    PRE_WEEK(4,"上周"),
    PRE_MONTH(5,"上个月"),
    USER_DEFINED(-2,"自定义")
    ;

    private int value;
    private String des;

    private DateFlagEnum(int value,String des) {
        this.value = value;
        this.des = des;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
