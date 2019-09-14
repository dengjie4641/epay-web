package cn.epay.service;

import org.springframework.ui.Model;

/**
 * 审核支付接口类
 */
public interface AuditPayService {
    String passPay(String id, String token, String myToken, String sendType,Model model);

    String passNotShow(String id, Model model);

    String backPay(String id, String myToken, Model model);
}
