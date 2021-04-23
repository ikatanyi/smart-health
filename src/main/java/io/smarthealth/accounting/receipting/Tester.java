package io.smarthealth.accounting.receipting;

public class Tester {
    public static void main(String[] args) {
        int len = (int)Math.log10(3692564)+1;
        System.err.println(len);
        System.err.println(lengthOfInteger(3692564));
        System.err.println(findNumbers(new int[]{12,345,2,6,7896}));
    }
    public static int lengthOfInteger(int number){
        int length=0;
        int temp=1;
        while(temp <= number){
            length++;
            temp*=10;
        }
        return length;
    }

    public static int findNumbers(int[] nums){
        int count = 0;
        for (int num: nums) {
            int len = (int)Math.log10(num)+1;
            if(len%2 == 0){
                count++;
            }
        }
        return count;
    }
}
