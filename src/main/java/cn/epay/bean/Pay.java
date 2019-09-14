package cn.epay.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Exrickx
 */
@Data
@Entity
@Table(name = "t_pay")
public class Pay implements Serializable{

    /**
     * 唯一标识
     */
    @Id
    @Column
    private String id;

    @Column
    @NotEmpty(message = "昵称必填")
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
     * 用户通知邮箱
     */
    @Column
    @Email(message = "填写正确的邮箱")
    private String email;

    @Column
    private String testEmail;

    /**
     * 显示状态 0待审核 1确认显示 2驳回 3通过不展示 4已扫码
     */
    @Column
    private Integer state=0;

    @Column
    @NotEmpty(message = "支付类型必填")
    private String payType;

    /**
     * 支付标识
     */
    @Column
    private String payNum;

    /**
     * 是否自定义输入
     */
    @Column
    private Boolean custom;

    /**
     * 支付设备是否为移动端
     */
    @Column
    private Boolean mobile;

    /**
     * 用户支付设备信息
     */
    @Column(length = 1000)
    private String device;

    /**
     * 生成二维码编号标识token
     */
    @Transient
    private String tokenNum;

    @Transient
    private String time;

    @Transient
    @JsonIgnore
    private String passUrl;

    /**
     * 含小程序
     */
    @Transient
    @JsonIgnore
    private String passUrl2;

    /**
     * 含xboot
     */
    @Transient
    @JsonIgnore
    private String passUrl3;

    @Transient
    @JsonIgnore
    private String backUrl;

    @Transient
    @JsonIgnore
    private String passNotShowUrl;

    @Transient
    @JsonIgnore
    private String editUrl;

    @Transient
    @JsonIgnore
    private String delUrl;

    @Transient
    @JsonIgnore
    private String closeUrl;

    @Transient
    @JsonIgnore
    private String statistic;

}
