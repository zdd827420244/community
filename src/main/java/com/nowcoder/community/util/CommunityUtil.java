package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //里面都是一些静态方法，很简单的，我们直接调用就行，就不拿容器托管了
    //生成一个随机字符串  用于生成激活码等情况
    public static String generateUUID(){//因为用到了UUID这个工具
        return UUID.randomUUID().toString().replaceAll("-","");  //UUID是JAVA自带的，randomUUID()生成一个UUID对象   里面会有字母数字和横线构成,我们不想要这个横线，所以全部用空字符串代替这个横线就行
    }

    //MD5加密  注册时，提交密码是明文，但我们存的时候，是要对其加密的  MD5只能加密不能解密，同一个值，每次加密，得到的密文值都是相同的
    //这样其实并不安全 比如hello->abc123def456 黑客库里是有hello这个词的，他会去尝试库里所有单词，当得到abc123def456后，他就知道你的密文是hello了
    //user表里有个salt盐字段  不管你密码是什么，我们都给你加一个随机的没有规律的字符串，也就是这个盐  这样hello + 3e4a8-> abc123def456abc
    //但是黑客库里是没有hello + 3e4a8这样的单词的，因为这个单词是个无规律的单词，这样就没办法盗取你的密码了
    public static String md5(String key){ //这个key实际上就是你原密码+盐
        if(StringUtils.isBlank(key)) { //StringUtils这个工具判断空很好用，key为null、空串、空格都会被这个工具判断为空
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());  //spring自带的一个工具 加密成一个16进制的字符串返回，但它要求传入的参数是byte数组
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map) {//编号、提示信息、业务数据，
        //返回的JSON数据往往要包含几部分，本方法就是把这几部分整合到一起，封装成一个JSON对象，把这个对象转换成字符串，返回一个JSON串
        JSONObject json=new JSONObject();//JSON对象
        json.put("code",code);
        json.put("msg",msg);
        //map业务数据需要打散装入
        if(map!=null) {
            for(String key:map.keySet()) {
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code, String msg) {//编号一定有  重载，便于调用
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code) {
        return getJSONString(code,null,null);
    }
    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
    }









}
