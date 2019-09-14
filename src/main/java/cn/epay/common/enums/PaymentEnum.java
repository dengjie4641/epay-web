package cn.epay.common.enums;

/**
 * 支付方式枚举类
 */
public enum PaymentEnum {
    DMF("DMF","支付宝当面付"),
    ALIPAY("Alipay","支付宝支付"),
    WECHAT("Wechat","微信支付"),
    QQ("QQ","qq支付"),
    UNIONPAY("UnionPay","云闪付"),
    DIANDAN("Diandan","点单支付")
    ;
    private String value;
    private String desc;

    PaymentEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
