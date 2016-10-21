package com.hfut.nanmu;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.State;
import java.nio.Buffer;
import java.security.KeyStore.Entry;
import java.text.FieldPosition;
import java.util.Arrays;
import java.util.HashMap;

import javax.sound.sampled.Line;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

public class HMM {
	
	private static double[][] A;		// 状态转移概率矩阵
	private static double[][] B;		// 观察符号概率矩阵
	private static double[] pi;	
	private static char[] keyArr;
	private static int[] state= {0,1,2,3};
	private static int[] observation;
	private static int[] X;
	private static String Punctuation = "。？！，、；：「」『』‘’“”（）〔〕【】—…–．《》〈〉";
	private static StringBuffer output = new StringBuffer();
	
	public static void  main(String[] args) throws IOException
	{
		buildAPi("wordCallout.txt");
		buildB("wordFile.txt","wordCallout.txt");
		
//		String inputSentence = "你结婚了吗？结婚了。";
		
//		input(inputSentence);
		
		System.out.println(output);
		
		JTextArea input=new JTextArea(10,210);
	    JTextArea show=new JTextArea("Result:\n",10,210);
		JButton btn = new JButton("分词");
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout(FlowLayout.LEADING));
		frame.setSize(500, 500);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);//¾ÓÖÐ
        show.setEditable(false);
        input.setEditable(true);
        
        frame.add(input);
        frame.add(show);
        frame.add(btn);
        
        btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				input(input.getText().toString());
				show.setText(output.toString());
			}
		});
        frame.setVisible(true);
		
		
	}
	
	public static void input(String inputSentence)
	{
		int begin = 0;
		int end = inputSentence.length();
		String input = "";
		
		for(int i = 0 ;i < inputSentence.length();i++)
		{
			if(Punctuation.indexOf(inputSentence.charAt(i)) != -1)
			{
				end = i+1;
				input = inputSentence.substring(begin, end);
				
				if(begin != end)
				{
					observation = new int[input.length()-1];
					for(int j = 0;j < observation.length;j++)
					{
						observation[j] = String.valueOf(keyArr).indexOf(input.charAt(j));
					}
					viterbi();
					write(input);
				}
				
				begin = end;
				end = inputSentence.length();
			
				
			}
		}
		
		
	}
	
	public static void write(String input)
	{
		
		for(int i = 0;i<X.length;i++)
		{
			if(X[i] == 1 || X[i] == 2)
			{
				output.append(input.charAt(i));
				output.append("/");
			}
			else if(X[i] == 0 && i == X.length-1)
			{
				output.append(input.charAt(i));
				output.append("/");
			}
			else
			{
				output.append(input.charAt(i));
			}
		}
		output.append(input.charAt(input.length()-1)+"/");
	}
		
	public static void buildAPi(String file) throws IOException

	{
		long[][] count = new long[4][4];
		A = new double[4][4];
		pi = new double[4];
		
		FileReader fileReader  = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = " ";
		while((line = bufferedReader.readLine()) != null)
		{
			for(int i = 0;i < line.length()-1;i++)
			{
				switch (line.charAt(i)) 
				{
			
					case 'B':
						switch (line.charAt(i+1)) 
						{
						
						case 'B':
							A[0][0]++;
							break;
						case 'E':
							A[0][1]++;
							break;
						case 'S':
							A[0][2]++;
							break;
						case 'M':
							A[0][3]++;
							break;		
						default:
							break;
						}
				
						break;
					case 'E':
						switch (line.charAt(i+1)) 
						{
						
						case 'B':
							A[1][0]++;
					
							break;
						case 'E':
							A[1][1]++;
							break;
						case 'S':
							A[1][2]++;
							break;
						case 'M':
							A[1][3]++;
							break;		
						default:
							break;
						}
						break;
					case 'S':
						switch (line.charAt(i+1)) 
						{
						
						case 'B':
							A[2][0]++;					
							break;
						case 'E':
							A[2][1]++;
							break;
						case 'S':
							A[2][2]++;
							break;
						case 'M':
							A[2][3]++;
							break;		
						default:
							break;
						}
						break;
					case 'M':
						switch (line.charAt(i+1)) 
						{
						
						case 'B':
							A[3][0]++;
					
							break;
						case 'E':
							A[3][1]++;
							break;
						case 'S':
							A[3][2]++;
							break;
						case 'M':
							A[3][3]++;
							break;		
						default:
							break;
						}
						break;		
					default:
						break;
				}
			}
			
			switch (line.charAt(0)) 
			{
			
			case 'B':
				pi[0]++;
				break;
			case 'S':
				pi[2]++;
				break;
			default:
				break;
			}
			
		}
		pi[0] = pi[0] / (pi[0]+pi[2]);
		pi[2] = 1 - pi[0];
		double[] tempA = new double[4];
		
		for(int i = 0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				tempA[i] += A[i][j];
			}

		}
		
		for(int i = 0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				 A[i][j] = A[i][j]/tempA[i];
			}

		}
		
//		System.out.println("A:");
//		for(int i = 0;i<4;i++)
//		{
//			for(int j=0;j<4;j++)
//			{
//				System.out.print(A[i][j]+"\t");
//			}
//			System.out.println();
//		}
//		
//		System.out.println("pi:");
//		for(int i = 0;i < 4;i++)
//		{
//			System.out.println(pi[i]);
//		}
		
		
		
	}

	public static void buildB(String wordFile ,String stateFile) throws IOException
	{
		
		HashMap<Character,Double> wordStaNumB=new HashMap<Character,Double>();  
		HashMap<Character,Double> wordStaNumE=new HashMap<Character,Double>();  
		HashMap<Character,Double> wordStaNumS=new HashMap<Character,Double>();  
		HashMap<Character,Double> wordStaNumM=new HashMap<Character,Double>();  
		
		FileReader wordFR = new FileReader(wordFile);
		FileReader stateFR  = new FileReader(stateFile);
		BufferedReader wordBR = new BufferedReader(wordFR);
		BufferedReader stateBR = new BufferedReader(stateFR);
		String wordLine = " ";
		String stateLine = " ";
		
		while((wordLine = wordBR.readLine()) != null && (stateLine =stateBR.readLine()) != null)
		{
			String temp = wordLine.replaceAll("  ", "");
			temp = temp.substring(0, temp.length()-1);
			
			if(stateLine.length() != temp.length() && stateLine.length() == 1)
			{
				continue;
			}
			
			for(int i = 0; i< stateLine.length() - 1;i++)
			{
				
				switch (stateLine.charAt(i)) {
				case 'B':
					if(wordStaNumB.containsKey(temp.charAt(i)))
					{
						wordStaNumB.put(temp.charAt(i),wordStaNumB.get(temp.charAt(i)).doubleValue()+1);
					}
					else 
					{
						wordStaNumB.put(temp.charAt(i),1.0);
					}
					break;
				case 'E':
					if(wordStaNumE.containsKey(temp.charAt(i)))
					{
						wordStaNumE.put(temp.charAt(i),wordStaNumE.get(temp.charAt(i)).doubleValue()+1);
					}
					else 
					{
						wordStaNumE.put(temp.charAt(i),1.0);
					}
					break;
				case 'S':
					if(wordStaNumS.containsKey(temp.charAt(i)))
					{
						wordStaNumS.put(temp.charAt(i),wordStaNumS.get(temp.charAt(i)).doubleValue()+1);
					}
					else 
					{
						wordStaNumS.put(temp.charAt(i),1.0);
					}
					break;
				case 'M':
					if(wordStaNumM.containsKey(temp.charAt(i)))
					{
						wordStaNumM.put(temp.charAt(i),wordStaNumM.get(temp.charAt(i)).doubleValue()+1);
					}
					else 
					{
						wordStaNumM.put(temp.charAt(i),1.0);
					}
					break;
					
				default:
					break;
				}
				
				if(!wordStaNumB.containsKey(temp.charAt(i)))
				{
					wordStaNumB.put(temp.charAt(i), 0.0);
				}
				if(!wordStaNumE.containsKey(temp.charAt(i)))
				{
					wordStaNumE.put(temp.charAt(i), 0.0);
				}
				if(!wordStaNumS.containsKey(temp.charAt(i)))
				{
					wordStaNumS.put(temp.charAt(i), 0.0);
				}
				if(!wordStaNumM.containsKey(temp.charAt(i)))
				{
					wordStaNumM.put(temp.charAt(i), 0.0);
				}
			}
		}
		
		
		double BNum = 0;
		double ENum = 0;
		double MNum = 0;
		double SNum = 0;
		
		for(java.util.Map.Entry<Character, Double> e:wordStaNumB.entrySet())
		{
			BNum += e.getValue();
		}
		for(java.util.Map.Entry<Character, Double> e:wordStaNumE.entrySet())
		{
			ENum += e.getValue();
		}
		for(java.util.Map.Entry<Character, Double> e:wordStaNumS.entrySet())
		{
			SNum += e.getValue();
		}
		for(java.util.Map.Entry<Character, Double> e:wordStaNumM.entrySet())
		{
			MNum += e.getValue();
		}
		
		
		for(java.util.Map.Entry<Character, Double> e:wordStaNumB.entrySet())
		{
			wordStaNumB.put(e.getKey(), e.getValue()/BNum);
		}
		for(java.util.Map.Entry<Character, Double> e:wordStaNumE.entrySet())
		{
			wordStaNumE.put(e.getKey(), e.getValue()/ENum);
		}
		for(java.util.Map.Entry<Character, Double> e:wordStaNumS.entrySet())
		{
			wordStaNumS.put(e.getKey(), e.getValue()/SNum);
		}
		for(java.util.Map.Entry<Character,  Double> e:wordStaNumM.entrySet())
		{
			wordStaNumM.put(e.getKey(), e.getValue()/MNum);
		}
		
		for(java.util.Map.Entry<Character,  Double> e:wordStaNumB.entrySet())
		{
			System.out.println(e.getKey()+"  "+e.getValue());
		}
		
//		System.out.println(BNum);
//		System.out.println(ENum);
//		System.out.println(SNum);
//		System.out.println(MNum);
		
		Object[] key_arr = wordStaNumB.keySet().toArray();  
		Arrays.sort(key_arr);
		
		B = new double[4][key_arr.length];
		
		for(int i = 0 ;i < key_arr.length;i++)
		{
			B[0][i] = wordStaNumB.get(key_arr[i]);
			B[1][i] = wordStaNumE.get(key_arr[i]);
			B[2][i] = wordStaNumS.get(key_arr[i]);
			B[3][i] = wordStaNumM.get(key_arr[i]);
		}
		
		keyArr = new char[key_arr.length];
		for(int i = 0 ;i < key_arr.length ;i++)
		{
			keyArr[i] =(char) key_arr[i] ;
		}
//		Object[] key_arr2 = wordStaNumE.keySet().toArray();  
//		Arrays.sort(key_arr2); 
//		Object[] key_arr3 = wordStaNumM.keySet().toArray();  
//		Arrays.sort(key_arr3); 
//		Object[] key_arr4 = wordStaNumS.keySet().toArray();  
//		Arrays.sort(key_arr4); 
//		
//		System.out.println(key_arr[100]);
//		System.out.println(key_arr2[100]);
//		System.out.println(key_arr3[100]);
//		System.out.println(key_arr4[100]);
		
		
	}
	
	public static void viterbi()
	{
		X = new int[observation.length];
		double[][] T1 = new double[4][observation.length];
		int[][] T2 = new int[4][observation.length];
		
		System.out.println(state.length);
		
		for(int i = 0;i < state.length;i++)
		{
			System.out.println(pi[i]);
			System.out.println(B[i][observation[0]]);
			T1[i][0] = pi[i]*B[i][observation[0]];
			T2[i][0] = 0;
		}
		
		
		double maxT1 = 0;
		int maxT2 = 0;
		double tempT = 0;
		for(int i = 1;i < observation.length;i++)
		{
			for(int j = 0;j < state.length;j++)
			{
				for(int k = 0;k < state.length;k++)
				{
					
					double haha = T1[k][i-1]*A[k][j]*B[j][observation[i]];
					
					if(T1[k][i-1]*A[k][j]*B[j][observation[i]] > maxT1)
					{
						maxT1 = T1[k][i-1]*A[k][j]*B[j][observation[i]] ;
					}
					
					double hha = T1[k][i-1]*A[k][j] ;
					
					if(T1[k][i-1]*A[k][j] > tempT)
					{
						tempT = T1[k][i-1]*A[k][j];
						maxT2 = state[k];
					}
				}
				
				
				T1[j][i] = maxT1;
				T2[j][i] = maxT2;
				maxT1 = 0;
				tempT = 0;
			}
		}
		
//		for(int i = 0;i < state.length;i++)
//		{
//			for(int j = 0;j < observation.length;j++)
//			{
//				System.out.print(T1[i][j]+"   ");
//			}
//			System.out.println();
//		}
		
		tempT = 0.0;
		int[] Z = new int[observation.length];
		for(int i = 0;i < state.length;i++)
		{
			if(T1[i][observation.length-1] > tempT)
			{
				tempT = T1[i][observation.length-1] ;
				Z[observation.length-1] = state[i];
			}
		}
		
		
		X[observation.length-1] = state[Z[observation.length-1]];
		
		for(int i = observation.length-1 ;i > 0;i--)
		{
			Z[i-1] = T2[Z[i]][i];
			X[i-1] = state[Z[i-1]];
			
		}
		
//		System.out.print("状态序列：");
//		for(int i = 0 ;i < X.length;i++)
//		{
//			System.out.print(X[i]+"  ");
//		}
	}
}
