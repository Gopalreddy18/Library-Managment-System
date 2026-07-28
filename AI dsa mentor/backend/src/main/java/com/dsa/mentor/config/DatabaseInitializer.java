package com.dsa.mentor.config;

import com.dsa.mentor.entities.Difficulty;
import com.dsa.mentor.entities.Problem;
import com.dsa.mentor.entities.User;
import com.dsa.mentor.entities.UserRole;
import com.dsa.mentor.repositories.ProblemRepository;
import com.dsa.mentor.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeProblems();
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Seed Admin User
            User admin = User.builder()
                .name("Admin System")
                .email("admin@dsamentor.com")
                .password(passwordEncoder.encode("adminpassword123"))
                .role(UserRole.ROLE_ADMIN)
                .college("DSA University")
                .graduationYear(2025)
                .targetCompany("Google")
                .targetRole("Site Reliability Engineer")
                .currentDsaLevel("ADVANCED")
                .build();
            userRepository.save(admin);

            // Seed Student User
            User student = User.builder()
                .name("John Doe")
                .email("student@dsamentor.com")
                .password(passwordEncoder.encode("studentpassword123"))
                .role(UserRole.ROLE_STUDENT)
                .college("Technology Institute")
                .graduationYear(2027)
                .targetCompany("Meta")
                .targetRole("Frontend Software Engineer")
                .currentDsaLevel("BEGINNER")
                .codingStreak(3)
                .accuracy(75.0)
                .totalProblemsSolved(1)
                .topicStrengths("Arrays")
                .topicWeaknesses("Graphs,Dynamic Programming")
                .build();
            userRepository.save(student);
        }
    }

    private void initializeProblems() {
        if (problemRepository.count() == 0) {
            Problem p1 = Problem.builder()
                .title("Two Sum")
                .description("Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.\n\nYou can return the answer in any order.")
                .difficulty(Difficulty.EASY)
                .tags("Arrays,HashMap")
                .constraints("2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9\nOnly one valid answer exists.")
                .examples("""
                    [
                      {
                        "input": "nums = [2,7,11,15], target = 9",
                        "output": "[0,1]",
                        "explanation": "Because nums[0] + nums[1] == 9, we return [0, 1]."
                      }
                    ]
                    """)
                .testCases("""
                    [
                      {
                        "input": "[2,7,11,15]\\n9",
                        "output": "[0,1]"
                      }
                    ]
                    """)
                .hiddenTestCases("""
                    [
                      {
                        "input": "[3,2,4]\\n6",
                        "output": "[1,2]"
                      },
                      {
                        "input": "[3,3]\\n6",
                        "output": "[0,1]"
                      }
                    ]
                    """)
                .editorial("The optimal solution is to use a HashMap. Iterating through the array, we check if the complement (target - nums[i]) exists in the map. If it does, we return the current index and the index of the complement. Otherwise, we add the current number to the map and continue.")
                .expectedComplexity("O(N) time, O(N) space")
                .build();

            Problem p2 = Problem.builder()
                .title("Reverse Linked List")
                .description("Given the head of a singly linked list, reverse the list, and return the reversed list.")
                .difficulty(Difficulty.EASY)
                .tags("Linked Lists")
                .constraints("The number of nodes in the list is the range [0, 5000].\n-5000 <= Node.val <= 5000")
                .examples("""
                    [
                      {
                        "input": "head = [1,2,3,4,5]",
                        "output": "[5,4,3,2,1]"
                      }
                    ]
                    """)
                .testCases("""
                    [
                      {
                        "input": "[1,2,3,4,5]",
                        "output": "[5,4,3,2,1]"
                      }
                    ]
                    """)
                .hiddenTestCases("""
                    [
                      {
                        "input": "[]",
                        "output": "[]"
                      },
                      {
                        "input": "[1]",
                        "output": "[1]"
                      }
                    ]
                    """)
                .editorial("This problem can be solved either iteratively or recursively. The iterative solution uses three pointers: prev, curr, and next. In each step, we store the next node, redirect the curr.next pointer to prev, shift prev to curr, and shift curr to next.")
                .expectedComplexity("O(N) time, O(1) space")
                .build();

            Problem p3 = Problem.builder()
                .title("Valid Parentheses")
                .description("Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.\n\nAn input string is valid if:\n1. Open brackets must be closed by the same type of brackets.\n2. Open brackets must be closed in the correct order.\n3. Every close bracket has a corresponding open bracket of the same type.")
                .difficulty(Difficulty.EASY)
                .tags("Stacks")
                .constraints("1 <= s.length <= 10^4\ns consists of parentheses only '()[]{}'.")
                .examples("""
                    [
                      {
                        "input": "s = \\"()\\"",
                        "output": "true"
                      },
                      {
                        "input": "s = \\"()[]{}\\"",
                        "output": "true"
                      },
                      {
                        "input": "s = \\"(]\\"",
                        "output": "false"
                      }
                    ]
                    """)
                .testCases("""
                    [
                      {
                        "input": "\\\"()\\\"",
                        "output": "true"
                      }
                    ]
                    """)
                .hiddenTestCases("""
                    [
                      {
                        "input": "\\\"()[]{}\\\"",
                        "output": "true"
                      },
                      {
                        "input": "\\\"(]\\\"",
                        "output": "false"
                      }
                    ]
                    """)
                .editorial("Use a Stack. Push any opening bracket onto the stack. For a closing bracket, check if the stack is not empty and the top of the stack matches the closing bracket type. If yes, pop. If mismatch or empty stack, return false. After parsing the string, return true if the stack is empty.")
                .expectedComplexity("O(N) time, O(N) space")
                .build();

            Problem p4 = Problem.builder()
                .title("Number of Islands")
                .description("Given an m x n 2D binary grid grid which represents a map of '1's (land) and '0's (water), return the number of islands.\n\nAn island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.")
                .difficulty(Difficulty.MEDIUM)
                .tags("Graphs,BFS,DFS")
                .constraints("m == grid.length\nn == grid[i].length\n1 <= m, n <= 300\ngrid[i][j] is '0' or '1'.")
                .examples("""
                    [
                      {
                        "input": "grid = [\\n  [\\"1\\",\\"1\\",\\"1\\",\\"1\\",\\"0\\"],\\n  [\\"1\\",\\"1\\",\\"0\\",\\"1\\",\\"0\\"],\\n  [\\"1\\",\\"1\\",\\"0\\",\\"0\\",\\"0\\"],\\n  [\\"0\\",\\"0\\",\\"0\\",\\"0\\",\\"0\\"]\\n]",
                        "output": "1"
                      }
                    ]
                    """)
                .testCases("""
                    [
                      {
                        "input": "[[\\"1\\",\\"1\\",\\"1\\",\\"1\\",\\"0\\"],[\\"1\\",\\"1\\",\\"0\\",\\"1\\",\\"0\\"],[\\"1\\",\\"1\\",\\"0\\",\\"0\\",\\"0\\"],[\\"0\\",\\"0\\",\\"0\\",\\"0\\",\\"0\\"]]",
                        "output": "1"
                      }
                    ]
                    """)
                .hiddenTestCases("""
                    [
                      {
                        "input": "[[\\"1\\",\\"1\\",\\"0\\",\\"0\\",\\"0\\"],[\\"1\\",\\"1\\",\\"0\\",\\"0\\",\\"0\\"],[\\"0\\",\\"0\\",\\"1\\",\\"0\\",\\"0\\"],[\\"0\\",\\"0\\",\\"0\\",\\"1\\",\\"1\\"]]",
                        "output": "3"
                      }
                    ]
                    """)
                .editorial("DFS/BFS search: Scan the grid. For each '1' encountered, increment island count and perform DFS or BFS to mark all connected lands as visited (or convert '1' to '0'). This stops counting nodes of the same island repeatedly.")
                .expectedComplexity("O(M * N) time, O(M * N) space")
                .build();

            problemRepository.saveAll(List.of(p1, p2, p3, p4));
        }
    }
}
