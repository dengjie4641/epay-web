package cn.epay.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Count {

    private BigDecimal amount = new BigDecimal("0.00");

    private BigDecimal alipay = new BigDecimal("0.00");

    private BigDecimal wechat = new BigDecimal("0.00");

    private BigDecimal qq = new BigDecimal("0.00");

    private BigDecimal union = new BigDecimal("0.00");

    private BigDecimal diandan = new BigDecimal("0.00");

    private BigDecimal dmf = new BigDecimal("0.00");


}
