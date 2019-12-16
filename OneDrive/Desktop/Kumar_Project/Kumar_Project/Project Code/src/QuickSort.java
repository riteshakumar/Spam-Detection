import java.util.Arrays;
import java.util.Scanner;

public class QuickSort {
  public static void sort(int[] array) {
    recursiveQuickSort(array, 0, array.length - 1);
  }

  public static void recursiveQuickSort(int[] array, int left, int right) {	//Quicksort recursively
    int size = right - left + 1;
    if (size <= 3)
      manualSort(array, left, right);
    else {
      double median = med3(array, left, right);
      int partition = partition(array, left, right, median);
      recursiveQuickSort(array, left, partition - 1);				//quicksort for left portion of partition
      recursiveQuickSort(array, partition + 1, right);				//quicksort for right portion of partition
    }
  }

  public static int med3(int[] array, int left, int right) {			//Dividing array based on median
    int center = (left + right) / 2;									// Sort left, middle, right

    if (array[left] > array[center])
      swap(array, left, center);

    if (array[left] > array[right])
      swap(array, left, right);

    if (array[center] > array[right])
      swap(array, center, right);

    swap(array, center, right - 1);
    return array[right - 1];
  }

  public static void swap(int[] array, int index1, int index2) {	//Swap method for values exchange
    int temp = array[index1];
    array[index1] = array[index2];
    array[index2] = temp;
  }

  public static int partition(int[] array, int left, int right, double pivot) {	//partition array values less than pivot to the left and more than pivot to right
    int leftPtr = left;
    int rightPtr = right - 1;

    while (true) {
      while (array[++leftPtr] < pivot)
        ;
      while (array[--rightPtr] > pivot)
        ;
      if (leftPtr >= rightPtr)
        break;
      else
        swap(array, leftPtr, rightPtr);
    }
    swap(array, leftPtr, right - 1);
    return leftPtr;
  }

  public static void manualSort(int[] array, int left, int right) {	//Sort small elements to left and large elements to right
    int size = right - left + 1;
    if (size <= 1)
      return;
    if (size == 2) {
      if (array[left] > array[right])
        swap(array, left, right);
      return;
    } else {
      if (array[left] > array[right - 1])
        swap(array, left, right - 1);
      if (array[left] > array[right])
        swap(array, left, right);
      if (array[right - 1] > array[right])
        swap(array, right - 1, right);
    }
  }
  
  public static void main(String[] args) {
	    Scanner s = new Scanner(System.in);
	    System.out.println("Enter the Size of the array:");
	    int length = s.nextInt();
	    int [] array = new int[length];

        System.out.println("Enter the elements of the array:");
        for(int i=0; i<array.length; i++ ) {
        	array[i] = s.nextInt();
        }
	    System.out.println("Provided Array"); 
        System.out.println(Arrays.toString(array));

	    sort(array);
        System.out.println("\nArray after Sorting"); 
        System.out.println(Arrays.toString(array));
        s.close();
	  }

}