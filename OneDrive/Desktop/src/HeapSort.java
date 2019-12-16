import java.util.Arrays;
import java.util.Scanner;

public class HeapSort { 
	public void sort(int array[]) { 
		int n = array.length; 
		for (int i = n / 2 - 1; i >= 0; i--) 		//Intialize the heap
			heapify(array, n, i); 

		for (int i=n-1; i>=0; i--) { 		//Mving root to the end
			int temp = array[0]; 
			array[0] = array[i]; 
			array[i] = temp; 
			heapify(array, i, 0); 
		} 
	} 

	void heapify(int array[], int n, int i) { 
		int largest = i;  						//Intialize root with largest element
		int left = 2*i + 1;						//Intialize left of binary heap tree
		int right = 2*i + 2;  					//Intialize right of binary heap tree

		if (left < n && array[left] > array[largest]) 	//Check left tree child greater than root
			largest = left; 

		if (right < n && array[right] > array[largest]) 	//Check right tree child greater than root
			largest = right; 

		if (largest != i) { 
			int swap = array[i]; 
			array[i] = array[largest]; 
			array[largest] = swap; 
			heapify(array, n, largest); 			//Recursively heapify sub tree
		} 
	} 

	public static void main(String args[]) 
	{ 
		HeapSort ob = new HeapSort(); 
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
