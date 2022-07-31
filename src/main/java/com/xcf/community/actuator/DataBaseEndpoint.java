package com.xcf.community.actuator;

import com.xcf.community.utils.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Joe
 * @ClassName DataBaseEndpoint.java
 * @Description
 * @createTime 2022年06月06日 20:34:00
 */
@Component
@Endpoint(id = "database")
@Slf4j
public class DataBaseEndpoint {


    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    // @ReadOperation : 只能以get方法访问该方法
    @ReadOperation
    public String checkConnection() {
        try (
                Connection connection = dataSource.getConnection();
        ){
            return CommunityUtil.getJsonString(0, "获取连接成功！");
        } catch (SQLException e) {
            log.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJsonString(1, "获取连接失败！");
        }
    }

}
