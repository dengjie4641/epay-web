package cn.epay.service;

import cn.epay.bean.Pay;
import cn.epay.vo.reqVo.FailOrderListReqVo;
import cn.epay.vo.respVo.PayRespVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DonateService {

    /**
     * 分页获取支付列表
     * @param state
     * @param key
     * @param pageable
     * @return
     */
    Page<Pay> getPayListByPage(Integer state, String key, Pageable pageable);

    List<PayRespVo> getFailOrderList(FailOrderListReqVo failOrderListReqVo);
}
