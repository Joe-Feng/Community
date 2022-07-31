package com.xcf.community.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Joe
 * @ClassName OssConfig.java
 * @Description
 * @createTime 2022年06月04日 11:07:00
 */
@Configuration
// 指定配置文件位置
//@PropertySource(value = {"classpath:application-aliyun-oss.yml"})
// 指定配置文件中自定义属性前缀
//@ConfigurationProperties(prefix = "aliyun")
@Data// lombok
//@Accessors(chain = true)// 开启链式调用
public class OssConfig {

    @Value("${aliyun.end-point}")
    private String endPoint;// 地域节点

    @Value("${aliyun.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.bucket-name}")
    private String bucketName;// OSS的Bucket名称

    @Value("${aliyun.url-prefix}")
    private String urlPrefix;// Bucket 域名

    @Value("${aliyun.file-host}")
    private String fileHost;// 目标文件夹

    // 创建OSSClient实例。 将OSS 客户端交给Spring容器托管
    @Bean
    public OSS OSSClient() {
        return new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
    }
}
