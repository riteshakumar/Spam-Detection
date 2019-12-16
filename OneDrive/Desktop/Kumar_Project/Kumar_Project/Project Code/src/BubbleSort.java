import java.util.Arrays;
import java.util.Scanner;

public class BubbleSort {
	    void sort(int array[]) 
	    { 
	        int n = array.length; 
	        for (int i = 0; i < n-1; i++) 
	            for (int j = 0; j < n-i-1; j++) 
	                if (array[j] > array[j+1]) 	//Swap the elements
	                { 
	                    int temp = array[j]; 
	                    array[j] = array[j+1]; 
	                    array[j+1] = temp; 
	                } 
	    } 
	  
	    public static void main(String args[]) 
	    { 
	        BubbleSort ob = new BubbleSort(); 
		    Scanner s = new Scanner(System.in);
		    System.out.println("Enter the Size of the array:");
		    int length = s.nextInt();
		    int [] intArray = new int[length];

	        System.out.println("Enter the elements of the array:");
	        for(int i=0; i<intArray.length; i++ ) {
	        	intArray[i] = s.nextInt();
	        }
		    System.out.println("Provided Array"); 
	        System.out.println(Arrays.toString(intArray));

	        ob.sort(intArray);
	        System.out.println("\nArray after Sorting"); 
	        System.out.println(Arrays.toString(intArray));
	        s.close();
	    } 
	} 

