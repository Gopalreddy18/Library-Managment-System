import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  Box, Grid, Typography, Card, CardContent, Button, Chip, List, ListItem, 
  ListItemText, Divider, LinearProgress, CircularProgress, Alert 
} from '@mui/material';
import { Flame, CheckCircle2, TrendingUp, AlertTriangle, ArrowRight, Award, HelpCircle, Code } from 'lucide-react';

interface Submission {
  id: number;
  problem: { id: number; title: string; difficulty: string };
  language: string;
  submissionTime: string;
  result: string;
}

interface Recommendation {
  id: number;
  type: string;
  targetId: number;
  targetName: string;
  reasoning: string;
}

interface Roadmap {
  id: number;
  targetCompany: string;
  currentSkillLevel: string;
  weeks: any[];
}

interface DashboardData {
  name: string;
  streak: number;
  solvedCount: number;
  accuracy: number;
  currentLevel: string;
  targetCompany: string;
  targetRole: string;
  strengths: string;
  weaknesses: string;
  currentRoadmap: Roadmap | null;
  recommendations: Recommendation[];
  recentSubmissions: Submission[];
}

const Dashboard: React.FC = () => {
  const { api } = useAuth();
  const navigate = useNavigate();

  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const res = await api.get('/api/dashboard');
        setData(res.data);
      } catch (err: any) {
        setError('Failed to load dashboard metrics.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' }}>
        <CircularProgress color="primary" />
      </Box>
    );
  }

  if (error || !data) {
    return (
      <Alert severity="error" sx={{ my: 4 }}>
        {error || 'No dashboard data found.'}
      </Alert>
    );
  }

  // Format strengths/weaknesses string into array
  const strengthsList = data.strengths ? data.strengths.split(',').map(s => s.trim()) : [];
  const weaknessesList = data.weaknesses ? data.weaknesses.split(',').map(w => w.trim()) : [];

  return (
    <Box className="animate-fade-in" sx={{ mt: 2 }}>
      {/* Welcome Banner */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 800 }}>
          Welcome back, <span className="gradient-text">{data.name}</span>!
        </Typography>
        <Typography variant="body1" sx={{ color: '#94a3b8' }}>
          Targeting <span style={{ color: 'white', fontWeight: 600 }}>{data.targetRole || 'Software Engineer'}</span> at <span style={{ color: '#8b5cf6', fontWeight: 600 }}>{data.targetCompany || 'Top Tech Companies'}</span>.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Metric Cards Row */}
        <Grid item xs={12} sm={6} md={3}>
          <Card className="glass-panel" sx={{ height: '100%' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Box sx={{ p: 1.5, borderRadius: 3, bgcolor: 'rgba(239, 68, 68, 0.12)', color: '#ef4444' }}>
                <Flame size={28} />
              </Box>
              <Box>
                <Typography variant="body2" sx={{ color: '#94a3b8' }}>Coding Streak</Typography>
                <Typography variant="h4" sx={{ fontWeight: 700 }}>{data.streak} Days</Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card className="glass-panel" sx={{ height: '100%' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Box sx={{ p: 1.5, borderRadius: 3, bgcolor: 'rgba(16, 185, 129, 0.12)', color: '#10b981' }}>
                <CheckCircle2 size={28} />
              </Box>
              <Box>
                <Typography variant="body2" sx={{ color: '#94a3b8' }}>Problems Solved</Typography>
                <Typography variant="h4" sx={{ fontWeight: 700 }}>{data.solvedCount}</Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card className="glass-panel" sx={{ height: '100%' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Box sx={{ p: 1.5, borderRadius: 3, bgcolor: 'rgba(6, 182, 212, 0.12)', color: '#06b6d4' }}>
                <TrendingUp size={28} />
              </Box>
              <Box>
                <Typography variant="body2" sx={{ color: '#94a3b8' }}>Success Accuracy</Typography>
                <Typography variant="h4" sx={{ fontWeight: 700 }}>{data.accuracy}%</Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card className="glass-panel" sx={{ height: '100%' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Box sx={{ p: 1.5, borderRadius: 3, bgcolor: 'rgba(139, 92, 246, 0.12)', color: '#8b5cf6' }}>
                <Award size={28} />
              </Box>
              <Box>
                <Typography variant="body2" sx={{ color: '#94a3b8' }}>Current Level</Typography>
                <Typography variant="h4" sx={{ fontWeight: 700 }}>{data.currentLevel}</Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Learning Roadmap Widget */}
        <Grid item xs={12} md={8}>
          <Card className="glass-panel" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flex: 1, p: 4 }}>
              <Typography variant="h5" gutterBottom sx={{ fontWeight: 700, mb: 3 }}>
                Active Learning Roadmap
              </Typography>

              {data.currentRoadmap ? (
                <Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                    <Typography variant="body1" sx={{ fontWeight: 600 }}>
                      Syllabus for {data.currentRoadmap.targetCompany} ({data.currentRoadmap.currentSkillLevel})
                    </Typography>
                    <Typography variant="body2" sx={{ color: '#8b5cf6', fontWeight: 600 }}>
                      4 Weeks Duration
                    </Typography>
                  </Box>
                  <Typography variant="body2" sx={{ color: '#94a3b8', mb: 3 }}>
                    Tailored weekly plan covering dynamic concepts, revision assignments, and mock interviews to prepare you for real technical reviews.
                  </Typography>

                  <Grid container spacing={2}>
                    {data.currentRoadmap.weeks?.map((week: any) => (
                      <Grid item xs={12} sm={6} key={week.id}>
                        <Card sx={{ bgcolor: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.04)', borderRadius: 3 }}>
                          <CardContent sx={{ p: 2 }}>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                              <Typography variant="caption" sx={{ color: '#a78bfa', fontWeight: 'bold' }}>
                                WEEK {week.weekNumber}
                              </Typography>
                            </Box>
                            <Typography variant="body1" sx={{ fontWeight: 600, mb: 1 }}>
                              {week.topicTitle}
                            </Typography>
                            <Typography variant="caption" sx={{ color: '#94a3b8', display: 'block', height: '40px', overflow: 'hidden' }}>
                              {week.conceptSummary}
                            </Typography>
                          </CardContent>
                        </Card>
                      </Grid>
                    ))}
                  </Grid>

                  <Button 
                    variant="outlined" 
                    onClick={() => navigate('/roadmap')}
                    sx={{ mt: 3, gap: 1 }}
                  >
                    View Roadmap Timeline <ArrowRight size={16} />
                  </Button>
                </Box>
              ) : (
                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', py: 4 }}>
                  <HelpCircle size={48} style={{ color: '#94a3b8', marginBottom: '16px' }} />
                  <Typography variant="body1" gutterBottom sx={{ color: '#94a3b8' }}>
                    You don't have an active learning roadmap generated yet.
                  </Typography>
                  <Button 
                    variant="contained" 
                    onClick={() => navigate('/roadmap')}
                    sx={{ mt: 2, background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)' }}
                  >
                    Generate Personalized Roadmap
                  </Button>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Skill Gap Analysis Widget */}
        <Grid item xs={12} md={4}>
          <Card className="glass-panel" sx={{ height: '100%' }}>
            <CardContent sx={{ p: 4 }}>
              <Typography variant="h5" gutterBottom sx={{ fontWeight: 700, mb: 3 }}>
                DSA Skill Gap Analysis
              </Typography>

              <Typography variant="body2" sx={{ color: '#94a3b8', mb: 1, fontWeight: 600 }}>
                STRENGTHS
              </Typography>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 4 }}>
                {strengthsList.length > 0 ? strengthsList.map(tag => (
                  <Chip key={tag} label={tag} color="success" variant="outlined" sx={{ fontWeight: 600 }} />
                )) : (
                  <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                    Solve problems correctly to define topic strengths.
                  </Typography>
                )}
              </Box>

              <Typography variant="body2" sx={{ color: '#94a3b8', mb: 1, fontWeight: 600 }}>
                WEAK AREAS
              </Typography>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 4 }}>
                {weaknessesList.length > 0 ? weaknessesList.map(tag => (
                  <Chip key={tag} label={tag} color="error" variant="outlined" sx={{ fontWeight: 600 }} />
                )) : (
                  <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                    No weak areas identified yet. Great work!
                  </Typography>
                )}
              </Box>

              <Divider sx={{ my: 2, borderColor: 'rgba(255,255,255,0.06)' }} />
              <Typography variant="body2" sx={{ color: '#94a3b8', fontSize: '13px' }}>
                * Skill gaps are dynamically recalculated on each submission, assessing accuracy against tag difficulty.
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Recommended Problems */}
        <Grid item xs={12} md={6}>
          <Card className="glass-panel" sx={{ height: '100%' }}>
            <CardContent sx={{ p: 4 }}>
              <Typography variant="h5" gutterBottom sx={{ fontWeight: 700, mb: 3 }}>
                Recommended Practice
              </Typography>
              <List>
                {data.recommendations?.map((rec) => (
                  <ListItem 
                    key={rec.id} 
                    sx={{ 
                      px: 2, 
                      py: 1.5, 
                      mb: 2, 
                      bgcolor: 'rgba(255,255,255,0.02)', 
                      borderRadius: 3, 
                      border: '1px solid rgba(255,255,255,0.04)',
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'flex-start'
                    }}
                  >
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center', mb: 1 }}>
                      <Typography variant="body1" sx={{ fontWeight: 600, color: 'white' }}>
                        {rec.targetName}
                      </Typography>
                      <Button 
                        size="small"
                        onClick={() => navigate(`/problems/${rec.targetId}`)}
                        sx={{ color: '#a78bfa' }}
                      >
                        Solve
                      </Button>
                    </Box>
                    <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                      {rec.reasoning}
                    </Typography>
                  </ListItem>
                ))}
                {(!data.recommendations || data.recommendations.length === 0) && (
                  <Typography variant="body1" sx={{ color: '#94a3b8' }}>
                    No recommendations available. Start solving problems to receive custom practice suggestions.
                  </Typography>
                )}
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Submissions */}
        <Grid item xs={12} md={6}>
          <Card className="glass-panel" sx={{ height: '100%' }}>
            <CardContent sx={{ p: 4 }}>
              <Typography variant="h5" gutterBottom sx={{ fontWeight: 700, mb: 3 }}>
                Recent Activity
              </Typography>
              <List>
                {data.recentSubmissions?.map((sub) => (
                  <ListItem 
                    key={sub.id} 
                    sx={{ 
                      px: 2, 
                      py: 1.5, 
                      mb: 1.5, 
                      bgcolor: 'rgba(255,255,255,0.02)', 
                      borderRadius: 3, 
                      border: '1px solid rgba(255,255,255,0.04)',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center'
                    }}
                  >
                    <Box>
                      <Typography variant="body1" sx={{ fontWeight: 600 }}>
                        {sub.problem?.title}
                      </Typography>
                      <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                        {sub.language.toUpperCase()} • {new Date(sub.submissionTime).toLocaleDateString()}
                      </Typography>
                    </Box>
                    <Chip 
                      label={sub.result === 'ACCEPTED' ? 'Accepted' : sub.result.replace('_', ' ')} 
                      color={sub.result === 'ACCEPTED' ? 'success' : 'error'} 
                      size="small"
                      sx={{ fontWeight: 'bold' }}
                    />
                  </ListItem>
                ))}
                {(!data.recentSubmissions || data.recentSubmissions.length === 0) && (
                  <Typography variant="body1" sx={{ color: '#94a3b8' }}>
                    No recent submissions. Try solving an Easy-level problem to kick off activity log.
                  </Typography>
                )}
              </List>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;
