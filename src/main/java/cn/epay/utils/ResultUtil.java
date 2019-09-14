package cn.epay.utils;


import cn.epay.bean.dto.Result;

import java.io.Serializable;

/**
 * @author Exrick
 * @date 2017/8/20
 */
public class ResultUtil<T> implements Serializable{

    private static final long serialVersionUID = -1445353534322323L;

    private static int SUCCESS = 200;

    private static int ERROR = 500;

    private Result<T> result;

    public static Result success(){
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    public static Result success(Object data){
        return new ResultUtil<Object>().setData(data);
    }

    public static Result error(String msg){
        return new ResultUtil<Object>().setErrorMsg(msg);
    }

    public static Result error(int code,String msg){
        return new ResultUtil<Object>().setErrorMsg(code,msg);
    }

    private ResultUtil(){
        result=new Result<>();
        result.setSuccess(true);
        result.setMessage("success");
        result.setCode(SUCCESS);
    }

    public Result<T> setData(T t){
        this.result.setResult(t);
        this.result.setCode(SUCCESS);
        return this.result;
    }

    public Result<T> setSuccessMsg(String msg){
        this.result.setSuccess(true);
        this.result.setMessage(msg);
        this.result.setCode(SUCCESS);
        this.result.setResult(null);
        return this.result;
    }

    public Result<T> setData(T t, String msg){
        this.result.setResult(t);
        this.result.setCode(SUCCESS);
        this.result.setMessage(msg);
        return this.result;
    }

    public Result<T> setErrorMsg(String msg){
        this.result.setSuccess(false);
        this.result.setMessage(msg);
        this.result.setCode(ERROR);
        return this.result;
    }

    public Result<T> setErrorMsg(int code, String msg){
        this.result.setSuccess(false);
        this.result.setMessage(msg);
        this.result.setCode(code);
        return this.result;
    }


}
