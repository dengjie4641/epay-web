package cn.epay.exception;

import cn.epay.bean.dto.Result;
import cn.epay.utils.ResultUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: ExceptionHandlerAdvice
 * @Description: 异常全局捕获通知
 * @Auther: 一叶知秋
 * @Date: 2019/8/26 21:21
 * @version: V1.0
 **/
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result exceptionHandlerRuntimeException(){
        Result result = ResultUtil.error(500, "系統错误!");
        return result;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result exceptionHandlerException(Exception e){
        Result result = ResultUtil.error(500, e.getMessage());
        return result;
    }
}
