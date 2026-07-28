import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  Box, Grid, Typography, Card, CardContent, Button, MenuItem, TextField, 
  CircularProgress, Alert, Tabs, Tab, Paper, Chip, Divider, Box as MuiBox
} from '@mui/material';
import { Play, Send, ChevronLeft, BookOpen, Terminal, Sparkles, Clock, HardDrive, ShieldCheck } from 'lucide-react';

interface Problem {
  id: number;
  title: string;
  description: string;
  difficulty: string;
  tags: string;
  constraints: string;
  examples: string; // JSON
  editorial: string;
  expectedComplexity: string;
}

interface Analysis {
  id: number;
  logicCorrectnessFeedback: String;
  timeComplexity: string;
  spaceComplexity: string;
  optimizationSuggestions: string;
  bestPracticesFeedback: string;
}

interface Submission {
  id: number;
  runtimeMs: number;
  memoryKb: number;
  result: string;
}

const ProblemDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { api } = useAuth();

  const [problem, setProblem] = useState<Problem | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Editor State
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState('java');

  // Execution States
  const [submitting, setSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState<Submission | null>(null);
  const [aiAnalysis, setAiAnalysis] = useState<Analysis | null>(null);
  const [consoleLog, setConsoleLog] = useState<string>('');

  // Tab navigation states
  const [leftTab, setLeftTab] = useState(0);
  const [bottomTab, setBottomTab] = useState(0);

  const defaultTemplates: { [key: string]: string } = {
    java: `class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        // Write your logic here\n        return new int[]{};\n    }\n}`,
    python: `def twoSum(nums, target):\n    # Write your logic here\n    pass`,
    cpp: `class Solution {\npublic:\n    vector<int> twoSum(vector<int>& nums, int target) {\n        // Write your logic here\n        return {};\n    }\n};`,
    javascript: `function twoSum(nums, target) {\n    // Write your logic here\n    return [];\n}`
  };

  useEffect(() => {
    const fetchProblem = async () => {
      try {
        const res = await api.get(`/api/problems/${id}`);
        setProblem(res.data);
        
        // Custom templates fallback based on problem title keyword
        const title = res.data.title.toLowerCase();
        if (title.includes("reverse")) {
          defaultTemplates.java = `class Solution {\n    public ListNode reverseList(ListNode head) {\n        // Write your logic here\n        return null;\n    }\n}`;
          defaultTemplates.python = `def reverseList(head):\n    # Write your logic here\n    pass`;
        } else if (title.includes("parentheses")) {
          defaultTemplates.java = `class Solution {\n    public boolean isValid(String s) {\n        // Write your logic here\n        return false;\n    }\n}`;
          defaultTemplates.python = `def isValid(s):\n    # Write your logic here\n    pass`;
        } else if (title.includes("islands")) {
          defaultTemplates.java = `class Solution {\n    public int numIslands(char[][] grid) {\n        // Write your logic here\n        return 0;\n    }\n}`;
          defaultTemplates.python = `def numIslands(grid):\n    # Write your logic here\n    pass`;
        }

        setCode(defaultTemplates[language]);
      } catch (err: any) {
        setError('Problem could not be loaded.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchProblem();
  }, [id]);

  const handleLanguageChange = (lang: string) => {
    setLanguage(lang);
    setCode(defaultTemplates[lang]);
  };

  const handleRunCode = () => {
    setBottomTab(0);
    setConsoleLog("Running compilation check...\nRunning test cases...\nResult: Simulated run successful! Click Submit to execute against hidden tests.");
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    setSubmissionResult(null);
    setAiAnalysis(null);
    setBottomTab(0);
    setConsoleLog("Evaluating solution on production server...\n");

    try {
      const submitRes = await api.post('/api/submissions', {
        problemId: problem?.id,
        sourceCode: code,
        language: language
      });
      
      const submission = submitRes.data;
      setSubmissionResult(submission);

      setConsoleLog(prev => prev + 
        `Submission Finished.\n` + 
        `Verdict: ${submission.result}\n` + 
        `Runtime: ${submission.runtimeMs} ms\n` + 
        `Memory: ${Math.round(submission.memoryKb / 1024 * 100) / 100} MB\n`
      );

      // Fetch AI Analysis
      setConsoleLog(prev => prev + "Triggering AI Analysis engine...\n");
      const analysisRes = await api.get(`/api/submissions/${submission.id}/analysis`);
      setAiAnalysis(analysisRes.data);
      setBottomTab(1); // auto switch to AI Review tab on complete

    } catch (err: any) {
      setConsoleLog(prev => prev + "Error evaluating code: " + (err.response?.data?.message || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' }}>
        <CircularProgress color="primary" />
      </Box>
    );
  }

  if (error || !problem) {
    return (
      <Alert severity="error" sx={{ my: 4 }}>
        {error || 'No problem workspace found.'}
      </Alert>
    );
  }

  // Parse examples if present
  let examplesParsed = [];
  try {
    examplesParsed = JSON.parse(problem.examples || '[]');
  } catch (e) {
    examplesParsed = [];
  }

  return (
    <Box sx={{ mt: 1 }}>
      {/* Workspace Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Button 
          variant="text" 
          onClick={() => navigate('/problems')}
          startIcon={<ChevronLeft size={16} />}
          sx={{ color: '#94a3b8' }}
        >
          Back to list
        </Button>
        <Typography variant="h5" sx={{ fontWeight: 800 }}>
          {problem.title}
        </Typography>
        <Chip 
          label={problem.difficulty} 
          color={problem.difficulty === 'EASY' ? 'success' : (problem.difficulty === 'MEDIUM' ? 'warning' : 'error')} 
          size="small"
          sx={{ fontWeight: 'bold', fontSize: '11px' }}
        />
      </Box>

      <Grid container spacing={3}>
        {/* Left Side: Description & Editorial */}
        <Grid item xs={12} md={6}>
          <Card className="glass-panel" sx={{ height: '70vh', display: 'flex', flexDirection: 'column' }}>
            <Tabs 
              value={leftTab} 
              onChange={(_, val) => setLeftTab(val)}
              textColor="primary"
              indicatorColor="primary"
              sx={{ borderBottom: '1px solid rgba(255,255,255,0.06)', px: 2 }}
            >
              <Tab icon={<BookOpen size={16} />} label="Description" sx={{ minHeight: '48px', gap: 1, textTransform: 'none' }} />
              <Tab icon={<Sparkles size={16} />} label="Editorial" sx={{ minHeight: '48px', gap: 1, textTransform: 'none' }} />
            </Tabs>

            <Box sx={{ flex: 1, overflowY: 'auto', p: 3 }}>
              {leftTab === 0 ? (
                <Box>
                  <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.7, mb: 4, color: '#cbd5e1' }}>
                    {problem.description}
                  </Typography>

                  <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1, color: 'white' }}>
                    Examples:
                  </Typography>
                  {examplesParsed.map((ex: any, idx: number) => (
                    <Box key={idx} sx={{ bgcolor: 'rgba(255,255,255,0.02)', p: 2, borderRadius: 2, border: '1px solid rgba(255,255,255,0.04)', mb: 2 }}>
                      <Typography variant="body2" sx={{ fontWeight: 'bold', color: '#a78bfa' }}>Example {idx + 1}:</Typography>
                      <Typography variant="body2" sx={{ fontFamily: 'monospace', my: 0.5 }}>Input: {ex.input}</Typography>
                      <Typography variant="body2" sx={{ fontFamily: 'monospace', my: 0.5 }}>Output: {ex.output}</Typography>
                      {ex.explanation && <Typography variant="caption" sx={{ color: '#94a3b8', display: 'block', mt: 1 }}>Explanation: {ex.explanation}</Typography>}
                    </Box>
                  ))}

                  <Typography variant="subtitle1" sx={{ fontWeight: 700, mt: 3, mb: 1, color: 'white' }}>
                    Constraints:
                  </Typography>
                  <Typography variant="body2" sx={{ fontFamily: 'monospace', whiteSpace: 'pre-wrap', color: '#f43f5e', mb: 3 }}>
                    {problem.constraints}
                  </Typography>
                </Box>
              ) : (
                <Box>
                  <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.7, color: '#cbd5e1' }}>
                    {problem.editorial || 'No official editorial available yet.'}
                  </Typography>
                </Box>
              )}
            </Box>
          </Card>
        </Grid>

        {/* Right Side: Code Editor */}
        <Grid item xs={12} md={6}>
          <Card className="glass-panel" sx={{ height: '70vh', display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ p: 2, borderBottom: '1px solid rgba(255,255,255,0.06)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="body2" sx={{ color: '#94a3b8', fontWeight: 600 }}>Code Workspace</Typography>
              <TextField
                select
                size="small"
                value={language}
                onChange={(e) => handleLanguageChange(e.target.value)}
                sx={{ width: '130px' }}
              >
                <MenuItem value="java">Java</MenuItem>
                <MenuItem value="python">Python</MenuItem>
                <MenuItem value="cpp">C++</MenuItem>
                <MenuItem value="javascript">JavaScript</MenuItem>
              </TextField>
            </Box>

            <textarea
              className="code-editor-area"
              style={{ flex: 1, border: 'none', borderRadius: 0 }}
              value={code}
              onChange={(e) => setCode(e.target.value)}
            />

            <Box sx={{ p: 2, borderTop: '1px solid rgba(255,255,255,0.06)', display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
              <Button 
                variant="outlined" 
                startIcon={<Play size={16} />}
                onClick={handleRunCode}
                disabled={submitting}
              >
                Run Code
              </Button>
              <Button 
                variant="contained" 
                startIcon={submitting ? <CircularProgress size={16} color="inherit" /> : <Send size={16} />}
                onClick={handleSubmit}
                disabled={submitting}
                sx={{ background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)' }}
              >
                Submit Solution
              </Button>
            </Box>
          </Card>
        </Grid>

        {/* Bottom Console Panel (Results & AI Review) */}
        <Grid item xs={12}>
          <Card className="glass-panel" sx={{ minHeight: '300px' }}>
            <Tabs 
              value={bottomTab} 
              onChange={(_, val) => setBottomTab(val)}
              textColor="primary"
              indicatorColor="primary"
              sx={{ borderBottom: '1px solid rgba(255,255,255,0.06)', px: 2 }}
            >
              <Tab icon={<Terminal size={16} />} label="Console Output" sx={{ minHeight: '48px', gap: 1, textTransform: 'none' }} />
              <Tab icon={<Sparkles size={16} />} label="AI Code Review" sx={{ minHeight: '48px', gap: 1, textTransform: 'none' }} />
            </Tabs>

            <Box sx={{ p: 3 }}>
              {bottomTab === 0 ? (
                <Box>
                  <Typography variant="body2" sx={{ fontFamily: 'monospace', whiteSpace: 'pre-wrap', color: '#10b981', bgcolor: '#080511', p: 3, borderRadius: 2 }}>
                    {consoleLog || "Workspace Console. Run or Submit code to see execution outputs."}
                  </Typography>
                </Box>
              ) : (
                <Box>
                  {aiAnalysis ? (
                    <Grid container spacing={3}>
                      {/* Complexity Estimates */}
                      <Grid item xs={12} sm={4}>
                        <Card sx={{ bgcolor: 'rgba(139, 92, 246, 0.05)', border: '1px solid rgba(139, 92, 246, 0.15)', borderRadius: 3 }}>
                          <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: '#a78bfa' }}>
                              <Clock size={18} />
                              <Typography variant="body2" sx={{ fontWeight: 600 }}>Estimated Time Complexity</Typography>
                            </Box>
                            <Typography variant="h4" sx={{ fontWeight: 800 }}>{aiAnalysis.timeComplexity}</Typography>
                          </CardContent>
                        </Card>
                      </Grid>

                      <Grid item xs={12} sm={4}>
                        <Card sx={{ bgcolor: 'rgba(6, 182, 212, 0.05)', border: '1px solid rgba(6, 182, 212, 0.15)', borderRadius: 3 }}>
                          <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: '#22d3ee' }}>
                              <HardDrive size={18} />
                              <Typography variant="body2" sx={{ fontWeight: 600 }}>Estimated Space Complexity</Typography>
                            </Box>
                            <Typography variant="h4" sx={{ fontWeight: 800 }}>{aiAnalysis.spaceComplexity}</Typography>
                          </CardContent>
                        </Card>
                      </Grid>

                      <Grid item xs={12} sm={4}>
                        <Card sx={{ bgcolor: 'rgba(16, 185, 129, 0.05)', border: '1px solid rgba(16, 185, 129, 0.15)', borderRadius: 3 }}>
                          <CardContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: '#34d399' }}>
                              <ShieldCheck size={18} />
                              <Typography variant="body2" sx={{ fontWeight: 600 }}>Goal Complexity</Typography>
                            </Box>
                            <Typography variant="h4" sx={{ fontWeight: 800 }}>{problem.expectedComplexity}</Typography>
                          </CardContent>
                        </Card>
                      </Grid>

                      {/* General Code Quality Review */}
                      <Grid item xs={12}>
                        <Divider sx={{ my: 1, borderColor: 'rgba(255,255,255,0.06)' }} />
                      </Grid>

                      <Grid item xs={12} md={6}>
                        <Typography variant="h6" sx={{ fontWeight: 700, color: 'white', mb: 1 }}>Correctness Feedback</Typography>
                        <Typography variant="body2" sx={{ color: '#cbd5e1', lineHeight: 1.6 }}>
                          {aiAnalysis.logicCorrectnessFeedback}
                        </Typography>
                      </Grid>

                      <Grid item xs={12} md={6}>
                        <Typography variant="h6" sx={{ fontWeight: 700, color: 'white', mb: 1 }}>Optimization Suggestions</Typography>
                        <Typography variant="body2" sx={{ color: '#cbd5e1', lineHeight: 1.6 }}>
                          {aiAnalysis.optimizationSuggestions}
                        </Typography>
                      </Grid>

                      <Grid item xs={12}>
                        <Typography variant="h6" sx={{ fontWeight: 700, color: 'white', mb: 1 }}>Coding Best Practices</Typography>
                        <Typography variant="body2" sx={{ color: '#cbd5e1', lineHeight: 1.6 }}>
                          {aiAnalysis.bestPracticesFeedback}
                        </Typography>
                      </Grid>

                    </Grid>
                  ) : (
                    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', py: 4 }}>
                      {submitting ? (
                        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
                          <CircularProgress size={36} color="primary" />
                          <Typography variant="body2" sx={{ color: '#94a3b8' }}>AI is analyzing your submission logic...</Typography>
                        </Box>
                      ) : (
                        <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                          Submit your code to trigger the AI review panel.
                        </Typography>
                      )}
                    </Box>
                  )}
                </Box>
              )}
            </Box>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default ProblemDetail;
