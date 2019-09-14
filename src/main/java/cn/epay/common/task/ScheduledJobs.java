package cn.epay.common.task;

import cn.epay.bean.Pay;
import cn.epay.bean.dto.Result;
import cn.epay.common.enums.PayNoticeEnum;
import cn.epay.common.enums.PayStateEnum;
import cn.epay.common.enums.PaymentEnum;
import cn.epay.utils.DateUtils;
import cn.epay.utils.EmailUtils;
import cn.epay.utils.StringUtils;
import cn.epay.dao.PayDao;
import cn.epay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 一叶知秋
 */
@Slf4j
@Component
public class ScheduledJobs {


    @Autowired
    private PayService payService;

    @Autowired
    private PayDao payDao;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${email.sender}")
    private String sender;


    /**
     * 每日凌晨清空除捐赠和审核中以外的数据
     */
    @Transactional
    @Scheduled(cron="0 0 3 * * ?")
    public void cronJob(){
        //获取所有状态为失败的订单
        List<Pay> list=payDao.getByStateIs(PayStateEnum.REBACK.getValue());
        //删除七天之前的失败订单
        list.stream().filter(e->(System.currentTimeMillis()-e.getCreateTime().getTime()) >= 7*24*3600*1000)
                .forEach(item->{
                    payService.deletePayById(item.getId());
                });

        log.info("--------->定时执行删除七天之前的失败订单完毕.........");

        //设置未审核或已扫码数据为支付失败
        List<Pay> dd = payDao.getByStateIs(PayStateEnum.AUDIT.getValue());
        //dd.addAll(payDao.getByStateIs(PayStateEnum.AIDIT_NOT_PASS.getValue()));
        dd.addAll(payDao.getByStateIs(PayStateEnum.SUCCESS_SM.getValue()));
        dd.stream().forEach(e->{
            e.setState(PayStateEnum.REBACK.getValue());
            e.setUpdateTime(DateUtils.getCurrentDate());
            payService.updatePay(e);
        });
        log.info("--------->定时执行设置未审核数据为支付失败完毕...........");
    }

    /**
     * 每半小时检查是否漏单
     */
    @Scheduled(cron="0 0/30 * * * ?")
    public void checkJob() {
        // 检查是否DMF漏单
        List<Pay> dmf = payDao.getByStateAndPayType(PayStateEnum.AUDIT.getValue(), PaymentEnum.DMF.getValue());
        dmf.forEach(e -> {
            Result<Object> result = payService.queryPayState(e.getId());
            String data = result.getResult().toString();
            if("1".equals(data)){
                log.info("-------->发现漏发订单,并已经补发订单成功.....");
            }else{
                log.info("-------->查询支付宝当面付订单失败,请检查程序....");
            }
        });
    }

    /**
     * 每日9：30am统一发送支付失败邮件
     */
    @Scheduled(cron="0 30 9 * * ?")
    public void sendEmailJob(){
        log.info("------>开始执行....");
        /**获取失败结果集**/
        List<Pay> list = payDao.getByStateIs(PayStateEnum.REBACK.getValue());
        /**只发送昨天**/
        list.stream().filter(e->(StringUtils.isNotBlank(e.getEmail()) && EmailUtils.checkEmail(e.getEmail())))
                     .filter(e->e.getCreateTime().compareTo(DateUtils.getFrontDatBegin(DateUtils.getCurrentDate(),1))!=-1)
                     .filter(e->e.getCreateTime().compareTo(DateUtils.getFrontDatBegin(DateUtils.getCurrentDate(),0))==-1)
                     .forEach(item->{
                         item.setTime(StringUtils.getTimeStamp(item.getCreateTime()));
                         emailUtils.sendTemplateMail(sender, item.getEmail(),
                         PayNoticeEnum.PAY_FAIL.getMessage(), "pay-fail", item);
                     });
        log.info("--------->定时执行统一发送支付失败邮件完毕");
    }

    /**
     * 每日2am关闭系统4小时
     */
    @Scheduled(cron="0 0 2 * * ?")
    public void close(){
        log.info("------->每天2点关闭系统----->");
        redisTemplate.opsForValue().set(Constant.CLOSE_KEY, "CLOSED", 4L, TimeUnit.HOURS);
        log.info("------->定时关闭系统成功------>");
    }
}
