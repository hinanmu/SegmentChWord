package com.hfut.nanmu;

import java.util.HashMap;  

public class TrieNode {  
  
    /**���ؼ��֣���ֵΪ���Ĵ��е�һ����*/  
    public char key=(char)0;  
    /**��������ڴ����ĩβ����bound=true*/  
    public boolean isEnd=false;  
    /**ָ����һ������ָ��ṹ��������ŵ�ǰ���ڴ��е���һ���ֵ�λ��*/  
    public HashMap<Character,TrieNode> childs=new HashMap<Character,TrieNode>();  
      
    public TrieNode(){  
    }  
      
    public TrieNode(char k){  
        this.key=k;  
    }  
    
}  