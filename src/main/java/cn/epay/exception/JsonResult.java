package cn.epay.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName: JsonResult
 * @Description: TODO
 * @Auther: 一叶知秋
 * @Date: 2019/8/26 20:53
 * @version: V1.0
 **/
@Getter
@Setter
@ToString
public class JsonResult implements Serializable{

    private static final long serialVersionUID = -1445353534322323L;

    private static int SUCCESS = 200;

    private static int ERROR = 500;

    private int code;

    private String msg;

    private Object data;

    private JsonResult(int code,String msg,Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static JsonResult success(int code,String msg,Object data){
        JsonResult jsonResult = new JsonResult(code, msg, data);
        return jsonResult;
    }

    /**
     * @Description //操作成功
     * @Author 一叶知秋
     * @Date 2019/8/26 21:08
     * @Param [data]
     * @return cn.epay.exception.JsonResult
     **/
    public static final JsonResult success(Object data){
        JsonResult jsonResult = success(SUCCESS, "操作成功", data);
        return jsonResult;
    }

    /**
     * @Description //操作成功
     * @Author 一叶知秋
     * @Date 2019/8/26 21:11
     * @Param []
     * @return cn.epay.exception.JsonResult
     **/
    public static final JsonResult success(){
        return success(SUCCESS,"操作成功",null);
    }

    public static final JsonResult success(String msg){
        return success(SUCCESS,msg,null);
    }


    /**
     * @Description //返回失败信息
     * @Author 一叶知秋
     * @Date 2019/8/26 21:15
     * @Param [code, msg, data]
     * @return cn.epay.exception.JsonResult
     **/
    public static final JsonResult failure(int code,String msg,Object data){
        return new JsonResult(code,msg,data);
    }

    public static final JsonResult failure(String msg,Object data){
        return failure(ERROR,msg,data);
    }

    public static final JsonResult failure(Object data){
        return failure(ERROR,"操作失败",data);
    }

    public static final JsonResult failure(){
        return failure(ERROR,"操作失败",null);
    }

}
