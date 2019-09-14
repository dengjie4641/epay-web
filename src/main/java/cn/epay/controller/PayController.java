package cn.epay.controller;

import cn.epay.bean.Pay;
import cn.epay.bean.dto.DataTablesResult;
import cn.epay.bean.dto.Result;
import cn.epay.common.enums.PayStateEnum;
import cn.epay.constant.PayConstant;
import cn.epay.service.AuditPayService;
import cn.epay.service.PayService;
import cn.epay.utils.*;
import cn.epay.vo.respVo.PayRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Description //支付模块管理类
 * @Author 一叶知秋
 * @Date 2019/8/14 9:20
 * @Param
 * @return
 **/
@SuppressWarnings("all")
@Slf4j
@Controller
@RequestMapping("/pay")
@CacheConfig(cacheNames = "epay")
@Api(tags = "开放接口", description = "捐赠列表管理")
public class PayController {

    @Autowired
    private PayService payService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private AuditPayService auditPayService;


    /**
     * @return cn.epay.bean.dto.DataTablesResult
     * @Description //获取未支付数据
     * @Author 一叶知秋
     * @Date 2019/8/14 9:23
     * @Param []
     **/
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取未支付数据")
    @ResponseBody
    public DataTablesResult getPayList() {
        DataTablesResult result = new DataTablesResult();
        List<PayRespVo> list = new ArrayList<>();
        try {
            //退回数据
            list = payService.getPayList(PayStateEnum.REBACK.getValue());
            //审核不通过数据
            List<PayRespVo> payList = payService.getPayList(PayStateEnum.AIDIT_NOT_PASS.getValue());
            list.addAll(payList);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError("获取未支付数据失败");
            return result;
        }
        result.setData(list);
        result.setSuccess(true);
        return result;
    }

    /**
     * @return cn.epay.bean.dto.DataTablesResult
     * @Description //获取支付审核列表
     * @Author 一叶知秋
     * @Date 2019/8/14 9:23
     * @Param []
     **/
    @GetMapping(value = "/check/list")
    @ApiOperation(value = "获取支付审核列表")
    @ResponseBody
    public DataTablesResult getCheckList() {

        DataTablesResult result = new DataTablesResult();
        List<PayRespVo> list = new ArrayList<>();
        try {
            list = payService.getPayList(PayStateEnum.AUDIT.getValue());
            //已经扫码
            list.addAll(payService.getPayList(PayStateEnum.SUCCESS_SM.getValue()));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError("获取支付审核列表失败");
            return result;
        }
        result.setData(list);
        result.setSuccess(true);
        return result;
    }


    /**
     * @return cn.epay.bean.dto.Result<java.lang.Object>
     * @Description //获取支付状态
     * @Author 一叶知秋
     * @Date 2019/8/14 9:24
     * @Param [id]
     **/
    @ResponseBody
    @GetMapping(value = "/state/{id}")
    @ApiOperation(value = "获取支付状态")
    @Cacheable(key = "#id")
    public Result<Object> getPayState(@PathVariable String id) {
        Pay pay = null;
        try {
            pay = payService.getPay(payService.getPayId(id));
        } catch (Exception e) {
            return ResultUtil.error("获取支付数据失败");
        }
        return ResultUtil.success(pay.getState());
    }

    /**
     * @return cn.epay.bean.dto.Result<java.lang.Object>
     * @Description //获取已经支付数据
     * @Author 一叶知秋
     * @Date 2019/8/14 9:27
     * @Param [id, token]
     **/
    @ResponseBody
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "获取支付数据")
    public Result<Object> getPayData(@PathVariable String id,
                                     @RequestParam String token) {
        String temp = redisTemplate.opsForValue().get(id);
        if (!token.equals(temp)) {
            return ResultUtil.error("无效的Token或链接");
        }
        Pay pay = null;
        try {
            pay = payService.getPay(payService.getPayId(id));
        } catch (Exception e) {
            return ResultUtil.error("获取支付数据失败");
        }
        return ResultUtil.success(pay);
    }



    /**
     * @return cn.epay.bean.dto.Result<java.lang.Object>
     * @Description //编辑支付订单
     * @Author 一叶知秋
     * @Date 2019/8/14 9:29
     * @Param [pay, id, token]
     **/
    @ResponseBody
    @PostMapping(value = "/edit")
    @ApiOperation(value = "编辑支付订单")
    @CacheEvict(key = "#id")
    public Result<Object> editPay(@ModelAttribute Pay pay,
                                  @RequestParam String id,
                                  @RequestParam String token) {

        String temp = redisTemplate.opsForValue().get(id);
        if (!token.equals(temp)) {
            return ResultUtil.error("无效的Token或链接");
        }
        try {
            String payId = pay.getId();
            String ID  = payService.getPayId(payId);
            pay.setId(ID);
            payService.editPay(pay);
        } catch (Exception e) {
            return ResultUtil.error("编辑支付订单失败");
        }
        return ResultUtil.success(null);
    }



    /**
     * @return java.lang.String
     * @Description //审核通过支付订单
     * @Author 一叶知秋
     * @Date 2019/8/14 9:44
     * @Param [id, token, myToken, sendType, model]
     **/
    @GetMapping(value = "/pass")
    @ApiOperation(value = "审核通过支付订单")
    @CacheEvict(key = "#id")
    public String passPay(@RequestParam String id,
                         @RequestParam String token,
                         @RequestParam String myToken,
                         @RequestParam String sendType, Model model) {
        //获取token信息
        String temp = redisTemplate.opsForValue().get(id);
        if (!token.equals(temp)) {
            model.addAttribute("errorMsg", "无效的Token或链接");
            return "500";
        }
        return auditPayService.passPay(id,token,myToken,sendType,model);
    }

    /**
     * @return java.lang.String
     * @Description //审核通过但不显示加入捐赠表
     * @Author 一叶知秋
     * @Date 2019/8/14 9:45
     * @Param [id, token, model]
     **/
    @GetMapping(value = "/passNotShow")
    @ApiOperation(value = "审核通过但不显示加入捐赠表")
    @CacheEvict(key = "#id")
    public String passNotShowPay(@RequestParam String id,
                                 @RequestParam String token,
                                 Model model) {

        String temp = redisTemplate.opsForValue().get(id);
        if (!token.equals(temp)) {
            model.addAttribute("errorMsg", "无效的Token或链接");
            return "500";
        }

        return auditPayService.passNotShow(id,model);
    }


    /**
     * @return java.lang.String
     * @Description //退回审核未成功的支付订单
     * @Author 一叶知秋
     * @Date 2019/8/14 9:46
     * @Param [id, token, myToken, model]
     **/
    @GetMapping(value = "/back")
    @ApiOperation(value = "审核驳回支付订单")
    @CacheEvict(key = "#id")
    public String backPay(@RequestParam String id,
                          @RequestParam String token,
                          @RequestParam String myToken,
                          Model model) {

        String temp = redisTemplate.opsForValue().get(id);
        if (!token.equals(temp)) {
            model.addAttribute("errorMsg", "无效的Token或链接");
            return "500";
        }

        return auditPayService.backPay(id,myToken,model);
    }

    /**
     * @return cn.epay.bean.dto.Result<java.lang.Object>
     * @Description //删除支付订单
     * @Author 一叶知秋
     * @Date 2019/8/14 9:48
     * @Param [id, token]
     **/
    @ResponseBody
    @GetMapping(value = "/del")
    @ApiOperation(value = "删除支付订单")
    @CacheEvict(key = "#id")
    public Result<Object> delPay(@RequestParam String id,
                                 @RequestParam String token) {

        String temp = redisTemplate.opsForValue().get(id);
        if (!token.equals(temp)) {
            return ResultUtil.error("无效的Token或链接");
        }
        Result<Object> result = payService.delPayOrder(id);
        return result;
    }

    /**
     * 关闭或开启系统
     *
     * @param id
     * @param token
     * @param all
     * @param allReason
     * @param dmf
     * @param dmfReason
     * @return
     */
    @PostMapping(value = "/closeOrOpen")
    @ResponseBody
    public Result<Object> closeOrOpen(@RequestParam String id,
                                      @RequestParam String token,
                                      @RequestParam Boolean all,
                                      @RequestParam String allReason,
                                      @RequestParam Boolean dmf,
                                      @RequestParam String dmfReason) {
        ValueOperations<String, String> vops = redisTemplate.opsForValue();
        String temp = vops.get(id);
        if (!token.equals(temp)) {
            return ResultUtil.error("无效的Token或链接");
        }
        try {
            if (all) {
                redisTemplate.delete(PayConstant.CLOSE_KEY);
            } else {
                vops.set(PayConstant.CLOSE_KEY, "CLOSED");
                // 设置原因
                vops.set(PayConstant.CLOSE_REASON, allReason);
            }
            if (dmf) {
                redisTemplate.delete(PayConstant.CLOSE_DMF_KEY);
            } else {
                vops.set(PayConstant.CLOSE_DMF_KEY, "CLOSED");
                // 设置原因
                vops.set(PayConstant.CLOSE_DMF_REASON, dmfReason);
            }
        } catch (Exception e) {
            return ResultUtil.error("处理数据出错");
        }
        return ResultUtil.error("操作成功");
    }

    /**
     * 当前系统状态
     *
     * @param id
     * @param token
     * @param model
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/currStatus")
    public Result<Object> open(@RequestParam String id, @RequestParam String token,
                               Model model) {

        String temp = redisTemplate.opsForValue().get(id);
        if (!token.equals(temp)) {
            return  ResultUtil.error("无效的Token或链接");
        }
        Map<String, Object> map = new HashMap<>(16);
        String all = redisTemplate.opsForValue().get(PayConstant.CLOSE_KEY);
        if (StringUtils.isBlank(all)) {
            map.put("all", true);
        } else {
            map.put("all", false);
        }
        String dmf = redisTemplate.opsForValue().get(PayConstant.CLOSE_DMF_KEY);
        if (StringUtils.isBlank(dmf)) {
            map.put("dmf", true);
        } else {
            map.put("dmf", false);
        }
        map.put("allReason", redisTemplate.opsForValue().get(PayConstant.CLOSE_REASON));
        map.put("dmfReason", redisTemplate.opsForValue().get(PayConstant.CLOSE_DMF_REASON));
        return  ResultUtil.success(map);
    }



}
