import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

public class ExecutionTime {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random r = new Random();
		long start,end;
		QuickSort q =new QuickSort();
		MergeSort m=new MergeSort();
		HeapSort h=new HeapSort();
		InsertionSort i1 = new InsertionSort();
		BubbleSort b = new BubbleSort();
		int[] arr = new int[1000];
		int[] temp;
		for(int i=0;i<10;i++) {
			int n = r.nextInt(15 - 5 + 1)+ 5;
			temp = new int[n];
			for(int j=0;j<n;j++) {
				int num = r.nextInt(1000);
				arr[j]=num;
			}
			start = System.nanoTime();
			i1.sort(arr);
			end = System.nanoTime();
			System.out.println("size=" +n+ ", execution time:"+(end-start));
		}
		  
	}

}