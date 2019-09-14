package cn.epay.controller;

import cn.epay.bean.Pay;
import cn.epay.bean.dto.Result;
import cn.epay.common.enums.PayStateEnum;
import cn.epay.utils.*;
import cn.epay.constant.Constant;
import cn.epay.service.PayService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description //阿里支付模块处理
 * @Author 一叶知秋
 * @Date 2019/8/14 9:21
 * @Param
 * @return
 **/
@SuppressWarnings("all")
@Controller
@RequestMapping("/alipay")
@Slf4j
public class AlipayController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PayService payService;


    /**
     * 生成二维码
     * @param pay
     * @param request
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "/precreate")
    @ResponseBody
    public Result<Object> getPayState(@ModelAttribute @Validated Pay pay,
                                      HttpServletRequest request) throws AlipayApiException {
        if(!EmailUtils.checkEmail(pay.getEmail())){
            return ResultUtil.error("请填写正确的通知邮箱");
        }
        if(pay.getMoney()==null){
            return ResultUtil.error("金额必填");
        }
        if(pay.getMoney().compareTo(new BigDecimal("0.10"))!=0){
            if(pay.getMoney().compareTo(new BigDecimal("10.00"))==-1){
                return ResultUtil.error("金额不能小于10元");
            }
        }
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        //判断是否关闭系统
        String isOpenDMF = valueOperations.get(Constant.CLOSE_DMF_KEY);
        String dmfReason = valueOperations.get(Constant.CLOSE_DMF_REASON);
        String msg = "";
        if(StringUtils.isNotBlank(isOpenDMF) && "closed".equalsIgnoreCase(isOpenDMF)){
            msg = dmfReason + "如有疑问请进行反馈";
            return ResultUtil.error(msg);
        }
        //防炸库验证
        String ip = IpInfoUtils.getIpAddr(request);
        if("0:0:0:0:0:0:0:1".equals(ip)){
            ip = "127.0.0.1";
        }
        ip="DMF:"+ip;
        String temp = valueOperations.get(ip);
        Long expire = redisTemplate.getExpire(ip, TimeUnit.SECONDS);
        if(StringUtils.isNotBlank(temp)){
            return ResultUtil.error("您提交的太频繁啦，服务器要炸啦！请"+expire+"秒后再试");
        }
        payService.addPay(pay);
        //记录缓存
        valueOperations.set(ip,"added", 1L, TimeUnit.MINUTES);

        AlipayClient alipayClient = new DefaultAlipayClient(Constant.GATEWAY,
                                                            Constant.appId,
                                                            Constant.privateKey,
                                                            Constant.FORMAT,
                                                            Constant.CHARSET,
                                                            Constant.publicKey,
                                                            Constant.SIGNTYPE);
        AlipayTradePrecreateRequest r = new AlipayTradePrecreateRequest();
        String tradNo = pay.getId();
        r.setBizContent("{" +
                "\"out_trade_no\":\""+tradNo+"\"," +
                "\"total_amount\":"+pay.getMoney()+"," +
                "\"subject\":\"ePay-向作者捐赠\"" +
                "}");
        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(r);
            if(!response.isSuccess()){
                return ResultUtil.error("调用支付宝接口生成二维码失败，请向作者反馈[点击菜单反馈]");
            }
            Map<String, Object> result = new HashMap<>(16);
            result.put("id", pay.getId());
            result.put("qrCode", response.getQrCode());
            return ResultUtil.success(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error("支付失败,"+e.getMessage());
        }
    }

    /**
     * 查询支付结果
     * @param outTradeNo 商户编号
     * @return
     * @throws AlipayApiException
     */
    @GetMapping(value = "/query/{outTradeNo}")
    @ResponseBody
    public Result<Object> queryPayState(@PathVariable String outTradeNo) throws AlipayApiException {

        Result<Object> result = payService.queryPayState(outTradeNo);
        return result;
    }


}
