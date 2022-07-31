package com.xcf.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName LoginTicket.java
 * @Description
 * @createTime 2022年05月13日 20:02:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
