package cn.epay.common.enums;

/**
 * @Description //支付宝支付状态
 * @Author 一叶知秋
 * @Date 2019/8/23 18:03
 * @Param
 * @return
 **/
public enum AlipayStatusEnum {

    SUCCESS("10000","成功"),
    PAYING("10003","用户支付中"),
    FAILED("40004","失败"),
    ERROR("20000","系统异常"),
    openAlipay("openAlipay","已经支付"),
    notAlipay("notAlipay","未支付");

    private String value;
    private String desc;

    private AlipayStatusEnum(String value, String desc) {
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
