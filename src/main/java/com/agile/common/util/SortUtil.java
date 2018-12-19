package com.agile.common.util;

/**
 * Created by 佟盟 on 2018/8/27
 */
public class SortUtil {
    public static int getMiddle(int[] numbers, int low, int high) {
        int temp = numbers[low]; //数组的第一个作为中轴
        while (low < high) {
            while (low < high && numbers[high] >= temp) {
                high--;
            }
            numbers[low] = numbers[high];//比中轴小的记录移到低端
            while (low < high && numbers[low] <= temp) {
                low++;
            }
            numbers[high] = numbers[low]; //比中轴大的记录移到高端
        }
        numbers[low] = temp; //中轴记录到尾
        return low; // 返回中轴的位置
    }

    /**
     * 快排
     *
     * @param array  数组
     * @param low    最低位
     * @param height 最高位
     */
    public static void fastSort(int[] array, int low, int height) {
        if (low < height) {
            int middle = getMiddle(array, low, height);
            fastSort(array, low, middle - 1);
            fastSort(array, middle + 1, height);
        }
    }

    public static void fastSort(int[] array) {
        fastSort(array, 0, array.length - 1);
    }


    public static void bubbleSort(int[] numbers) {
        int temp = 0;
        int size = numbers.length;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (numbers[j] > numbers[j + 1])  //交换两数位置
                {
                    temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] a = {3, 3, 4, 4, 33, 33, 33, 11, 11, 11, 11, 2, 3, 4, 2, 1, 7, 3, 8, 3};
//        long q = System.currentTimeMillis();
//        bubbleSort(a);
//        System.out.println(System.currentTimeMillis()-q);

        fastSort(a);
        for (int s : a) {
            System.out.println(s);
        }


    }
}
