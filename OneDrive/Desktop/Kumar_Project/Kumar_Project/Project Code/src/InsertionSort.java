import java.util.Arrays;
import java.util.Scanner;

public class InsertionSort {
    void sort(int array[]) 
    { 
        int n = array.length; 
        for (int i = 1; i < n; i++) { 
            int key = array[i]; 
            int j = i - 1; 
            while (j >= 0 && array[j] > key) { 			//Move elements of array one position ahead which are greater then the key value 
                array[j + 1] = array[j]; 
                j--; 
            } 
            array[j + 1] = key; 
        } 
    } 
  
    public static void main(String args[]) 
    { 
    	InsertionSort ob = new InsertionSort(); 
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
