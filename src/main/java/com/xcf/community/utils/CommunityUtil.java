package com.xcf.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName CommunityUtil.java
 * @Description
 * @createTime 2022年05月12日 16:53:00
 */
public class CommunityUtil {
    /**
     * 生成随机字符串
     * @return
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }



    /**
     * MD5加密
     * @param key 加上salt后的密码
     * @return
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 将数据封装成Json格式数据
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJsonString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    public static String getJsonString(int code, String msg){
        return getJsonString(code, msg, null);
    }

    public static String getJsonString(int code){
        return getJsonString(code, null, null);
    }
}
