package com.hfut.nanmu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.text.FieldPosition;

public class wordToState {
	public static void  main(String[] args) throws IOException
	{
		FileReader fileReader = new FileReader("sentences_segment2.txt");
		FileWriter fileWriter = new FileWriter(new File("wordCallout.txt"));
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = " ";
		while((line=bufferedReader.readLine()) != null)
		{
			String[] words = line.split("  ");
			if(words.length == 1)
			{
				continue;
			}
			for(int i = 0;i < words.length - 1;i++)
			{
				int length = words[i].length();
				
				if(length == 0)
				{
					continue;
				}
				else if(length == 1)
				{
					fileWriter.write("S");
				}
				else if(length == 2)
				{
					fileWriter.write("BE");
				}
				else 
				{
					fileWriter.write("B");
					for(int j = 0;j < length-2;j++)
					{
						fileWriter.write("M");
					}
					fileWriter.write("E");
				}
			}
			fileWriter.write("\r\n");
		}
	
	}

}
