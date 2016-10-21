package com.hfut.nanmu;

import org.w3c.dom.css.ElementCSSInlineStyle;

public class StadardTrieNode {
	
	TrieNode root = new TrieNode(' ');
	
	public void insert(String word)
	{
		char[] charWord = word.toCharArray();
		TrieNode preNode =root;
		for(int i = 0;i < charWord.length;i++)
		{
			if(preNode.childs.get(charWord[i]) == null)
			{
				TrieNode childNode = new TrieNode(charWord[i]);	
				preNode.childs.put(charWord[i], childNode);
			}
			preNode = preNode.childs.get(charWord[i]);
			
			if(i == charWord.length-1)
			{
				preNode.isEnd = true;
			}
		}
	}
	
	public boolean match(String word) 
	{
		char[] charWord = word.toCharArray();
		TrieNode preNode = root;
		for(int i = 0;i < charWord.length;i++)
		{
//			System.out.println("111"+preNode.childs.keySet());
			if(preNode.childs.get(charWord[i]) == null)
			{
				return false;
			}
			
			preNode = preNode.childs.get(charWord[i]);
			
			
			if(i == charWord.length-1 && preNode.isEnd == true)
			{
				return true;
			}
		}
		return false;
	}
	
	public String segment(String content)
	{
	 	StringBuffer result = new StringBuffer();
		for(int i = 0 ;i < content.length();)
		{
			String tempStr = content.substring(i);
			if(tempStr.length() != 1)
			{
				int maxLen = 6;
				if(tempStr.length() < maxLen)
				{
					maxLen = tempStr.length();
				}
				tempStr = tempStr.substring(0,maxLen);
				
				while(!match(tempStr))
				{
					if(tempStr.length() > 1)
					{
						tempStr = tempStr.substring(0,tempStr.length()-1);
					}
					else
					{
						result.append(tempStr+'/');
						break;
					}
				}
				if(match(tempStr))
				{
					result.append(tempStr+'/');
				}
				i=i+tempStr.length();
			}
		}
		return result.toString();
	}
}
