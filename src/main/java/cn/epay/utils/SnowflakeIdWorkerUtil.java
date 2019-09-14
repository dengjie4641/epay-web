package cn.epay.utils;

/**
 * @Description //雪花算法ID生成器
 * @Author 一叶知秋
 * @Date 2019/8/27 20:08
    0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
    第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)，然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点） ，最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
    一共加起来刚好64位，为一个Long型。(转换成字符串后长度最多19)
    https://www.cnblogs.com/relucent/p/4955340.html  http://www.wolfbe.com/detail/201611/381.html
 */
public class SnowflakeIdWorkerUtil {
    private SnowflakeIdWorkerUtil(){}

    //得到二进制样例 10111100110111110011001010100001100111111100001000000000000
    /** 开始时间截 (2015-01-01) */
    private final long twepoch = 1420041600000L;
 
    //每一部分占用的位数
    /** 机器id所占的位数 */
    private final long workerIdBits = 5L;
    /** 数据标识id所占的位数 */
    private final long datacenterIdBits = 5L;
    /** 序列在id中占的位数 */
    private final long sequenceBits = 12L;
 
    //每一部分的最大值
    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /** 支持的最大数据标识id，结果是31 */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
 
    //每一部分向左的位移
    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;
    /** 数据标识id向左移17位(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
 
    
 
    /** 工作机器ID(0~31) */
    private long workerId=1;
    /** 数据中心ID(0~31) */
    private long datacenterId=31;
    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;
    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;
 
    //==============================Constructors=====================================
    /**
     * 构造函数
     * @param workerId 工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    private SnowflakeIdWorkerUtil(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
 
    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
 
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
 
        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出 //同一毫秒的序列数已经达到最大
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
 
        //上次生成ID的时间截
        lastTimestamp = timestamp;
 
        System.out.println("timestamp: "+timestamp+","+(timestamp - twepoch));
        //移位并通过或运算拼到一起组成64位的ID，类似于相加的方式,当前时间减去2015-01-01，41bit的时间戳可以支持该算法使用年限：69年
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }
 
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
 
    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }


    /**
     * @Description //生成id
     * @Author 一叶知秋
     * @Date 2019/8/27 20:16
     * @Param []
     * @return java.lang.Long
     **/
    public static final Long getId(){
        SnowflakeIdWorkerUtil idWorker = new SnowflakeIdWorkerUtil(1, 31);
        Long id = idWorker.nextId();
        return id;
    }

    //==============================Test=============================================
    /** 测试 */
    public static void main(String[] args) {
        SnowflakeIdWorkerUtil idWorker = new SnowflakeIdWorkerUtil(1, 31);
        for (int i = 0; i < 10; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id)+" "+id);
        }
    }
}