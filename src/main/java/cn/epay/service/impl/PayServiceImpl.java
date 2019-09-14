package cn.epay.service.impl;

import cn.epay.bean.dto.Result;
import cn.epay.common.enums.DateFlagEnum;
import cn.epay.bean.Pay;
import cn.epay.bean.dto.Count;
import cn.epay.common.enums.PayStateEnum;
import cn.epay.common.enums.PaymentEnum;
import cn.epay.constant.PayConstant;
import cn.epay.service.BaseService;
import cn.epay.utils.*;
import cn.epay.dao.PayDao;
import cn.epay.service.PayService;
import cn.epay.vo.respVo.PayRespVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class PayServiceImpl extends BaseService implements PayService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PayDao payDao;

    @Autowired
    private EmailUtils emailUtils;



    @Override
    public List<PayRespVo> getPayList(Integer state) {
        List<PayRespVo> pays = new ArrayList<>();
        List<Pay> list = payDao.getByStateIs(state);
        list.stream().forEach(item -> {
            PayRespVo payRespVo = new PayRespVo();
            CopyBeanUtil.copyProperties(item,payRespVo);
            String payType = item.getPayType();
            switch (payType.toLowerCase()){
                case "dmf":
                    payRespVo.setPayType(PaymentEnum.DMF.getDesc());
                    break;
                case "qq":
                    payRespVo.setPayType(PaymentEnum.QQ.getDesc());
                    break;
                case "alipay":
                    payRespVo.setPayType(PaymentEnum.ALIPAY.getDesc());
                    break;
                case "wechat":
                    payRespVo.setPayType(PaymentEnum.WECHAT.getDesc());
                    break;
                case "diandan":
                    payRespVo.setPayType(PaymentEnum.DIANDAN.getDesc());
                    break;
                default:
                    payRespVo.setPayType(PaymentEnum.UNIONPAY.getDesc());
                    break;
            }
            pays.add(payRespVo);
        });
        return pays;
    }

    @Override
    public Pay getPay(String id) {
        Optional<Pay> optional = payDao.findById(id);
        Pay pay= optional.get();
        pay.setTime(StringUtils.getTimeStamp(pay.getCreateTime()));
        return pay;
    }

    @Override
    public int addPay(Pay pay) {
        pay.setId(String.valueOf(SnowflakeIdWorkerUtil.getId()));
        pay.setCreateTime(DateUtils.getCurrentDate());
        pay.setUpdateTime(DateUtils.getCurrentDate());
        pay.setState(PayStateEnum.AUDIT.getValue());
        payDao.save(pay);
        return 1;
    }

    @Override
    public int updatePay(Pay pay) {
        pay.setUpdateTime(DateUtils.getCurrentDate());
        payDao.saveAndFlush(pay);
        return 1;
    }

    @Override
    public int changePayState(String id, Integer state) {
        Pay pay=getPay(id);
        pay.setState(state);
        pay.setUpdateTime(DateUtils.getCurrentDate());
        payDao.saveAndFlush(pay);
        return 1;
    }

    @Override
    public int deletePayById(String id) {
        payDao.deleteById(id);
        return 1;
    }

    @Override
    public Count statistic(Integer type, String start, String end) {
        Count count=new Count();
        if(type== DateFlagEnum.TY_ALL.getValue()){
            //所有支付方式汇总
            count.setAmount(payDao.countAllMoney());
            count.setDmf(payDao.countAllMoneyByType(PaymentEnum.DMF.getValue()));
            count.setAlipay(payDao.countAllMoneyByType(PaymentEnum.ALIPAY.getValue()));
            count.setWechat(payDao.countAllMoneyByType(PaymentEnum.WECHAT.getValue()));
            count.setQq(payDao.countAllMoneyByType(PaymentEnum.QQ.getValue()));
            count.setUnion(payDao.countAllMoneyByType(PaymentEnum.UNIONPAY.getValue()));
            count.setDiandan(payDao.countAllMoneyByType(PaymentEnum.DIANDAN.getValue()));
            return count;
        }
        Date startDate=null,endDate=null;
        if(type== DateFlagEnum.TO_DAY.getValue()){
            // 今天
            startDate = DateUtils.getDayBegin();
            endDate = DateUtils.getDayEnd();
        }if(type== DateFlagEnum.TO_YES.getValue()){
            // 昨天
            startDate = DateUtils.getBeginDayOfYesterday();
            endDate = DateUtils.getEndDayOfYesterDay();
        }else if(type==DateFlagEnum.TO_WEEK.getValue()){
            // 本周
            startDate = DateUtils.getBeginDayOfWeek();
            endDate = DateUtils.getEndDayOfWeek();
        }else if(type==DateFlagEnum.TO_MONTH.getValue()){
            // 本月
            startDate = DateUtils.getBeginDayOfMonth();
            endDate = DateUtils.getEndDayOfMonth();
        }else if(type==DateFlagEnum.TO_YEAR.getValue()){
            // 本年
            startDate = DateUtils.getBeginDayOfYear();
            endDate = DateUtils.getEndDayOfYear();
        }else if(type==DateFlagEnum.PRE_WEEK.getValue()){
            // 上周
            startDate = DateUtils.getBeginDayOfLastWeek();
            endDate = DateUtils.getEndDayOfLastWeek();
        }else if(type==DateFlagEnum.PRE_MONTH.getValue()){
            // 上个月
            startDate = DateUtils.getBeginDayOfLastMonth();
            endDate = DateUtils.getEndDayOfLastMonth();
        }else if(type==DateFlagEnum.USER_DEFINED.getValue()){
            // 自定义
            startDate = DateUtils.parseStartDate(start);
            endDate = DateUtils.parseEndDate(end);
        }
        count.setAmount(payDao.countMoney(startDate, endDate));
        count.setDmf(payDao.countMoneyByType(PaymentEnum.DMF.getValue(), startDate, endDate));
        count.setAlipay(payDao.countMoneyByType(PaymentEnum.ALIPAY.getValue(), startDate, endDate));
        count.setWechat(payDao.countMoneyByType(PaymentEnum.WECHAT.getValue(), startDate, endDate));
        count.setQq(payDao.countMoneyByType(PaymentEnum.QQ.getValue(), startDate, endDate));
        count.setUnion(payDao.countMoneyByType(PaymentEnum.UNIONPAY.getValue(), startDate, endDate));
        count.setDiandan(payDao.countMoneyByType(PaymentEnum.DIANDAN.getValue(), startDate, endDate));
        return count;
    }

    @Override
    public Result<Object> queryPayState(String outTradeNo) {
        try{
            AlipayClient alipayClient = new DefaultAlipayClient(PayConstant.GATEWAY,
                    PayConstant.appId,
                    PayConstant.privateKey,
                    PayConstant.FORMAT,
                    PayConstant.CHARSET,
                    PayConstant.publicKey,
                    PayConstant.SIGNTYPE);
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{\"out_trade_no\":\""+outTradeNo+"\" }");
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response!=null && response.isSuccess()){
                this.sendActiveEmail(outTradeNo);
                return ResultUtil.success(1);
            }else{
                return ResultUtil.success(0);
            }
        }catch (AlipayApiException e){
            return ResultUtil.error(e.getErrMsg());
        }
    }

    /**
     * 获得假管理ID
     * @param payId
     * @return
     */
    @Override
    public String getPayId(String payId) {
        if (payId.contains(fakePre)) {
            String realId = payId.substring(payId.indexOf("-", 0) + 1, payId.length());
            return realId;
        }
        return payId;
    }

    @Override
    public void editPay(Pay pay) {
        String id = pay.getId();
        Pay p = this.getPay(id);
        pay.setState(p.getState());
        if (!pay.getId().contains(fakePre)) {
            pay.setCreateTime(StringUtils.getDate(pay.getTime()));
        } else {
            //假管理
            pay.setMoney(p.getMoney());
            pay.setPayType(p.getPayType());
        }
        this.updatePay(pay);

        if (id.contains(fakePre)) {
            redisTemplate.opsForValue().set(id, "", 1L, TimeUnit.SECONDS);
        }
    }

    @Override
    public Result<Object> delPayOrder(String id) {
        try {
            //通知回调
            String payId = this.getPayId(id);
            Pay pay = this.getPay(payId);
            if (id.contains(fakePre) && pay.getState() == 1) {
                return ResultUtil.error("您无权删除已成功支付的订单");
            }
            if (StringUtils.isNotBlank(pay.getEmail()) && EmailUtils.checkEmail(pay.getEmail())) {
                emailUtils.sendTemplateMail(emailSender, pay.getEmail(), "【ePay个人收款支付系统】支付失败通知", "pay-fail", pay);
            }
            this.deletePayById(payId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error("删除支付订单失败");
        }
        if (id.contains(fakePre)) {
            redisTemplate.opsForValue().set(id, "", 1L, TimeUnit.SECONDS);
        }
        return ResultUtil.success();
    }


    @Async
    public void sendActiveEmail(String id){
        Pay pay = this.getPay(id);
        /**
         * 确认显示
         */
        if(pay.getState()== PayStateEnum.CONFIRM_SHOW.getValue()){
            return;
        }
        String token = UUID.randomUUID().toString();
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        opsForValue.set("dmf:"+pay.getId(), token, adminExpire, TimeUnit.DAYS);
        opsForValue.set(pay.getId(), token, adminExpire, TimeUnit.DAYS);
        pay = this.getAdminUrl(pay,serverUrl,pay.getId(), token, myToken);

        if(pay.getMoney().compareTo(new BigDecimal("0.99"))==1){
            emailUtils.sendTemplateMail(emailSender,emailReceiver,"【epay支付系统】当面付收款"+pay.getMoney()+"元","email-admin",pay);
        }
        if(pay.getMoney().compareTo(new BigDecimal("9.99"))==1&&pay.getMoney().compareTo(new BigDecimal("68.00"))==-1){
            // 发送epay
            emailUtils.sendTemplateMail(emailSender, pay.getEmail(),"【epay个人收款支付系统】支付成功通知","pay-success", pay);
        }else if(pay.getMoney().compareTo(new BigDecimal("67.99"))==1&&pay.getMoney().compareTo(new BigDecimal("198.00"))==-1){
            // 发送小程序
            emailUtils.sendTemplateMail(emailSender, pay.getEmail(),"【epay个人收款支付系统】支付成功通知","sendwxcode", pay);
        }else if(pay.getMoney().compareTo(new BigDecimal("198.00"))==0||pay.getMoney().compareTo(new BigDecimal("198.00"))==1){
            // 发送xboot
            emailUtils.sendTemplateMail(emailSender, pay.getEmail(),"【epay个人收款支付系统】支付成功通知","sendxboot", pay);
        }
        pay.setState(PayStateEnum.CONFIRM_SHOW.getValue());

        /**
         * 更新数据
         */
        this.updatePay(pay);
        redisTemplate.delete("epay:"+pay.getId());
    }


}
