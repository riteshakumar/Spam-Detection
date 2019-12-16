import java.util.Arrays;
import java.util.Scanner;

public class MergeSort {
    void sort(int array[], int low, int end){ 
        if (low < end){ 
            int mid = (low+end)/2; 
            sort(array, low, mid); 			//Sorting Firt half of given array
            sort(array , mid+1, end); 		//Sorting of Second half of given array
            merge(array, low, mid, end); 	//Merging the two sorted array
        } 
    } 

    void merge(int array[], int low, int mid, int end){ 
        int size1 = mid + 1 - low, size2 = end - mid; 				//Intialize the size for left and right array.
        int Left[] = new int [size1], Right[] = new int [size2]; 
  
        for (int i=0; i<size1; ++i) 
            Left[i] = array[low + i]; 
        for (int j=0; j<size2; ++j) 
            Right[j] = array[mid + 1+ j]; 

        int i = 0, j = 0, k = low; 
        
        while (i < size1 && j < size2) { 						//Compare left and right array until the finish
            if (Left[i] <= Right[j]) { 
                array[k] = Left[i]; 
                i++; 
            } 
            else{ 
                array[k] = Right[j]; 
                j++; 
            } 
            k++; 
        } 

        while (i < size1){ 							//remaining element from left array
            array[k] = Left[i]; 
            i++; 
            k++; 
        } 
  
        while (j < size2){ 						//remaining element from right array
            array[k] = Right[j]; 
            j++; 
            k++; 
        } 
    } 

    public static void main(String args[]){ 
    	MergeSort ob = new MergeSort(); 
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

        ob.sort(intArray, 0, intArray.length-1); 
        System.out.println("\nArray after Sorting"); 
        System.out.println(Arrays.toString(intArray));
        s.close();
    } 
} 