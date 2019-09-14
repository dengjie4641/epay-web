package cn.epay.controller;

import cn.epay.bean.dto.Count;
import cn.epay.bean.dto.Result;
import cn.epay.service.PayService;
import cn.epay.utils.ResultUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 统计管理类
 */
@Slf4j
@Controller
@RequestMapping("/pay")
@Api(tags = "开放接口", description = "数据报表统计管理")
public class StatisticController {

    @Autowired
    private PayService payService;

    @Value("${my.token}")
    private String my_token;


    /**
     * 数据统计
     * @return
     */
    @PostMapping(value = "/statistic")
    @ResponseBody
    public Result<Object> statistic(@RequestParam Integer type,
                                    @RequestParam(required = false) String start,
                                    @RequestParam(required = false) String end,
                                    @RequestParam(required = true) String myToken) {

        if (!my_token.equals(myToken)) {
            return ResultUtil.error("二次密码验证不正确");
        }
        Count count = payService.statistic(type, start, end);
        return ResultUtil.success(count);
    }
}
