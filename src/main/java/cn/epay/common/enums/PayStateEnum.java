package cn.epay.common.enums;

import com.google.gson.internal.$Gson$Preconditions;

/**
 * 支付状态枚举类
 */
public enum PayStateEnum {
    AUDIT(0,"待审核"),
    CONFIRM_SHOW(1,"支付成功"),
    REBACK(2,"支付失败"),
    AIDIT_NOT_PASS(3,"审核不通过"),
    SUCCESS_SM(4,"已扫码")
    ;

    PayStateEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;
    private String desc;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
