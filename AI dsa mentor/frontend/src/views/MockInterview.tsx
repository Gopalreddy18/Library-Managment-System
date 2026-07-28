import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  Box, Typography, Card, CardContent, Button, MenuItem, TextField, 
  CircularProgress, Alert, Grid, Divider, List, ListItem, Avatar, LinearProgress
} from '@mui/material';
import { Award, Send, MessageSquare, ChevronRight, HelpCircle, Code, User, ArrowLeft } from 'lucide-react';

interface MockInterviewSession {
  id: number;
  mode: string;
  currentQuestion: string;
  interviewStatus: string;
  conversationHistory: string; // JSON list
  scorecard: string; // JSON obj
}

const MockInterview: React.FC = () => {
  const { api, user } = useAuth();

  const [activeSession, setActiveSession] = useState<MockInterviewSession | null>(null);
  const [sessionsHistory, setSessionsHistory] = useState<MockInterviewSession[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Start Form State
  const [mode, setMode] = useState('DSA');
  const [starting, setStarting] = useState(false);

  // Chat/Response State
  const [userMsg, setUserMsg] = useState('');
  const [submittingMsg, setSubmittingMsg] = useState(false);
  const [finishing, setFinishing] = useState(false);

  const chatEndRef = useRef<HTMLDivElement>(null);

  const fetchSessionData = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get('/api/interview');
      setSessionsHistory(res.data);
      
      // Check if there is an in-progress session
      const active = res.data.find((s: any) => s.interviewStatus === 'IN_PROGRESS');
      if (active) {
        setActiveSession(active);
      }
    } catch (err: any) {
      setError('Could not retrieve interview history.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSessionData();
  }, []);

  useEffect(() => {
    if (chatEndRef.current) {
      chatEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [activeSession]);

  const handleStart = async () => {
    setStarting(true);
    setError(null);
    try {
      const res = await api.post('/api/interview/start', { mode });
      setActiveSession(res.data);
      // Add to local history list
      setSessionsHistory(prev => [res.data, ...prev]);
    } catch (err: any) {
      setError('Failed to start interview session.');
    } finally {
      setStarting(false);
    }
  };

  const handleSendResponse = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!userMsg.trim() || !activeSession) return;

    setSubmittingMsg(true);
    const textToSend = userMsg;
    setUserMsg('');

    try {
      const res = await api.post(`/api/interview/${activeSession.id}/respond`, {
        response: textToSend
      });
      setActiveSession(res.data);
    } catch (err: any) {
      setError('Failed to send message to interviewer.');
    } finally {
      setSubmittingMsg(false);
    }
  };

  const handleFinish = async () => {
    if (!activeSession) return;
    setFinishing(true);
    try {
      const res = await api.post(`/api/interview/${activeSession.id}/submit`);
      setActiveSession(res.data);
      // Reload overall history
      const histRes = await api.get('/api/interview');
      setSessionsHistory(histRes.data);
    } catch (err: any) {
      setError('Failed to evaluate mock session.');
    } finally {
      setFinishing(false);
    }
  };

  const handleBackToSelect = () => {
    setActiveSession(null);
    fetchSessionData();
  };

  // Chat message parser
  const getChatMessages = (session: MockInterviewSession) => {
    try {
      return JSON.parse(session.conversationHistory || '[]');
    } catch (e) {
      return [];
    }
  };

  // Scorecard Parser
  const getScorecardData = (session: MockInterviewSession) => {
    try {
      return JSON.parse(session.scorecard || '{}');
    } catch (e) {
      return null;
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' }}>
        <CircularProgress color="primary" />
      </Box>
    );
  }

  return (
    <Box className="animate-fade-in" sx={{ mt: 2 }}>
      {/* Title */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 800 }}>
          AI Mock Interview Simulator
        </Typography>
        <Typography variant="body1" sx={{ color: '#94a3b8' }}>
          Simulate coding, systems architecture, core programming paradigms, or HR behavioral interview rounds.
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {!activeSession ? (
        <Grid container spacing={4}>
          {/* Start panel */}
          <Grid item xs={12} md={5}>
            <Card className="glass-panel" sx={{ p: 3 }}>
              <CardContent sx={{ p: 0 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 3 }}>
                  <Award size={24} style={{ color: '#8b5cf6' }} />
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>Initiate Mock Interview</Typography>
                </Box>
                
                <TextField
                  fullWidth
                  select
                  label="Select Interview Mode"
                  value={mode}
                  onChange={(e) => setMode(e.target.value)}
                  sx={{ mb: 3 }}
                >
                  <MenuItem value="DSA">Data Structures & Algorithms</MenuItem>
                  <MenuItem value="JAVA">Core Java & OOP Design</MenuItem>
                  <MenuItem value="SYSTEM_DESIGN">System Design & Architecture</MenuItem>
                  <MenuItem value="HR">HR & Behavioral round</MenuItem>
                </TextField>

                <Button
                  fullWidth
                  variant="contained"
                  disabled={starting}
                  onClick={handleStart}
                  sx={{ background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)', py: 1.2 }}
                >
                  {starting ? <CircularProgress size={24} color="inherit" /> : 'Start Interview Session'}
                </Button>
              </CardContent>
            </Card>
          </Grid>

          {/* History Panel */}
          <Grid item xs={12} md={7}>
            <Card className="glass-panel" sx={{ p: 3 }}>
              <CardContent sx={{ p: 0 }}>
                <Typography variant="h5" gutterBottom sx={{ fontWeight: 700, mb: 3 }}>
                  Past Interview Scorecards
                </Typography>
                {sessionsHistory.length === 0 ? (
                  <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                    You have not completed any interview simulations yet. Initiate a session above to evaluate your verbal or logical capabilities.
                  </Typography>
                ) : (
                  <List>
                    {sessionsHistory.map((session) => {
                      const card = getScorecardData(session);
                      return (
                        <ListItem 
                          key={session.id} 
                          sx={{ 
                            px: 2, 
                            py: 1.5, 
                            mb: 2, 
                            bgcolor: 'rgba(255,255,255,0.02)', 
                            borderRadius: 3, 
                            border: '1px solid rgba(255,255,255,0.04)',
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            cursor: 'pointer'
                          }}
                          onClick={() => setActiveSession(session)}
                        >
                          <Box>
                            <Typography variant="body1" sx={{ fontWeight: 600 }}>
                              {session.mode} Interview
                            </Typography>
                            <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                              Status: <span style={{ color: session.interviewStatus === 'COMPLETED' ? '#10b981' : '#f59e0b', fontWeight: 600 }}>{session.interviewStatus}</span> • {new Date(session.createdAt || '').toLocaleDateString()}
                            </Typography>
                          </Box>
                          
                          {session.interviewStatus === 'COMPLETED' && card && (
                            <Chip 
                              label={`Score: ${card.overallScore || 0}/10`} 
                              color="primary"
                              variant="outlined"
                              sx={{ fontWeight: 'bold' }}
                            />
                          )}
                          
                          {session.interviewStatus === 'IN_PROGRESS' && (
                            <Button size="small" variant="outlined" color="warning">Resume</Button>
                          )}
                        </ListItem>
                      );
                    })}
                  </List>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      ) : (
        // Active Session Chat & Final scorecard view
        <Box>
          <Button 
            variant="text" 
            startIcon={<ArrowLeft size={16} />}
            onClick={handleBackToSelect}
            sx={{ mb: 3, color: '#94a3b8' }}
          >
            Exit Workspace
          </Button>

          {activeSession.interviewStatus === 'IN_PROGRESS' ? (
            <Grid container spacing={3}>
              {/* Chat View */}
              <Grid item xs={12} md={8}>
                <Card className="glass-panel" sx={{ height: '60vh', display: 'flex', flexDirection: 'column' }}>
                  <Box sx={{ p: 2, borderBottom: '1px solid rgba(255,255,255,0.06)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="body1" sx={{ fontWeight: 700 }}>Active Recruiter Session: {activeSession.mode}</Typography>
                    <Chip label="Live Chat" color="error" size="small" sx={{ fontWeight: 'bold' }} />
                  </Box>

                  <Box sx={{ flex: 1, overflowY: 'auto', p: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
                    {getChatMessages(activeSession).map((msg: any, idx: number) => {
                      const isInterviewer = msg.role === 'interviewer';
                      return (
                        <Box 
                          key={idx} 
                          sx={{ 
                            display: 'flex', 
                            flexDirection: 'column',
                            alignItems: isInterviewer ? 'flex-start' : 'flex-end',
                            width: '100%'
                          }}
                        >
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                            <Avatar sx={{ width: 24, height: 24, bgcolor: isInterviewer ? '#8b5cf6' : '#06b6d4', fontSize: '12px' }}>
                              {isInterviewer ? <MessageSquare size={12} /> : <User size={12} />}
                            </Avatar>
                            <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                              {isInterviewer ? 'AI Recruiter' : 'You'}
                            </Typography>
                          </Box>
                          <Box sx={{ 
                            p: 2, 
                            borderRadius: 3, 
                            maxWidth: '80%', 
                            bgcolor: isInterviewer ? 'rgba(255,255,255,0.02)' : 'rgba(139, 92, 246, 0.15)',
                            border: isInterviewer ? '1px solid rgba(255,255,255,0.04)' : '1px solid rgba(139, 92, 246, 0.25)',
                          }}>
                            <Typography variant="body2" sx={{ color: 'white', whiteSpace: 'pre-wrap', lineHeight: 1.5 }}>
                              {msg.content}
                            </Typography>
                          </Box>
                        </Box>
                      );
                    })}
                    <div ref={chatEndRef} />
                  </Box>

                  <Box sx={{ p: 2, borderTop: '1px solid rgba(255,255,255,0.06)' }}>
                    <form onSubmit={handleSendResponse} style={{ display: 'flex', gap: 12 }}>
                      <TextField
                        fullWidth
                        placeholder="Write your explanation or code logic here..."
                        size="medium"
                        value={userMsg}
                        onChange={(e) => setUserMsg(e.target.value)}
                        disabled={submittingMsg || finishing}
                      />
                      <Button
                        type="submit"
                        variant="contained"
                        disabled={submittingMsg || finishing}
                        sx={{ background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)', px: 3 }}
                      >
                        <Send size={18} />
                      </Button>
                    </form>
                  </Box>
                </Card>
              </Grid>

              {/* Sidebar Action */}
              <Grid item xs={12} md={4}>
                <Card className="glass-panel" sx={{ height: '60vh', p: 3, display: 'flex', flexDirection: 'column', justifyBlock: 'space-between' }}>
                  <CardContent sx={{ p: 0, flex: 1 }}>
                    <Typography variant="h6" gutterBottom sx={{ fontWeight: 700 }}>
                      Interview Instructions
                    </Typography>
                    <Typography variant="body2" sx={{ color: '#94a3b8', mb: 2, lineHeight: 1.6 }}>
                      This simulator monitors your analytical reasoning, terminology correctness, and structural layout. Explain your thought process in detail.
                    </Typography>
                    <Typography variant="body2" sx={{ color: '#94a3b8', mb: 4, lineHeight: 1.6 }}>
                      Once you feel you have fully answered the prompt (typically 3-4 replies), click below to finalize and let the AI grade your session.
                    </Typography>
                  </CardContent>

                  <Button
                    fullWidth
                    variant="contained"
                    color="primary"
                    disabled={finishing || submittingMsg}
                    onClick={handleFinish}
                    sx={{ py: 1.5, background: 'linear-gradient(135deg, #ef4444 0%, #ea580c 100%)' }}
                  >
                    {finishing ? <CircularProgress size={24} color="inherit" /> : 'Complete & Generate Scorecard'}
                  </Button>
                </Card>
              </Grid>
            </Grid>
          ) : (
            // Completed Scorecard View
            <Card className="glass-panel" sx={{ p: { xs: 2, sm: 4 } }}>
              <CardContent sx={{ p: 0 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 3 }}>
                  <Award size={32} style={{ color: '#eab308' }} />
                  <Typography variant="h4" sx={{ fontWeight: 800 }}>Evaluation Scorecard: {activeSession.mode}</Typography>
                </Box>

                {getScorecardData(activeSession) ? (
                  <Grid container spacing={4}>
                    <Grid item xs={12} md={5}>
                      <Card sx={{ bgcolor: 'rgba(255,255,255,0.01)', border: '1px solid rgba(255,255,255,0.04)', p: 3, height: '100%' }}>
                        <Typography variant="h6" gutterBottom sx={{ fontWeight: 700 }}>Overall Assessment Metrics</Typography>
                        
                        <Box sx={{ my: 3 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                            <Typography variant="body2">Logic / Correctness</Typography>
                            <Typography variant="body2" sx={{ fontWeight: 700 }}>{getScorecardData(activeSession).correctness}/10</Typography>
                          </Box>
                          <LinearProgress variant="determinate" value={getScorecardData(activeSession).correctness * 10} color="success" sx={{ height: 6, borderRadius: 3 }} />
                        </Box>

                        <Box sx={{ my: 3 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                            <Typography variant="body2">Communication</Typography>
                            <Typography variant="body2" sx={{ fontWeight: 700 }}>{getScorecardData(activeSession).communication}/10</Typography>
                          </Box>
                          <LinearProgress variant="determinate" value={getScorecardData(activeSession).communication * 10} color="primary" sx={{ height: 6, borderRadius: 3 }} />
                        </Box>

                        <Box sx={{ my: 3 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                            <Typography variant="body2">Optimization</Typography>
                            <Typography variant="body2" sx={{ fontWeight: 700 }}>{getScorecardData(activeSession).optimization}/10</Typography>
                          </Box>
                          <LinearProgress variant="determinate" value={getScorecardData(activeSession).optimization * 10} color="warning" sx={{ height: 6, borderRadius: 3 }} />
                        </Box>

                        <Box sx={{ my: 3 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                            <Typography variant="body2">Analytical Problem Solving</Typography>
                            <Typography variant="body2" sx={{ fontWeight: 700 }}>{getScorecardData(activeSession).problemSolving}/10</Typography>
                          </Box>
                          <LinearProgress variant="determinate" value={getScorecardData(activeSession).problemSolving * 10} color="secondary" sx={{ height: 6, borderRadius: 3 }} />
                        </Box>

                        <Divider sx={{ my: 3, borderColor: 'rgba(255,255,255,0.06)' }} />
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                          <Typography variant="h6">Overall Rating Score</Typography>
                          <Chip label={`${getScorecardData(activeSession).overallScore}/10`} color="primary" sx={{ fontSize: '18px', fontWeight: 'bold', px: 1, py: 2 }} />
                        </Box>
                      </Card>
                    </Grid>

                    <Grid item xs={12} md={7}>
                      <Typography variant="h6" gutterBottom sx={{ fontWeight: 700 }}>Detailed Feedback Summary</Typography>
                      <Typography variant="body1" sx={{ color: '#cbd5e1', lineHeight: 1.7, mb: 4 }}>
                        {getScorecardData(activeSession).generalFeedback}
                      </Typography>

                      <Typography variant="h6" gutterBottom sx={{ fontWeight: 700 }}>Session Transcript Logs</Typography>
                      <Box sx={{ maxHeight: '250px', overflowY: 'auto', border: '1px solid rgba(255,255,255,0.04)', p: 2, borderRadius: 2, bgcolor: 'rgba(0,0,0,0.1)' }}>
                        {getChatMessages(activeSession).map((msg: any, idx: number) => (
                          <Box key={idx} sx={{ mb: 2 }}>
                            <Typography variant="caption" sx={{ fontWeight: 'bold', color: msg.role === 'interviewer' ? '#a78bfa' : '#22d3ee' }}>
                              {msg.role === 'interviewer' ? 'Interviewer: ' : 'Candidate: '}
                            </Typography>
                            <Typography variant="body2" sx={{ color: '#cbd5e1' }}>{msg.content}</Typography>
                          </Box>
                        ))}
                      </Box>
                    </Grid>
                  </Grid>
                ) : (
                  <Typography variant="body1" sx={{ color: '#94a3b8' }}>
                    Scorecard generation error. Wait for AI calculations or restart simulation.
                  </Typography>
                )}

                <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 4 }}>
                  <Button variant="outlined" onClick={handleBackToSelect}>
                    Return to Lobby
                  </Button>
                </Box>
              </CardContent>
            </Card>
          )}
        </Box>
      )}
    </Box>
  );
};

export default MockInterview;
