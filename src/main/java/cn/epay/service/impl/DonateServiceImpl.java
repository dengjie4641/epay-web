package cn.epay.service.impl;

import cn.epay.bean.Pay;
import cn.epay.utils.CopyBeanUtil;
import cn.epay.utils.StringUtils;
import cn.epay.dao.PayDao;
import cn.epay.service.DonateService;
import cn.epay.vo.reqVo.FailOrderListReqVo;
import cn.epay.vo.respVo.PayRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DonateServiceImpl
 * @Description: TODO
 * @Auther: 一叶知秋
 * @Date: 2019/8/27 13:56
 * @version: V1.0
 **/
@Service
public class DonateServiceImpl implements DonateService {

    @Autowired
    private PayDao payDao;




    @Override
    public Page<Pay> getPayListByPage(Integer state, String key, Pageable pageable) {

        return payDao.findAll(new Specification<Pay>() {
            @Override
            public Predicate toPredicate(Root<Pay> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> nickNameField = root.get("nickName");
                Path<String> infoField = root.get("info");
                Path<String> payTypeField=root.get("payType");
                Path<Integer> stateField=root.get("state");

                List<Predicate> list = new ArrayList<Predicate>();

                //模糊搜素
                if(StringUtils.isNotBlank(key)){
                    Predicate p1 = cb.like(nickNameField,'%'+key+'%');
                    Predicate p3 = cb.like(infoField,'%'+key+'%');
                    Predicate p4 = cb.like(payTypeField,'%'+key+'%');
                    list.add(cb.or(p1,p3,p4));
                }

                //状态
                if(state!=null){
                    list.add(cb.equal(stateField, state));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public List<PayRespVo> getFailOrderList(FailOrderListReqVo failOrderListReqVo) {
        List<Pay> list = payDao.getByStateIs(0);
        List<PayRespVo> payRespVos = new ArrayList<>();
        list.stream().forEach(item->{
            PayRespVo payRespVo = new PayRespVo();
            CopyBeanUtil.copyProperties(payRespVo,item);
            payRespVos.add(payRespVo);
        });
        return payRespVos;
    }
}
