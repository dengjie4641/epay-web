package cn.epay.vo.reqVo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: FailOrderListReqVo
 * @Description: TODO
 * @Auther: 一叶知秋
 * @Date: 2019/8/27 16:29
 * @version: V1.0
 **/
@Data
public class FailOrderListReqVo {

    @NotBlank(message = "状态码必填")
    private Integer status;

}
