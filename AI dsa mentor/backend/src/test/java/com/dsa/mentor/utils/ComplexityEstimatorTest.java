package com.dsa.mentor.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComplexityEstimatorTest {

    private ComplexityEstimator complexityEstimator;

    @BeforeEach
    public void setUp() {
        complexityEstimator = new ComplexityEstimator();
    }

    @Test
    public void testConstantComplexity() {
        String code = """
            public int add(int a, int b) {
                int sum = a + b;
                return sum;
            }
            """;
        ComplexityEstimator.EstimationResult result = complexityEstimator.estimate(code, "java");
        assertEquals("O(1)", result.getTimeComplexity());
        assertEquals("O(1)", result.getSpaceComplexity());
        assertTrue(result.getConfidenceScore() >= 0.8);
    }

    @Test
    public void testLinearComplexity() {
        String code = """
            public int findMax(int[] nums) {
                int max = nums[0];
                for (int i = 1; i < nums.length; i++) {
                    if (nums[i] > max) {
                        max = nums[i];
                    }
                }
                return max;
            }
            """;
        ComplexityEstimator.EstimationResult result = complexityEstimator.estimate(code, "java");
        assertEquals("O(N)", result.getTimeComplexity());
        assertEquals("O(1)", result.getSpaceComplexity());
    }

    @Test
    public void testQuadraticComplexity() {
        String code = """
            public void bubbleSort(int[] arr) {
                int n = arr.length;
                for (int i = 0; i < n-1; i++) {
                    for (int j = 0; j < n-i-1; j++) {
                        if (arr[j] > arr[j+1]) {
                            int temp = arr[j];
                            arr[j] = arr[j+1];
                            arr[j+1] = temp;
                        }
                    }
                }
            }
            """;
        ComplexityEstimator.EstimationResult result = complexityEstimator.estimate(code, "java");
        assertEquals("O(N^2)", result.getTimeComplexity());
    }

    @Test
    public void testLogarithmicComplexity() {
        String code = """
            int binarySearch(int arr[], int l, int r, int x) {
                while (l <= r) {
                    int m = l + (r - l) / 2;
                    if (arr[m] == x) return m;
                    if (arr[m] < x) l = m + 1;
                    else r = m - 1;
                }
                return -1;
            }
            """;
        ComplexityEstimator.EstimationResult result = complexityEstimator.estimate(code, "cpp");
        assertEquals("O(log N)", result.getTimeComplexity());
    }

    @Test
    public void testHashMapSpaceComplexity() {
        String code = """
            import java.util.HashMap;
            class Solution {
                public int[] twoSum(int[] nums, int target) {
                    HashMap<Integer, Integer> map = new HashMap<>();
                    for (int i = 0; i < nums.length; i++) {
                        int comp = target - nums[i];
                        if (map.containsKey(comp)) {
                            return new int[] { map.get(comp), i };
                        }
                        map.put(nums[i], i);
                    }
                    return new int[]{};
                }
            }
            """;
        ComplexityEstimator.EstimationResult result = complexityEstimator.estimate(code, "java");
        assertEquals("O(N)", result.getTimeComplexity());
        assertEquals("O(N)", result.getSpaceComplexity());
    }
}
