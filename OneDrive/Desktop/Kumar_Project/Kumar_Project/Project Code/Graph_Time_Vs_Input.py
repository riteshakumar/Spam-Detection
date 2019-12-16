import time
from numpy import sort
from numpy.random import randint
import matplotlib.pyplot as plt
import sorting
elements = []
times = []

#size = [10,15,20,14,18,6,7,13,12,8]
#size.sort()
#elements = [13,8,11,10,15,5,15,9,8,15]
#elements.sort()
#times=[319800,211300,269900,247800,362600,146300,343000,140800,60200,106300]
#times.sort()


for i in range(1, 10):
    a = randint(0, 100* i, i*100)

    start = time.perf_counter()
    sorting.bubble(a)
    #sorting.maxheap(a)
    #sorting.quick(a)
    #sorting.merge(a)
    #sort(a)
    end = time.perf_counter()
    print(len(a), "Elements Sorted by BubbleSort in ", end - start)
    elements.append(len(a))
    times.append(end - start)

plt.xlabel('List Length')
plt.ylabel('Time Complexity/Running Time')
plt.plot(elements, times, label='Bubble Sort')
plt.grid()
plt.legend()
plt.show()