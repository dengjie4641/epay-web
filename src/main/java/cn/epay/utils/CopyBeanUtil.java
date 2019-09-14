package cn.epay.utils;


import org.springframework.beans.BeanUtils;

/**
 * @ClassName: CopyBeanUtil
 * @Description: 拷贝对象
 * @Auther: 一叶知秋
 * @Date: 2019/8/27 14:12
 * @version: V1.0
 **/
public class CopyBeanUtil {

    private CopyBeanUtil(){}

    /**
     * @Description //拷贝对象属性
     * @Author 一叶知秋
     * @Date 2019/8/27 14:19
     * @Param [dest 目标对象, orig 源对象]
     * @return void
     **/
    public static void copyProperties(final Object orig,final Object dest){
        try{
            BeanUtils.copyProperties(orig,dest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
