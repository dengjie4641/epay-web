package cn.epay.vo.reqVo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: AuditOrderListReqVo
 * @Description: TODO
 * @Auther: 一叶知秋
 * @Date: 2019/8/27 17:05
 * @version: V1.0
 **/
@Data
public class AuditOrderListReqVo {

    @NotBlank(message = "订单状态必填")
    private Integer status;

}
