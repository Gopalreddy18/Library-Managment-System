package com.dsa.mentor.utils;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ComplexityEstimator {

    @Getter
    @Builder
    public static class EstimationResult {
        private String timeComplexity;
        private String spaceComplexity;
        private double confidenceScore;
        private String details;
    }

    public EstimationResult estimate(String code, String language) {
        if (code == null || code.isBlank()) {
            return EstimationResult.builder()
                .timeComplexity("O(1)")
                .spaceComplexity("O(1)")
                .confidenceScore(0.5)
                .details("Empty source code provided.")
                .build();
        }

        // Clean comments to prevent false positives in complexity estimation
        String cleanedCode = cleanComments(code);

        boolean hasRecursion = checkRecursion(cleanedCode);
        int maxLoopNesting = checkLoopNesting(cleanedCode);
        boolean hasLogarithmicLoop = checkLogarithmicLoop(cleanedCode);
        boolean hasCollectionSpace = checkCollectionSpace(cleanedCode);
        boolean hasGraphOrTree = checkTreeGraphKeywords(cleanedCode);

        String estimatedTime = "O(N)";
        String estimatedSpace = "O(1)";
        double confidence = 0.7;
        StringBuilder explanation = new StringBuilder("Static analysis details: ");

        // Determine Time Complexity
        if (hasRecursion) {
            // Check if exponential recursion (e.g. Fibonacci: fib(n-1) + fib(n-2))
            boolean isExponentialRecursion = checkExponentialRecursion(cleanedCode);
            if (isExponentialRecursion) {
                estimatedTime = "O(2^N)";
                explanation.append("Multiple recursive calls detected, suggesting exponential execution tree.");
            } else {
                estimatedTime = "O(N)";
                explanation.append("Single recursive call structure detected.");
            }
            estimatedSpace = "O(N)"; // recursion stack space
            confidence = 0.75;
        } else if (maxLoopNesting >= 2) {
            if (maxLoopNesting == 2) {
                estimatedTime = "O(N^2)";
                explanation.append("Nested loops detected (depth of 2).");
            } else {
                estimatedTime = "O(N^" + maxLoopNesting + ")";
                explanation.append("Deeply nested loops detected (depth of ").append(maxLoopNesting).append(").");
            }
            confidence = 0.8;
        } else if (hasLogarithmicLoop) {
            estimatedTime = "O(log N)";
            explanation.append("Logarithmic index adjustments (halving or doubling) found within loop.");
            confidence = 0.7;
        } else if (cleanedCode.contains("for") || cleanedCode.contains("while")) {
            estimatedTime = "O(N)";
            explanation.append("Linear loop structure detected.");
            confidence = 0.85;
        } else {
            estimatedTime = "O(1)";
            explanation.append("No loops or recursive calls identified. Assuming constant execution paths.");
            confidence = 0.9;
        }

        // Refine for N log N (e.g., sorting + single loop)
        if (cleanedCode.contains("Arrays.sort") || cleanedCode.contains("Collections.sort") || 
            cleanedCode.contains(".sort(") || cleanedCode.contains("sort(")) {
            if (estimatedTime.equals("O(N)")) {
                estimatedTime = "O(N log N)";
                explanation.append(" Built-in sort invocation paired with linear parsing.");
                confidence = 0.8;
            }
        }

        // Determine Space Complexity
        if (hasCollectionSpace) {
            estimatedSpace = "O(N)";
            explanation.append(" Heavy memory storage structures (HashMap, HashSet, List, dynamic array/dict) allocated.");
            confidence = Math.min(confidence + 0.05, 0.95);
        } else if (hasGraphOrTree) {
            estimatedSpace = "O(V + E)";
            explanation.append(" Adjacency list or graph structure indicators matching O(V+E) auxiliary space.");
            confidence = 0.75;
        } else if (hasRecursion) {
            estimatedSpace = "O(N)"; // recursion stack
            explanation.append(" Stack memory allocated for deep recursion calls.");
        }

        return EstimationResult.builder()
            .timeComplexity(estimatedTime)
            .spaceComplexity(estimatedSpace)
            .confidenceScore(confidence)
            .details(explanation.toString())
            .build();
    }

    private String cleanComments(String code) {
        // Remove multi-line comments /* ... */
        String noMulti = code.replaceAll("(?s)/\\*.*?\\*/", "");
        // Remove single-line comments // ... or # ...
        String noSingle = noMulti.replaceAll("//.*", "");
        return noSingle.replaceAll("#.*", "");
    }

    private boolean checkRecursion(String code) {
        // Look for method names that call themselves.
        // A generic regex search for custom function signature matches and their recursive usage.
        // Since regex parsing of raw code is heuristic, we look for recursive patterns:
        // matching a name token immediately followed by arguments, and that same token inside the body.
        // For simplicity: look for recursive calls to common recursion names or recursive patterns.
        Pattern functionPattern = Pattern.compile("(?:def|public|private|static|function|const)\\s+([a-zA-Z0-9_]+)\\s*\\(");
        Matcher matcher = functionPattern.matcher(code);
        while (matcher.find()) {
            String funcName = matcher.group(1);
            if (funcName.equalsIgnoreCase("main") || funcName.equalsIgnoreCase("solve")) {
                continue;
            }
            // Check if the function name appears again later in the code
            // (heuristic: calls itself after definition)
            Pattern callPattern = Pattern.compile("\\b" + Pattern.quote(funcName) + "\\s*\\(");
            Matcher callMatcher = callPattern.matcher(code);
            int count = 0;
            while (callMatcher.find()) {
                count++;
            }
            if (count >= 2) { // 1 for definition, 1 or more for recursive call
                return true;
            }
        }
        return false;
    }

    private boolean checkExponentialRecursion(String code) {
        // Look for multiple recursion branches e.g. fib(n-1) + fib(n-2)
        // or helper(..., i+1) + helper(..., i+2)
        Pattern functionPattern = Pattern.compile("(?:def|public|private|static|function|const)\\s+([a-zA-Z0-9_]+)\\s*\\(");
        Matcher matcher = functionPattern.matcher(code);
        while (matcher.find()) {
            String funcName = matcher.group(1);
            if (funcName.equalsIgnoreCase("main")) continue;
            
            Pattern doubleCallPattern = Pattern.compile("\\b" + Pattern.quote(funcName) + "\\s*\\(.*\\).*\\b" + Pattern.quote(funcName) + "\\s*\\(");
            if (doubleCallPattern.matcher(code).find()) {
                return true;
            }
        }
        return false;
    }

    private int checkLoopNesting(String code) {
        // Heuristically count nested loops using brace tracking or indentation
        // Since code can be Java (braces) or Python (indentation), we look for consecutive loop keywords:
        // loop keywords: 'for', 'while'
        // Let's do block level analysis: look for 'for' or 'while' keyword, then look inside its block
        int maxDepth = 0;
        int currentDepth = 0;
        
        // Split code by lines/characters and count opening/closing scopes
        // Simple heuristic: count occurrences of "for" and "while" that are opened before closing.
        // We'll search for 'for' or 'while' structures in sequence.
        // Let's check matching pairs of nested loops
        String[] lines = code.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("for") || trimmed.startsWith("while") || 
                trimmed.contains(" for ") || trimmed.contains(" while ")) {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
            } else if (trimmed.equals("}") || trimmed.equals("end") || trimmed.isEmpty()) {
                if (currentDepth > 0) {
                    currentDepth--;
                }
            }
        }
        
        // Fallback checks
        if (maxDepth == 0) {
            if (code.contains("for") || code.contains("while")) {
                maxDepth = 1;
            }
        }
        
        // Double-check nested patterns (e.g. for inside for)
        Pattern nestedPattern = Pattern.compile("(?:for|while)\\s*\\(.*?\\)\\s*\\{[^}]*(?:for|while)\\s*\\(.*?\\)\\s*\\{");
        if (nestedPattern.matcher(code.replace("\n", " ")).find()) {
            maxDepth = Math.max(maxDepth, 2);
        }
        
        return maxDepth;
    }

    private boolean checkLogarithmicLoop(String code) {
        // Look for division or multiplication on loop variables e.g. i /= 2, i *= 2, i = i / 2, i >> 1, i >>= 1
        Pattern logPattern = Pattern.compile("(\\w+)\\s*(?:/=|\\*=|>>=|<<=)\\s*\\d+|(\\w+)\\s*=\\s*\\1\\s*(?:/|\\*|>>|<<)\\s*\\d+");
        return logPattern.matcher(code).find() || code.contains("binarySearch") || code.contains("binary_search");
    }

    private boolean checkCollectionSpace(String code) {
        // Look for creations of large collections: HashMap, HashSet, ArrayList, lists, dict, set, vector
        String lower = code.toLowerCase();
        return lower.contains("new hashmap") || lower.contains("new hashset") || 
               lower.contains("new arraylist") || lower.contains("new int[") ||
               lower.contains("std::vector") || lower.contains("std::unordered_map") ||
               lower.contains("new list") || (lower.contains("dict()") && lower.contains("for")) ||
               (lower.contains("set()") && lower.contains("for")) || 
               lower.contains("[]") && lower.contains(".append") ||
               lower.contains("map = {}") || lower.contains("map = new map");
    }

    private boolean checkTreeGraphKeywords(String code) {
        String lower = code.toLowerCase();
        return lower.contains("adjlist") || lower.contains("graph") || 
               lower.contains("treenode") || lower.contains("node") && 
               (lower.contains("left") || lower.contains("right"));
    }
}
