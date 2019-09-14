package cn.epay.controller;

import cn.epay.bean.Pay;
import cn.epay.bean.dto.DataTablesResult;
import cn.epay.bean.dto.PageVo;
import cn.epay.common.enums.PaymentEnum;
import cn.epay.utils.PageUtil;
import cn.epay.service.DonateService;
import cn.epay.utils.CopyBeanUtil;
import cn.epay.vo.reqVo.AuditOrderListReqVo;
import cn.epay.vo.reqVo.FailOrderListReqVo;
import cn.epay.vo.respVo.PayRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DonateController
 * @Description: 捐赠处理类
 * @Auther: 一叶知秋
 * @Date: 2019/8/27 13:52
 * @version: V1.0
 **/
@Slf4j
@Controller
@RequestMapping("/thanks")
public class DonateController {

    @Autowired
    private DonateService donateService;

    /**
     * @Description 获取捐赠列表
     * @Author 一叶知秋
     * @Date 2019/8/14 9:22
     * @Param [draw, start, length, search, orderCol, orderDir]
     * @return cn.epay.bean.dto.DataTablesResult
     **/
    @ResponseBody
    @GetMapping(value = "/list")
    public DataTablesResult getThanksList(int draw, int start, int length,
                                          @RequestParam("search[value]") String search,
                                          @RequestParam("order[0][column]") int orderCol,
                                          @RequestParam("order[0][dir]") String orderDir){

        DataTablesResult result = new DataTablesResult();
        //获取客户端需要排序的列
        //String[] cols = {"nickName","payType", "money", "info", "state", "createTime"};
        //String orderColumn = cols[orderCol];

        //以创建时间排序
        String orderColumn = "createTime";
        //获取排序方式 默认为desc(asc)
        orderDir = "desc";
        //限制只能查看前面30条
        if(length>30){
            result.setDraw(draw);
            result.setSuccess(false);
            result.setError("看我那么多数据干嘛");
            return result;
        }
        PageVo pageVo = new PageVo();
        int page = start/length + 1;
        pageVo.setPageNumber(page);
        pageVo.setPageSize(length);
        pageVo.setSort(orderColumn);
        pageVo.setOrder(orderDir);
        Pageable pageable = PageUtil.initPage(pageVo);

        Page<Pay> payPage = null;
        try {
            payPage = donateService.getPayListByPage(1,search,pageable);
        }catch (Exception e){
            log.error(e.toString());
            result.setSuccess(false);
            result.setDraw(draw);
            result.setError("获取捐赠列表失败");
            return result;
        }

        List<PayRespVo> list = new ArrayList<>();
        payPage.getContent().stream().forEach(e -> {
            PayRespVo payRespVo = new PayRespVo();
            CopyBeanUtil.copyProperties(e,payRespVo);
            String payType = e.getPayType();
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
            list.add(payRespVo);
        });
        result.setRecordsFiltered(Math.toIntExact(payPage.getTotalElements()));
        result.setRecordsTotal(Math.toIntExact(payPage.getTotalElements()));
        result.setData(list);
        result.setDraw(draw);
        result.setSuccess(true);
        return result;
    }

}
