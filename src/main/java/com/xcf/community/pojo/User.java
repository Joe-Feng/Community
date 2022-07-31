package com.xcf.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.util.Date;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName User.java
 * @Description
 * @createTime 2022年05月09日 20:05:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Validated
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
//    @Email(message = "邮箱格式不正确")
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
