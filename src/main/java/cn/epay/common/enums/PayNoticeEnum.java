package cn.epay.common.enums;

/**
 * epay支付统计消息枚举类
 */
public enum PayNoticeEnum {
    SUCCESS("200","操作成功"),
    ERROR("500","系统内部错误"),
    PAY_FAIL("10001","【ePay个人收款支付系统】支付失败通知")
    ;


    private String code;

    private String message;

    private PayNoticeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
