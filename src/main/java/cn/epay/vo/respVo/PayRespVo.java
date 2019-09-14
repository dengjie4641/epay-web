package cn.epay.vo.respVo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName: PayRespVo
 * @Description: TODO
 * @Auther: 一叶知秋
 * @Date: 2019/8/27 14:05
 * @version: V1.0
 **/
@Data
public class PayRespVo {


    @Column
    private String nickName;

    @Column
    private BigDecimal money;

    /**
     * 留言
     */
    @Column
    private String info;

    @Column
    private Date createTime;

    @Column
    private Date updateTime;

    /**
     * 显示状态 0待审核 1确认显示 2驳回 3通过不展示 4已扫码
     */
    @Column
    private Integer state;

    @Column
    private String payType;



}
