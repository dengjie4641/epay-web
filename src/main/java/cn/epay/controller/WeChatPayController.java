package cn.epay.controller;

import cn.epay.bean.Pay;
import cn.epay.bean.dto.Result;
import cn.epay.common.enums.PaymentEnum;
import cn.epay.constant.PayConstant;
import cn.epay.service.BaseService;
import cn.epay.service.PayService;
import cn.epay.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Controller
@RequestMapping("/pay")
@Api(tags = "开放接口", description = "支付操作管理")
public class WeChatPayController {

    @Autowired
    private BaseService baseService;

    @Autowired
    private PayService payService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailUtils emailUtils;

    @Value("${ip.expire}")
    private Long ip_expire;

    @Value("${my.token}")
    private String my_token;

    @Value("${email.sender}")
    private String email_sender;

    @Value("${email.receiver}")
    private String email_receiver;

    @Value("${token.admin.expire}")
    private Long admin_expire;

    @Value("${token.fake.expire}")
    private Long fake_expire;

    @Value("${fake.pre}")
    private String fake_pre;

    @Value("${server.url}")
    private String server_url;

    @Value("${qrnum}")
    private Integer qr_num;


    /**
     * @return cn.epay.bean.dto.Result<java.lang.Object>
     * @Description //添加支付订单
     * @Author 一叶知秋
     * @Date 2019/8/14 9:27
     * @Param [pay, request]
     **/
    @ResponseBody
    @PostMapping(value = "/addPay")
    @ApiOperation(value = "添加支付订单")
    public Result<Object> addPay(@ModelAttribute @Validated Pay pay, HttpServletRequest request) {
        BigDecimal money = pay.getMoney();
        if (!EmailUtils.checkEmail(pay.getEmail())) {
            return ResultUtil.error("请填写正确的通知邮箱");
        }
        if(money.compareTo(new BigDecimal(1.00)) == -1){
            return ResultUtil.error("输入金额不小于1.00");
        }
        if (null == pay.getCustom()) {
            return ResultUtil.error("自定义金额参数必填");
        }
        ValueOperations<String, String> valueops = redisTemplate.opsForValue();

        // 判断是否开启支付
        String isOpen = valueops.get(PayConstant.CLOSE_KEY);
        String allReason = valueops.get(PayConstant.CLOSE_REASON);

        // 判断是否开启当面付
        String isOpenDMF = valueops.get(PayConstant.CLOSE_DMF_KEY);
        String dmfReason = valueops.get(PayConstant.CLOSE_DMF_REASON);

        Long expireOpen = redisTemplate.getExpire(PayConstant.CLOSE_KEY, TimeUnit.HOURS);

        String msg = "";
        if (StringUtils.isNotBlank(isOpen)) {
            if (expireOpen < 0) {
                msg = allReason + "系统暂时关闭，如有疑问请进行反馈";
            } else {
                msg = "暂停支付测试，剩余" + expireOpen + "小时后开放，早点休息吧";
            }
            return ResultUtil.error(msg);
        }
        if (money.compareTo(new BigDecimal(68.00)) == 0) {
            if (StringUtils.isNotBlank(isOpenDMF)) {
                msg = dmfReason + "如有疑问请进行反馈";
                return ResultUtil.error(msg);
            }
        }
        //防炸库验证
        String ip = IpInfoUtils.getIpAddr(request);
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        String temp = valueops.get(ip);
        Long expire = redisTemplate.getExpire(ip, TimeUnit.SECONDS);
        if (StringUtils.isNotBlank(temp)) {
            return ResultUtil.error("您提交的太频繁啦，服务器要炸啦！请" + expire + "秒后再试");
        }

        try {
            //unionPay 云闪付
            String payType = pay.getPayType();
            if (pay.getCustom() && !PaymentEnum.UNIONPAY.getValue().equals(payType)
                                && !PaymentEnum.DIANDAN.getValue().equals(payType)) {
                //自定义金额生成四位数随机标识
                pay.setPayNum(StringUtils.getRandomNum());
            } else {
                // 从redis中取出num
                String key = "PAY_NUM_" + pay.getPayType();
                String value = valueops.get(key);
                // 初始化
                if (StringUtils.isBlank(value)) {
                    valueops.set(key, "0");
                }
                // 取出num
                int num = Integer.parseInt(valueops.get(key)) + 1;
                if (qr_num.intValue() == num) {
                    valueops.set(key, "0");
                } else {
                    // 更新记录num
                    valueops.set(key, String.valueOf(num));
                }
                pay.setPayNum(String.valueOf(num));
            }
            payService.addPay(pay);
            pay.setTime(StringUtils.getTimeStamp(DateUtils.getCurrentDate()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResultUtil.error("添加捐赠支付订单失败");
        }
        //记录缓存
        valueops.set(ip, "added", ip_expire, TimeUnit.MINUTES);

        //给管理员发送审核邮件
        String tokenAdmin = UUID.randomUUID().toString();
        valueops.set(pay.getId(), tokenAdmin, admin_expire, TimeUnit.DAYS);
        pay = baseService.getAdminUrl(pay,server_url, pay.getId(), tokenAdmin, my_token);

        //异步更新订单表
        this.updatePay(pay);

        emailUtils.sendTemplateMail(email_sender, email_receiver, "【ePay个人收款支付系统】待审核处理", "email-admin", pay);

        //给假管理员发送审核邮件
        if (StringUtils.isNotBlank(pay.getTestEmail()) && EmailUtils.checkEmail(pay.getTestEmail())) {
            Pay pay2 = payService.getPay(pay.getId());
            String tokenFake = UUID.randomUUID().toString();
            valueops.set(fake_pre + pay.getId(), tokenFake, fake_expire, TimeUnit.HOURS);
            pay2 = baseService.getAdminUrl(pay2,server_url, fake_pre + pay.getId(), tokenFake, my_token);
            emailUtils.sendTemplateMail(email_sender, pay.getTestEmail(), "【ePay个人收款支付系统】待审核处理", "email-fake", pay2);
        }

        Pay p = new Pay();
        p.setId(pay.getId());
        p.setPayNum(pay.getPayNum());
        return ResultUtil.success(p);
    }

    /**
     * @Description //异步更新订单信息
     * @Author 一叶知秋
     * @Date 2019/8/27 19:50
     * @Param [pay]
     * @return void
     **/
    @Async
    void updatePay(Pay pay){
        payService.updatePay(pay);
    }



}
