package cn.epay.controller;

import cn.epay.common.enums.AlipayStatusEnum;
import cn.epay.utils.StringUtils;
import cn.epay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;


/**
 * @Description
 * @Author 一叶知秋
 * @Date 2019/8/14 9:19
 * @Param 
 * @return 
 **/
@Controller
public class PageController {


    @Autowired
    private PayService payService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @Description //跳转到首页
     * @Author 一叶知秋
     * @Date 2019/8/14 9:19
     * @Param []
     * @return java.lang.String
     **/
    @RequestMapping("/")
    public String index(){
        return "index";
    }

    
    
    /**
     * @Description //删除数据
     * @Author 一叶知秋
     * @Date 2019/8/14 9:19
     * @Param [page, request]
     * @return java.lang.String
     **/
    @RequestMapping("/{page}")
    public String showPage(@PathVariable("page") String page,
                           HttpServletRequest request){

        String id = request.getParameter("id");
        if(AlipayStatusEnum.openAlipay.getValue().equals(page)
                && StringUtils.isNotBlank(id)){
            // 已扫码状态
            try{
                payService.changePayState(id,4);
                Set<String> keys = redisTemplate.keys("epay:*");
                redisTemplate.delete(keys);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return page;
    }
}
