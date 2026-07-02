package com.aidsamentor.complexity.service.impl;

import com.aidsamentor.complexity.dto.ComplexityEstimateResponse;
import com.aidsamentor.complexity.service.ComplexityEstimatorService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class ComplexityEstimatorServiceImpl implements ComplexityEstimatorService {

    private static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern LINE_COMMENT = Pattern.compile("//.*");
    private static final Pattern METHOD_PATTERN = Pattern.compile(
            "(?:public|private|protected|static|final|synchronized|native|abstract|\\s)*" +
                    "[\\w<>\\[\\], ?]+\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{"
    );
    private static final Pattern LOOP_PATTERN = Pattern.compile("\\b(for|while)\\s*\\(");
    private static final Pattern COLLECTION_ALLOCATION_PATTERN = Pattern.compile(
            "\\b(new\\s+(?:ArrayList|LinkedList|HashMap|HashSet|TreeMap|TreeSet|Queue|Stack|PriorityQueue)|" +
                    "(?:List|Set|Map|Queue|Stack|Deque)<|\\w+\\s*=\\s*new\\s+\\w+\\s*\\[)"
    );

    @Override
    public ComplexityEstimateResponse estimate(String sourceCode) {
        String code = stripComments(sourceCode == null ? "" : sourceCode);
        String lowerCode = code.toLowerCase();

        List<String> signals = new ArrayList<>();
        boolean hashMapUsage = containsHashMapUsage(code);
        boolean graphTraversal = containsGraphTraversal(lowerCode);
        boolean dfsOrBfs = containsDfsOrBfs(lowerCode);
        boolean sorting = containsSorting(lowerCode);
        boolean logLoop = containsLogarithmicLoop(lowerCode);
        boolean nestedLoop = containsNestedLoop(code);
        int loopCount = countLoops(code);
        RecursionAnalysis recursion = analyzeRecursion(code);

        if (hashMapUsage) {
            signals.add("HashMap/Map usage detected");
        }
        if (graphTraversal) {
            signals.add("graph traversal structure detected");
        }
        if (dfsOrBfs) {
            signals.add("DFS/BFS traversal detected");
        }
        if (recursion.recursive()) {
            signals.add("recursive method call detected");
        }
        if (recursion.exponential()) {
            signals.add("multiple recursive branches detected");
        }

        String timeComplexity = estimateTimeComplexity(
                loopCount,
                nestedLoop,
                logLoop,
                sorting,
                graphTraversal,
                dfsOrBfs,
                recursion
        );
        String spaceComplexity = estimateSpaceComplexity(code, hashMapUsage, graphTraversal, dfsOrBfs, recursion);
        double confidence = estimateConfidence(loopCount, nestedLoop, logLoop, sorting, graphTraversal, dfsOrBfs, recursion);
        String explanation = buildExplanation(timeComplexity, spaceComplexity, loopCount, nestedLoop, logLoop, sorting, signals);

        return new ComplexityEstimateResponse(timeComplexity, spaceComplexity, confidence, explanation);
    }

    private String estimateTimeComplexity(
            int loopCount,
            boolean nestedLoop,
            boolean logLoop,
            boolean sorting,
            boolean graphTraversal,
            boolean dfsOrBfs,
            RecursionAnalysis recursion
    ) {
        if (recursion.exponential()) {
            return "O(2^N)";
        }
        if (graphTraversal || dfsOrBfs) {
            return "O(V + E)";
        }
        if (nestedLoop) {
            return "O(N^2)";
        }
        if (sorting) {
            return "O(N log N)";
        }
        if (recursion.recursive() && recursion.reducesByHalf()) {
            return "O(log N)";
        }
        if (logLoop) {
            return "O(log N)";
        }
        if (recursion.recursive() || loopCount > 0) {
            return "O(N)";
        }
        return "O(1)";
    }

    private String estimateSpaceComplexity(
            String code,
            boolean hashMapUsage,
            boolean graphTraversal,
            boolean dfsOrBfs,
            RecursionAnalysis recursion
    ) {
        if (hashMapUsage || graphTraversal || dfsOrBfs || hasCollectionAllocation(code)) {
            return "O(N)";
        }
        if (recursion.exponential()) {
            return "O(N)";
        }
        if (recursion.recursive() && recursion.reducesByHalf()) {
            return "O(log N)";
        }
        if (recursion.recursive()) {
            return "O(N)";
        }
        return "O(1)";
    }

    private double estimateConfidence(
            int loopCount,
            boolean nestedLoop,
            boolean logLoop,
            boolean sorting,
            boolean graphTraversal,
            boolean dfsOrBfs,
            RecursionAnalysis recursion
    ) {
        double confidence = 0.62;
        if (loopCount > 0 || recursion.recursive()) {
            confidence += 0.12;
        }
        if (nestedLoop || logLoop || sorting) {
            confidence += 0.08;
        }
        if (graphTraversal || dfsOrBfs) {
            confidence += 0.1;
        }
        if (recursion.exponential()) {
            confidence += 0.08;
        }
        return Math.min(0.95, Math.round(confidence * 100.0) / 100.0);
    }

    private String buildExplanation(
            String timeComplexity,
            String spaceComplexity,
            int loopCount,
            boolean nestedLoop,
            boolean logLoop,
            boolean sorting,
            List<String> signals
    ) {
        List<String> details = new ArrayList<>();
        if (loopCount > 0) {
            details.add(loopCount + " loop(s) detected");
        }
        if (nestedLoop) {
            details.add("nested loops indicate quadratic growth");
        }
        if (logLoop) {
            details.add("halving/doubling loop pattern indicates logarithmic growth");
        }
        if (sorting) {
            details.add("sorting call indicates N log N growth");
        }
        details.addAll(signals);
        if (details.isEmpty()) {
            details.add("no loops, recursion, or growing data structures detected");
        }
        return "Estimated time " + timeComplexity + " and space " + spaceComplexity + " because " +
                String.join("; ", details) + ".";
    }

    private String stripComments(String sourceCode) {
        String withoutBlockComments = BLOCK_COMMENT.matcher(sourceCode).replaceAll("");
        return LINE_COMMENT.matcher(withoutBlockComments).replaceAll("");
    }

    private int countLoops(String code) {
        Matcher matcher = LOOP_PATTERN.matcher(code);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private boolean containsNestedLoop(String code) {
        Matcher matcher = LOOP_PATTERN.matcher(code);
        while (matcher.find()) {
            String loopBody = extractBlockStartingAt(code, code.indexOf('{', matcher.end()));
            if (!loopBody.isBlank() && LOOP_PATTERN.matcher(loopBody).find()) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLogarithmicLoop(String lowerCode) {
        return lowerCode.matches("(?s).*(\\*=\\s*2|/=\\s*2|=\\s*\\w+\\s*/\\s*2|=\\s*\\w+\\s*>>\\s*1).*")
                || lowerCode.contains("mid =")
                || lowerCode.contains("mid=")
                || lowerCode.contains("binarysearch")
                || lowerCode.contains("binary_search");
    }

    private boolean containsSorting(String lowerCode) {
        return lowerCode.contains("arrays.sort")
                || lowerCode.contains("collections.sort")
                || lowerCode.contains(".sorted(")
                || lowerCode.contains(".sort(");
    }

    private boolean containsHashMapUsage(String code) {
        return code.matches("(?s).*(HashMap|LinkedHashMap|ConcurrentHashMap|Map<|\\.put\\(|\\.containsKey\\(|\\.getOrDefault\\().*");
    }

    private boolean containsGraphTraversal(String lowerCode) {
        return lowerCode.contains("adjacency")
                || lowerCode.contains("adjlist")
                || lowerCode.contains("adj[")
                || lowerCode.contains("graph")
                || lowerCode.contains("visited");
    }

    private boolean containsDfsOrBfs(String lowerCode) {
        return lowerCode.matches("(?s).*(\\bdfs\\s*\\(|\\bbfs\\s*\\(|\\bqueue\\b|\\bstack\\b).*")
                && lowerCode.contains("visited");
    }

    private boolean hasCollectionAllocation(String code) {
        return COLLECTION_ALLOCATION_PATTERN.matcher(code).find();
    }

    private RecursionAnalysis analyzeRecursion(String code) {
        Matcher matcher = METHOD_PATTERN.matcher(code);
        while (matcher.find()) {
            String methodName = matcher.group(1);
            if (isControlKeyword(methodName)) {
                continue;
            }
            String methodBody = extractBlockStartingAt(code, matcher.end() - 1);
            int selfCalls = countMethodCalls(methodBody, methodName);
            if (selfCalls > 0) {
                String lowerBody = methodBody.toLowerCase();
                boolean reducesByHalf = lowerBody.matches("(?s).*(/\\s*2|>>\\s*1|half).*");
                boolean exponential = selfCalls > 1 || lowerBody.matches("(?s).*" + methodName.toLowerCase() + "\\s*\\([^)]*-\\s*1[^)]*\\).*" +
                        methodName.toLowerCase() + "\\s*\\([^)]*-\\s*2[^)]*\\).*");
                return new RecursionAnalysis(true, exponential, reducesByHalf);
            }
        }
        return new RecursionAnalysis(false, false, false);
    }

    private int countMethodCalls(String methodBody, String methodName) {
        Matcher callMatcher = Pattern.compile("\\b" + Pattern.quote(methodName) + "\\s*\\(").matcher(methodBody);
        int count = 0;
        while (callMatcher.find()) {
            count++;
        }
        return count;
    }

    private boolean isControlKeyword(String methodName) {
        return "if".equals(methodName) || "for".equals(methodName) || "while".equals(methodName) || "switch".equals(methodName);
    }

    private String extractBlockStartingAt(String code, int openingBraceIndex) {
        if (openingBraceIndex < 0 || openingBraceIndex >= code.length() || code.charAt(openingBraceIndex) != '{') {
            return "";
        }
        int depth = 0;
        for (int i = openingBraceIndex; i < code.length(); i++) {
            char current = code.charAt(i);
            if (current == '{') {
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth == 0) {
                    return code.substring(openingBraceIndex + 1, i);
                }
            }
        }
        return code.substring(openingBraceIndex + 1);
    }

    private record RecursionAnalysis(boolean recursive, boolean exponential, boolean reducesByHalf) {
    }
}
