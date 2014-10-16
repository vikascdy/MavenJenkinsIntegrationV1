/**
 * Created by saurprak on 15-10-2014.
 */
public class hello {

   public static void main(String args[])
   {    int a=0;
       int b=0;
       System.out.print("hello");
       for(int i=0 ; i<100 ; i++){
        a=a+1;
           System.out.println("A value::::"+a);
        b=b+a;
           System.out.print(" \nB value"+b);
       }
	int importantData =1;
    int[]  buffer = new int[10];

    for (int i =0; i < 15; i++)
      buffer[i] = 7;

    System.out.println("after buffer overflow ");
    System.out.println("Important data  = "+importantData);

   }
}
