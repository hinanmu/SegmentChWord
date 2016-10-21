package com.hfut.nanmu;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore.Entry;

public class MMSegment {
	public static StadardTrieNode wordTrie =  new StadardTrieNode();
	
	public static void  main(String[] args) throws IOException 
	{
	
		 FileReader fileReader = new FileReader("sentences_segment2.txt");
		 BufferedReader bufferedReader = new BufferedReader(fileReader);  
		 String line = " ";
		 while((line = bufferedReader.readLine()) != null)
		 { 
			 String[] wordArr = line.split("  ");
			 for(int i = 0;i < wordArr.length;i++)
			 {
				 wordTrie.insert(wordArr[i]);
			 }
		
		 }
		 bufferedReader.close();

		System.out.println(wordTrie.segment("�����������"));
		
	 }
		
}

