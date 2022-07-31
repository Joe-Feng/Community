package com.xcf.community.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joe
 * @ClassName SensitiveFilter.java
 * @Description
 * @createTime 2022年05月15日 16:46:00
 */
@Component
@Slf4j
public class SensitiveFilter {
    //替换符
    private  static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * //@PostConstruct ：调用该类的构造器后自动执行该方法
     * 初始化前缀树
     */
    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyWord(keyword);
            }
        } catch (Exception e) {
           log.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    private void addKeyWord(String keyword) {
        TrieNode tempNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            //判断是否存在值为c的子节点
            if(subNode == null){
                //初试化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            //指向子节点，进入下一轮循环
            tempNode = subNode;

            //设置结束标志
            if(i == keyword.length() - 1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 需要过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        StringBuilder result = new StringBuilder();
        //pointer1
        TrieNode p1 = rootNode;
        //pointer2
        int begin = 0;
        //pointer3
        int position = 0;
        while (begin < text.length()) {

            char c = text.charAt(position);

            //跳过符号
            if (isSymbol(c)){
                //指针1处于根节点，则将此符号计入结果，让指针2向下走一步
                if(p1 == rootNode){
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            //检查下级节点
            p1 = p1.getSubNode(c);
            if(p1 == null){
                //以 begin 开头的字符串不是敏感词
                result.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                p1 = rootNode;
            }else if(p1.isKeyWordEnd()){
                //发现敏感词，将begin-position字符替换掉
                result.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //重新指向根节点
                p1 = rootNode;
            }else {
                // 检查下一个字符
               if(position < text.length() - 1){
                   position++;
               }else {
                   position = begin;
               }
            }
        }

        return result.toString();
    }


    /**
     * 判断字符是否为特殊字符
     * @param c
     * @return
     */
    private boolean isSymbol(Character c){
        //0x2E80 ~ 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode{
        //关键词结束标志
        private boolean isKeyWordEnd = false;

        //子节点(key：下级字符 value: 下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();



        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
