package cn.epay.dao;

import cn.epay.bean.Pay;
import cn.epay.vo.reqVo.FailOrderListReqVo;
import cn.epay.vo.respVo.PayRespVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PayDao extends JpaRepository<Pay,String>, JpaSpecificationExecutor<Pay> {

    @Select("select * from t_pay where state = #{state}")
    List<Pay> getByStateIs(Integer state);

    @Select("select * from t_pay where state = #{state} and pay_type = #{payType}")
    List<Pay> getByStateAndPayType(Integer state, String payType);

    @Query(value = "select sum(money) from t_pay where state = 1",nativeQuery = true)
    BigDecimal countAllMoney();

    @Query(value = "select sum(money) from t_pay where state = 1 and pay_type = ?1",nativeQuery = true)
    BigDecimal countAllMoneyByType(String payType);

    @Query(value = "select sum(money) from t_pay where state = 1 and create_time between ?1 and ?2",nativeQuery = true)
    BigDecimal countMoney(Date date1, Date date2);

    @Query(value = "select sum(money) from t_pay where state = 1 and pay_type = ?1 and create_time between ?2 and ?3",nativeQuery = true)
    BigDecimal countMoneyByType(String payType, Date date1, Date date2);

}
