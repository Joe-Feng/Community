package com.xcf.community.service;

import java.util.Date;

/**
 * @author Joe
 * @ClassName IDataService.java
 * @Description
 * @createTime 2022年06月01日 20:28:00
 */
public interface IDataService {
    //将指定ip计入UA
    void recordUV(String ip);

    //统计指定日期范围内的UA
    long calculateUV(Date start, Date end);

    //将指定用户计入DAU
    void recordDAU(int userId);

    //统计指定日期方位内的DAU
    long calculateDAU(Date start, Date end);
}
