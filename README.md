# ePay个人收款支付系统
epay个人免签收款支付系统,完全免费,资金直接到达本人账号,支持支付宝,微信,QQ,云闪付  
无需备案,无需签约,无需挂机监控APP,无需插件,无需第三方支付SDK,无需营业执照身份证,只需收款码,搞定支付流程 
现已支持移动端支付  
体验链接地址：[http://5epay.cn][http://5epay.cn]

> 本系统已经升级V2.0了，将jpa换成了mybatis-plus，查询更快，操作更简单，更是有详细的文档可以参阅
点击epay官网地址可以获取源码：[http://5epay.cn][http://5epay.cn]
- 或者用手机扫码体验  
![获取userId](mobiletest.png)

### 本系统基于Java开发 新手请先百度Spring Boot教程

#### 前端所用技术
MUI：原生前端框架  
jQuery  
BootStrap  
DataTables：jQuery表格插件  
#### 后端所用技术
各框架依赖版本皆使用目前最新版本  
SpringBoot  
SpringMVC  
Spring Data Jpa  [注：V2.0已经更换为mybatis-plus框架，点击获取最新源码](http://5epay.cn)  
MySQL  
Spring Data Redis  
Druid  
Thymeleaf：模版引擎  
Swagger2  
Maven  
其它小实现：
@Async 异步调用  
@Scheduled 定时任务  
JavaMailSender发送模版邮件  
第三方插件
lombok 
其它开发工具  
JRebel：开发热部署  
阿里JAVA开发规约插件  
文件说明  
数据库文件：epay.sql(仅一张表) 
本地开发运行部署  
依赖：Redis(必须)  
新建数据库，见epay.sql文件  
在 application.properties 中修改你的配置，例如端口、数据库、Redis、邮箱配置等，其中有详细注释  
运行 EpayApplication.java  
访问默认端口8080：http://localhost:8080  
Linux后台运行示例   
nohup java -jar xpay-1.0-SNAPSHOT.jar -Xmx128m &  


### 支付宝官方获取userId方式
- 支付宝内打开链接：`https://render.alipay.com/p/f/fd-ixpo7iia/index.html`
- 或使用支付宝扫描该文件夹根目录下中的二维码
- userId获取：请进入 蚂蚁金服开放平台官网 登陆后 点击右上角进入账户管理或账户信息 在合作伙伴管理下方即可找到你的角色身份PID那串数字即为你的userId，其他方法请百度
- 支付宝扫码：打开你的支付宝，扫下面的图片
- ![扫码获取userId](1568474899.png)  

### v1.9新增支付宝一键红包支付模式以及云闪付说明
- 借助支付宝“扫码点单”小程序，只需开通商家收款码即可开通
- 备注号对应桌号
- 详细使用说明见word图文文档
- 红包模式正规个人业务没必要使用，量大怕风控者用的
- 需先执行加好友，支付宝需在设置-隐私-常用隐私设置中关闭加好友需要验证，实例：`alipays://platformapi/startapp?appId=20000186&actionType=addfriend&userId=支付宝userId&loginId=支付宝账号&source=by_f_v&alert=true`
- 仅支持普通红包，目前h5中可一键拉起，同样url的中文（如支付宝昵称）需经过encode编码，实例：`alipays://platformapi/startapp?appId=88886666&appLaunchMode=3&canSearch=false&chatLoginId=支付宝账号&chatUserId=支付宝userId&chatUserName=支付宝昵称&chatUserType=1&entryMode=personalStage&prevBiz=chat&schemaMode=portalInside&target=personal&money=金额&amount=金额&remark=备注`
- 云闪付由于官方风控生成的固码具有时效性（一定时间后无法扫码）
    - 解决方案：请务必使用商家收款码（app中申请即可），由于商家收款吗无法添加备注，匹配支付标识采用不同优惠价格实现，优惠规则详见pay.html中487行js代码
- v1.9需要替换的地方：
```
alipay.html中你的访问域名前缀 替换ePay.exrick.cn
openAlipay.html中支付宝userId、银行卡转账信息、红包模式所需支付宝账号相关信息
qr/unipay二维码文件夹只需配置云闪付商家收款码
```
### v1.8新增银行卡转账模式
- 转银行卡模式正规个人业务没必要使用，量大怕风控者用的
- 支付宝内打开该web应用，实例：`https://ds.alipay.com/?from=pc&appId=09999988&actionType=toCard&sourceId=bill&cardNo=银行卡卡号&bankAccount=持卡人姓名&money=金额&amount=金额&bankMark=银行缩写简写&bankName=银行完整名称&tdsourcetag=s_pctim_aiomsg` url中文记得需经过encode编码
- v1.8增加需要替换的地方：
```
alipay.html中你的访问域名前缀 替换5epay.cn
openAlipay.html中的银行卡转账信息
```
### v1.7支付宝转账码原理

- 拉起支付宝APP借助[支付宝H5 JSAPI](http://myjsapi.alipay.com/jsapi/index.html)，先打开指定网页，url需经过encode编码，例如 alipays://platformapi/startapp?appId=20000067&url=http%3A%2F%2Fm.taobao.com
- 无法修改金额转账应用，实例：`alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={"s": "money","u": "你的支付宝userId","a": "金额","m": "备注"}`,
- v1.7需要替换的地方：
```
alipay.html中的访问域名如ePay.exrick.cn和支付宝userId
openAlipay.html中的自定义金额收款码
```

### v1.6支付宝转账码原理
- 示例： `alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&userId=你的支付宝userId&amount=金额&memo=备注`
- userId获取：请进入 蚂蚁金服开放平台官网 登陆后 点击右上角进入账户管理或账户信息 在合作伙伴管理下方即可找到你的角色身份PID那串数字即为你的userId
- 通过scheme启动 scheme可以理解为一种特殊的URI，格式与URI相同 支付宝客户端的标准scheme为：alipays://platformapi/startapp?appId=
即为H5App自身的appId，但如果是某些运营页之类的单独页面，没有自己的appId，可以使用Nebula容器的通用浏览器模式appId=20000067 来启动，同时将需要打开的H5页面url经过encode编码后设置到url参数内，例如：alipays://platformapi/startapp?appId=20000067&url=http%3A%2F%2Fm.taobao.com
- 因此其中`appId=09999988`为支付宝内转账码H5应用，不得修改
- <a href='alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&userId=2088012242122163&amount=66.66&memo=测试' target='_blank' class='btn btn-danger m-top-20'>测试一键打开支付宝APP支付</a>
- 若转账码被封将退回v1.5版本 请各位做好被封心理准备

### v1.5支付宝风控解决方案
- 固码收款将非常容易触发风控，因此废弃固码，仅支持自定义金额输入，由于ePay天生的“人工智障”检测优势，支持用户自定义金额输入(要求用户输入订单备注)

### v1.2升级原理说明
- 创建多张同金额不同备注的收款码，支付时挨个递增选取，实现订单支付标识，添加的越多，越能实现多人短时间内同时支付。
    - 配置二维码数量数在`application.properties`中修改，二维码配置在`src\main\resources\static\assets\qr`文件内，具体支付宝支付为"alipay"文件夹，"1.00元"分为单个"1.00"文件夹，其中多个图片命名由"1"递增，订单备注需和"1"相同或者设置为你能识别对应的，图格式为".png"，图片数不得少于你在`application.properties`中配置的
- 自定义金额收款需用户输入系统自动生成的四位数随机码，实现订单支付标识，图片名为"custom.png"
- 一键打开支付宝App配置（支持安卓浏览器、不支持微信）【固码已凉 仅支持自定义码】
    - 将生成的支付宝收款码解析链接后放入href即可 例如：HTTPS://QR.ALIPAY.COM/FKX05348YGHADA5W9JJV66，具体见下面
    - `<a href='HTTPS://QR.ALIPAY.COM/FKX05348YGHADA5W9JJV66' target='_blank'>一键打开支付宝APP支付</a>`
    - 具体页面中取链接配置参考`alipay.html`页面js代码

##个人申请支付接口现状分析
### 原生网银支付

以银行网银为代表。此类方式涉及到承诺、合同、不菲的保证金，对个人来说不现实。

结论：不可行

### 原生支付宝，微信支付

支付宝微信只服务于有营业执照、个体工商户的商户。就算你有钱但没实体店铺在某宝上也是买不到的。截止目前（2019-01-01）无法以个人身份（或以个人为主体）直接申请API。

结论：不可行

### 关联企业支付宝账号

即新建企业账户，然后采用已经实名认证了的企业账户关联该账户，用其实名主体完成新账户的实名认证。一系列操作完成后，新的账户具有和企业账户一样的资质可以申请API。

结论：如果条件允许，推荐此方案

### 购买企业支付宝账号

购买一个企业支付宝账号，然后信息变更，完成过户。但这种“非法”的方式，毕竟涉及到钱的问题，安全性无保障。另外，企业账户价格比较高。

结论：不推荐

### 聚合支付工具，Ping++等

就是个第四方聚合支付工具，简化了接入开发流程而已，个人开发者仍然需要去申请所需接口的使用权限。

结论：不可行

### 第四方聚合支付

支付资金进入官方账号，自己再进行提现操作。需要开通域名，提现手续费较高，支付页面不支持自定义。另外，对于此种类型的聚合支付平台，隐藏着极高的跑路风险。

结论：不推荐

### 美团 & 支付宝口碑

通过美团商家码收款，通过支付宝口碑掌柜收款。

结论：不推荐，支付转化率并不高，可能随时被风控。

### 有赞

通过有赞微商城支付接口收款。

结论：不推荐，需手动提现，不免费，费用6800/年起，一旦风控资金很难取出。

### 挂机监听软件，PaysApi、绿点支付等

本质上依然是采用挂机监听的策略，但针对的是移动端支付宝或微信的收款通知消息
结论：成本高，配置麻烦，需24小时挂台安卓手机，不免费
其他基于Xposed挂机监听软件

### 基于virtual xposed hook相关技术，可自动生成任意备注金额收款码 参考抢红包外挂
结论：成本高，配置麻烦，需24小时挂台安卓手机，量大易触发风控、不免费，黑产适用
Payjs （疑使用微信小微商户）

结论：仅支持微信、不免费、使用官方接口收取代开费用
国外支付，PayPal、Strip：不可用

### ePay
支持支付宝、微信、QQ钱包、翼支付、云闪付等任意收款码，资金直接到达本人账号，个人一键审核即时回调，不需提现，不需备案，完全免费，不干涉监听任何支付数据，个人收款0风险方案
由于xpay天生“人工智障”检测方案，个人收款使用无须担心风控问题
结论：个人收款较少、见不得人的支付业务推荐使用

## 点击epay官网地址可以获取源码：[http://5epay.cn](http://5epay.cn)
- 或者用手机扫码体验  
![获取userId](mobiletest.png)