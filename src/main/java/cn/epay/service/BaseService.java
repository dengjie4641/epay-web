package cn.epay.service;

import cn.epay.bean.Pay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class BaseService {

    @Value("${ip.expire}")
    public Long ipExpire;

    @Value("${my.token}")
    public String myToken;

    @Value("${email.sender}")
    public String emailSender;

    @Value("${email.receiver}")
    public String emailReceiver;

    @Value("${server.url}")
    public String serverUrl;

    @Value("${token.admin.expire}")
    public Long adminExpire;

    @Value("${token.fake.expire}")
    public Long fakeExpire;

    @Value("${fake.pre}")
    public String fakePre;

    @Value("${qrnum}")
    public Integer qrNum;

    /**
     * 拼接管理员链接
     */
    public Pay getAdminUrl(Pay pay,String server_url,String id,String token,String myToken){

        String pass=server_url+"/pay/pass?sendType=0&id="+id+"&token="+token+"&myToken="+myToken;
        pay.setPassUrl(pass);

        String pass2=server_url+"/pay/pass?sendType=1&id="+id+"&token="+token+"&myToken="+myToken;
        pay.setPassUrl2(pass2);

        String pass3=server_url+"/pay/pass?sendType=2&id="+id+"&token="+token+"&myToken="+myToken;
        pay.setPassUrl3(pass3);

        String back=server_url+"/pay/back?id="+id+"&token="+token+"&myToken="+myToken;
        pay.setBackUrl(back);

        String passNotShow=server_url+"/pay/passNotShow?id="+id+"&token="+token;
        pay.setPassNotShowUrl(passNotShow);

        String edit=server_url+"/pay-edit?id="+id+"&token="+token;
        pay.setEditUrl(edit);

        String del=server_url+"/pay-del?id="+id+"&token="+token;
        pay.setDelUrl(del);

        String close=server_url+"/pay-close?id="+id+"&token="+token;
        pay.setCloseUrl(close);

        String statistic=server_url+"/statistic?myToken="+myToken;
        pay.setStatistic(statistic);

        return pay;
    }


}
