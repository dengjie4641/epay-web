package cn.epay.service;

import cn.epay.bean.Pay;
import cn.epay.bean.dto.Count;
import cn.epay.bean.dto.Result;
import cn.epay.vo.respVo.PayRespVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description //支付接口类
 * @Author 一叶知秋
 * @Date 2019/8/26 21:24
 * @Param
 * @return
 **/
public interface PayService {


    /**
     * 获得支付表
     * @param state
     * @return
     */
    List<PayRespVo> getPayList(Integer state);

    /**
     * 获得支付
     * @param id
     * @return
     */
    Pay getPay(String id);

    /**
     * 添加支付
     * @param pay
     * @return
     */
    int addPay(Pay pay);

    /**
     * 编辑支付
     * @param pay
     * @return
     */
    int updatePay(Pay pay);

    /**
     * 状态改变
     * @param id
     * @param state
     * @return
     */
    int changePayState(String id,Integer state);

    /**
     * 删除除捐赠和审核中以外的数据支付
     * @param id
     * @return
     */
    int deletePayById(String id);

    /**
     * 统计数据
     * @param type
     * @param start
     * @param end
     * @return
     */
    Count statistic(Integer type, String start, String end);

    Result<Object> queryPayState(String outTradeNo);

    String getPayId(String payId);

    void editPay(Pay pay);

    Result<Object> delPayOrder(String id);
}
