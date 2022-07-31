package com.xcf.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName Page.java
 * @Description 封装分页相关信息
 * @createTime 2022年05月11日 10:24:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page {
    //当前页码
    @Min(value = 1, message = "当前页不能小于1")
    private int current = 1;

    //显示上限
    @Size(min = 1, max = 100, message = "页面显示上限为100条数据")
    private int limit = 10;

    //数据总数（用于计算总页数）
    @Min(value = 0, message = "数据总数不能为负数")
    private int rows;

    //查询路径
    private String path;

    /**
     *计算当前页起始数据下标
     * @return
     */
    public int getOffset(){
        return (current - 1) * limit;
    }

    /**
     * 计算总页数
     * @return
     */
    public int getTotal(){
        if(rows % limit == 0){
            return rows / limit;
        }else{
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取终止页码
     * @return
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
