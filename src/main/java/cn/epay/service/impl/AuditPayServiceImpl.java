package cn.epay.service.impl;

import cn.epay.bean.Pay;
import cn.epay.common.enums.PayStateEnum;
import cn.epay.service.AuditPayService;
import cn.epay.service.BaseService;
import cn.epay.service.PayService;
import cn.epay.utils.EmailUtils;
import cn.epay.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuditPayServiceImpl implements AuditPayService {

    @Autowired
    private BaseService baseService;

    @Autowired
    private PayService payService;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public String passPay(String id, String token, String adminToken,
                          String sendType, Model model) {
        //二次校验token
        if (!adminToken.equals(baseService.myToken)) {
            model.addAttribute("errorMsg", "您未通过二次验证，当我傻吗");
            return "500";
        }
        try {
            payService.changePayState(payService.getPayId(id), PayStateEnum.CONFIRM_SHOW.getValue());
            //通知回调
            Pay pay = payService.getPay(payService.getPayId(id));
            if (StringUtils.isNotBlank(pay.getEmail()) && EmailUtils.checkEmail(pay.getEmail())) {
                if ("0".equals(sendType)) {
                    emailUtils.sendTemplateMail(baseService.emailSender, pay.getEmail(), "【ePay个人收款支付系统】支付成功通知", "pay-success", pay);
                } else if ("1".equals(sendType)) {
                    emailUtils.sendTemplateMail(baseService.emailSender, pay.getEmail(), "【ePay个人收款支付系统】支付成功通知", "sendwxcode", pay);
                } else if ("2".equals(sendType)) {
                    emailUtils.sendTemplateMail(baseService.emailSender, pay.getEmail(), "【ePay个人收款支付系统】支付成功通知", "sendxboot", pay);
                }
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", "处理数据出错");
            return "500";
        }
        return "redirect:/success";
    }

    @Override
    public String passNotShow(String id, Model model) {
        try {
            Pay pay = payService.getPay(payService.getPayId(id));
            if (id.contains(baseService.fakePre) && pay.getState() == 1) {
                model.addAttribute("errorMsg", "对于已成功支付的订单您无权进行该操作");
                return "500";
            }
            payService.changePayState(payService.getPayId(id), PayStateEnum.AIDIT_NOT_PASS.getValue());
            //通知回调
            if (StringUtils.isNotBlank(pay.getEmail()) && EmailUtils.checkEmail(pay.getEmail())) {
                emailUtils.sendTemplateMail(baseService.emailSender, pay.getEmail(), "【ePay个人收款支付系统】支付成功通知", "pay-notshow", pay);
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", "处理数据出错");
            return "500";
        }
        if (id.contains(baseService.fakePre)) {
            redisTemplate.opsForValue().set(id, "", 1L, TimeUnit.SECONDS);
        }
        return "redirect:/success";
    }

    @Override
    public String backPay(String id, String adminToken, Model model) {
        if (!baseService.myToken.equals(adminToken)) {
            model.addAttribute("errorMsg", "您未通过二次验证，当我傻吗");
            return "500";
        }
        try {
            payService.changePayState(payService.getPayId(id), PayStateEnum.REBACK.getValue());
            //通知回调
            Pay pay = payService.getPay(payService.getPayId(id));
            if (StringUtils.isNotBlank(pay.getEmail()) && EmailUtils.checkEmail(pay.getEmail())) {
                emailUtils.sendTemplateMail(baseService.emailSender, pay.getEmail(), "【ePay个人收款支付系统】支付失败通知", "pay-fail", pay);
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", "处理数据出错");
            return "500";
        }
        if (id.contains(baseService.fakePre)) {
            redisTemplate.opsForValue().set(id, "", 1L, TimeUnit.SECONDS);
        }
        return "redirect:/success";
    }
}
