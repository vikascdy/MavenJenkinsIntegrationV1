package com.staticcodeanalysis;

public class DemoClass {
	
	private String str = new String("Findbug");
	static int five = 5;
	public DemoClass(){}
	public DemoClass(String str)
	{
		this.str=str;
	}
	public String printName(){
		return str;
	}
	public void iterate(){
		for (int i=0;i<5;i++){
			for(int j=0;j<5;j++){
			}
		}
	}
	
	public static void main(String[] args)
	{
		DemoClass dc = new DemoClass();
		System.out.println("Name is :" + dc.printName());
	}
}
