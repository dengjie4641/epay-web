package cn.epay.constant;

/**
 * @ClassName: Constant
 * @Description: TODO
 * @Auther: 一叶知秋
 * @Date: 2019/8/27 09:17
 * @version: V1.0
 **/
public class Constant {

    /**
     * 系统关闭key
     */
    public static final String CLOSE_DMF_KEY="PAY_CLOSE_DMF_KEY";

    /**系统关闭原因**/
    public static final String CLOSE_DMF_REASON="PAY_CLOSE_DMF_REASON";

    /**
     * 系统关闭key
     */
    public static final String CLOSE_KEY="PAY_CLOSE_KEY";

    /**系统关闭原因**/
    public static final String CLOSE_REASON="PAY_CLOSE_REASON";


    /**
     * 支付宝网关
     */
    public static final String GATEWAY = "https://openapi.alipay.com/gateway.do";

    /**
     * 你的应用ID
     */
    public static final String appId = "";

    /**
     * 传输格式
     */
    public static final String FORMAT = "json";

    /**
     * 编码
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 签名方式
     */
    public static final String SIGNTYPE = "RSA2";


    /**
     * 你的私钥
     */
    public static final String privateKey = "";

    /**
     * 你的公钥(支付宝公钥)
     */
    public static final String publicKey = "";


    /**
     * 交易成功
     */
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";


}
