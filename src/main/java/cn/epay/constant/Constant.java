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
    public static final String appId = "2017011805204999";

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
    public static final String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCDzskBkDtlj/xpLxPD3rNMe6dgvqy6JQhC61kg1HvZdtLOt2RJbIyUu2wtG6jLNGqPhsS/jf5dlsyGnzkd6Zk3b1VYcqcPEYAk5lkYImQfMqV2je7w0VPpJbLY7hxyygAqlYuF1AnkHHlDXqDmCCiKtoyWkPxsa/LgwVv3z/ztOPKAPKNQC3PWlsttfjBaYd1wQ/KggLf7iF9884LK+WFhQe1yp/C0aLKCAK4fjBIxt7736k5Df6pUYnaLJ4+HMs5Wcz9FtSsyg9OHFYmkoQyWEM7ANBElVQjDl2dDI1g7JwPRvk7zDrtdrgqDNhvA+S9DV4uCGM3j30Wr0UnHdKNNAgMBAAECggEATl7KZ1KR7n5hT009jE3DACvZxo04+GkCc3+p+/o7NX8QMvcZ+wo+wER8OonioAgnf/Va0Kal4pHCRB0yAdYeUraw4SkyqaP3NHl96fqw21fqyAX2V1uvb8YJhDB328y9spQ+ZDFCPE6tz1/ZfUzGhkuqZfAJFpJM1FRD0aHqOkWyWoS9sWvGuj+AfrTWIRCs1Ms7Fj/db0qW0S9U+cuS4A5AZaqH4I/vk8iLVwH5OVNdjZkVxCCswXDuWiVSM+VbdypK+zbtXERArt9XNk55jN39EJH+z97hswolwSLGLtdbgX0scXIQ7WhCpNOfKqsCc4hdll7hOxCJaUuQ8w6p4QKBgQC+p3uqx7daKIfLQp4ZqbuxOtqefxCycMqYhIw5kVdTm4gZ/V8tixXz+mx7Fj7i6uLVlTuVkanngzc0JGSIlzGlBuZklcKTTqg8he5kcqb17QkausJCtkCJc7rcNGqnOVSXsImSJ1h4i1isUJjYIBdVU+oc5SJS32MdfTks6hgXCQKBgQCw+/QTKIbRLvyYGkjm75FreK2EFFTYVBnt3TynidGWZBLXiddXE6UCNkxaTLDnb3sa7XMDlLIWqIf3GXVxlNcZmxb6/KsycKlK+RXQYGt+tVTe80keMnGw11Xi3aEkx7xsxJonXw2QE+W/w2AD6EsVV1Wdw+yl+8TBUmmBhwuXJQKBgQCReE9W1n4jD6vVPmZNpIGuz/5BUyiVTpR23SP1RN2B0Ll/QyFdKbO9bgVZwXaIQ7qCRxSoofUmzarQYThINDzP8zzV4KMPLMQXMgXcpNKZ8Juyxm804cTKXABmxqPeJlNSToQZfWX2zDQDfpewyIOlOpr2IysWWpZQyNxYVeG9qQKBgQCKcTw2tE/pV+jtDjqSszm8qNSKaeEIq5jw0Uu9/ne9PaDji4awLcS420KUrEMBdtoEcVOzrGA2izR4XSjaZURxWjj6siU0up4i8H9Y1eOgZTXzhLlgbrMyUu9zHE0OWVOSfaPcQmV4m6fvfmQgCliZZAyr8XOvTydU1iOD1+CAJQKBgDVqivTTp0lJf/ubA/lGnnfTCO7NiqqIjcMUbu/jnT2usUihOvyYzW1CoDRwTAlMcoP9T94QlDGLGkLwblVK/rV2MvckTHqNLfkPgJChFkSPiB53yDwA7uTZoyO3BK5OuiTODErxNVa7TCx9zxXtdKUP595gbKTmS+B0kMvfRgJr";

    /**
     * 你的公钥(支付宝公钥)
     */
    public static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArljWPfkRrjkKzK+9KydhaKknU6tFNATOOb3tSA/oxQ68QVPVHU5zpRQleS64LNdMyYl1H1Mu9yjRxV/XUwghq1rdTX55lvM0Bejl/TLo23LmdSOPZKgjKJRV+riOrhiT1pMpeiSxkSvqUxT2oGtdC0//nuGjpeSw3xyF4WHJ3gZM9mVBDadrfZnE43wtarBs2uxt8Ul9turq1Z2d8AWWb6Jlk370de5chx6L4ofjpnQwDVBmDnFJwewrSMX7Kl3Lm5Hi7mJSc9xriLHZ1fd2Yaa4jBeqhW+uvY5OLB6f1L9tmQQMc7Xw70AdMYRAcHTWMDBjIjaTeQ2dMEAyTZdLlwIDAQAB";


    /**
     * 交易成功
     */
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";


}
