package com.aidsamentor.complexity.service.impl;

import com.aidsamentor.complexity.dto.ComplexityEstimateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComplexityEstimatorServiceImplTest {

    private ComplexityEstimatorServiceImpl estimatorService;

    @BeforeEach
    void setUp() {
        estimatorService = new ComplexityEstimatorServiceImpl();
    }

    @Test
    void detectsConstantTime() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int first(int[] nums) {
                    return nums[0];
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(1)");
        assertThat(response.spaceComplexity()).isEqualTo("O(1)");
        assertThat(response.confidence()).isBetween(0.6, 0.95);
        assertThat(response.explanation()).contains("no loops");
    }

    @Test
    void detectsLinearTimeLoop() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int sum(int[] nums) {
                    int total = 0;
                    for (int num : nums) {
                        total += num;
                    }
                    return total;
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(N)");
        assertThat(response.spaceComplexity()).isEqualTo("O(1)");
        assertThat(response.explanation()).contains("1 loop");
    }

    @Test
    void detectsLogarithmicTimeBinarySearch() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int search(int[] nums, int target) {
                    int left = 0;
                    int right = nums.length - 1;
                    while (left <= right) {
                        int mid = left + (right - left) / 2;
                        if (nums[mid] == target) return mid;
                        if (nums[mid] < target) left = mid + 1;
                        else right = mid - 1;
                    }
                    return -1;
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(log N)");
        assertThat(response.spaceComplexity()).isEqualTo("O(1)");
        assertThat(response.explanation()).contains("logarithmic");
    }

    @Test
    void detectsNLogNFromSorting() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int[] sortedSquares(int[] nums) {
                    Arrays.sort(nums);
                    return nums;
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(N log N)");
        assertThat(response.spaceComplexity()).isEqualTo("O(1)");
        assertThat(response.explanation()).contains("sorting");
    }

    @Test
    void detectsQuadraticNestedLoops() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int pairs(int[] nums) {
                    int count = 0;
                    for (int i = 0; i < nums.length; i++) {
                        for (int j = 0; j < nums.length; j++) {
                            count++;
                        }
                    }
                    return count;
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(N^2)");
        assertThat(response.spaceComplexity()).isEqualTo("O(1)");
        assertThat(response.explanation()).contains("nested loops");
    }

    @Test
    void detectsLinearRecursion() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int factorial(int n) {
                    if (n <= 1) return 1;
                    return n * factorial(n - 1);
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(N)");
        assertThat(response.spaceComplexity()).isEqualTo("O(N)");
        assertThat(response.explanation()).contains("recursive method call");
    }

    @Test
    void detectsLogarithmicRecursion() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int power(int n) {
                    if (n <= 1) return 1;
                    return power(n / 2);
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(log N)");
        assertThat(response.spaceComplexity()).isEqualTo("O(log N)");
    }

    @Test
    void detectsExponentialRecursion() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int fib(int n) {
                    if (n <= 1) return n;
                    return fib(n - 1) + fib(n - 2);
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(2^N)");
        assertThat(response.spaceComplexity()).isEqualTo("O(N)");
        assertThat(response.explanation()).contains("multiple recursive branches");
    }

    @Test
    void detectsHashMapUsageAndLinearSpace() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int[] twoSum(int[] nums, int target) {
                    Map<Integer, Integer> seen = new HashMap<>();
                    for (int i = 0; i < nums.length; i++) {
                        if (seen.containsKey(target - nums[i])) return new int[] { seen.get(target - nums[i]), i };
                        seen.put(nums[i], i);
                    }
                    return new int[] {};
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(N)");
        assertThat(response.spaceComplexity()).isEqualTo("O(N)");
        assertThat(response.explanation()).contains("HashMap/Map usage");
    }

    @Test
    void detectsGraphTraversalAndBfs() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                void bfs(List<List<Integer>> graph, int start) {
                    boolean[] visited = new boolean[graph.size()];
                    Queue<Integer> queue = new LinkedList<>();
                    queue.add(start);
                    visited[start] = true;
                    while (!queue.isEmpty()) {
                        int node = queue.poll();
                        for (int next : graph.get(node)) {
                            if (!visited[next]) {
                                visited[next] = true;
                                queue.add(next);
                            }
                        }
                    }
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(V + E)");
        assertThat(response.spaceComplexity()).isEqualTo("O(N)");
        assertThat(response.explanation()).contains("graph traversal", "DFS/BFS");
    }

    @Test
    void detectsGraphTraversalAndDfs() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                void dfs(List<List<Integer>> adjacency, boolean[] visited, int node) {
                    visited[node] = true;
                    for (int next : adjacency.get(node)) {
                        if (!visited[next]) {
                            dfs(adjacency, visited, next);
                        }
                    }
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(V + E)");
        assertThat(response.spaceComplexity()).isEqualTo("O(N)");
        assertThat(response.explanation()).contains("DFS/BFS", "recursive method call");
    }

    @Test
    void ignoresCommentsWhenEstimating() {
        ComplexityEstimateResponse response = estimatorService.estimate("""
                int value(int x) {
                    // for (int i = 0; i < n; i++) {}
                    /* while (true) { } */
                    return x + 1;
                }
                """);

        assertThat(response.timeComplexity()).isEqualTo("O(1)");
        assertThat(response.spaceComplexity()).isEqualTo("O(1)");
    }
}
