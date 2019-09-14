package cn.epay.bean.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 前后端交互数据标准
 * @author Exrick
 * @date 2017/8/20
 */
@Data
public class Result<T> implements Serializable{

    /**
     * 成功标志
     */
    private boolean success;

    /**
     * 失败消息
     */
    private String message;

    /**
     * 返回代码
     */
    private Integer code;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 结果对象
     */
    private T result;

}
