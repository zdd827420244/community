package com.nowcoder.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class SensitiveFilter {

    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT="***";

    //根节点
    private TrieNode rootNode =new TrieNode();

    @PostConstruct  //这个注解表明这个方法是个初始化方法，Bean构造后就会调用 而Bean在服务启动时 就会被构造
    public void init(){

        try(
        InputStream is=this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");   //填classes下的存放路径
        //获取类加载器  任意对象都能getClass() ，类加载器是从类路径中加载资源，也就是target中的classes目录下
        //程序一编译，所有代码都会编译到classes目录下，包括配置文件  所以我们就从classes下去读这个配置文件
        //得到的是一个字节流，要转成字符流
        //new InputStreamReader(is) 得到的就是一个字符流  但是依然不方便，还要转换成缓冲流
        BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword=reader.readLine())!=null) {
                //敏感词添加到前缀树
                this.addKeyWord(keyword);


            }

        }catch (IOException e) {
            logger.error("加载敏感词失败:"+e.getMessage());
        }
    }
    //将一个敏感词添加到前缀树
    private void addKeyWord(String keyword) {
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++) {
            char c=keyword.charAt(i);
            TrieNode subNode=tempNode.getSubNode(c);

            if(subNode==null) {
                //初始化子节点
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //指向子节点，进入下一轮循环
            tempNode=subNode;
            //设置结束表示
            if(i==keyword.length()-1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }
    //过滤敏感词  参数是带过滤文本  返回过滤后的文本
    public String filter(String text) {
        if(StringUtils.isBlank(text)) {
            return null;
        }
        //指针1
        TrieNode tempNode=rootNode;
        //指针2
        int begin=0;
        //指针3
        int position=0;
        //结果
        StringBuilder sb=new StringBuilder();
        while (position <text.length()) {
            char c=text.charAt(position);
            //跳过符号
            if(isSymbol(c)) {
                //若指针1处于根节点,将此符号计入结果，让指针2向下走
                if(tempNode==rootNode) {
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            //检测下级节点
            tempNode=tempNode.getSubNode(c);
            if(tempNode==null) {
                //以begin为开头的字符不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                begin++;
                position=begin;
                tempNode=rootNode;
            } else if(tempNode.isKeywordEnd()) {
                //发现敏感词,将begin到position这段字符串替换掉
                sb.append(REPLACEMENT);
                position++;
                begin=position;
                tempNode=rootNode;
            }else {
                //检查下一个字符
                position++;
            }
        }
        sb.append(text.substring(begin));
        return sb.toString();
    }
    //判断是否为符号
    private boolean isSymbol(Character c) {
        //0x2E80~0x9FFF是东亚文字范围  包括中日韩文  在西方也被认为是特殊字符，但我们不认为这是符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0x9FFF);//该方法判断是否为普通字符，普通字符返回true  特殊字符，也就是符号，返回false
    }

    //前缀树
    private class TrieNode {
        //关键词结束标识（是不是叶子结点）
        private boolean isKeywordEnd=false;

        //子节点(key是下级节点字符 value是下级节点)
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        //添加子节点
        public void addSubNode(Character c,TrieNode node) {
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }


}
