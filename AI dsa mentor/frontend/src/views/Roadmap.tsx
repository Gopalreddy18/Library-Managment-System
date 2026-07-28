import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  Box, Typography, Card, CardContent, Button, MenuItem, TextField, 
  CircularProgress, Alert, Grid, Chip, Divider, List, ListItem, ListItemText 
} from '@mui/material';
import { Sparkles, Calendar, BookOpen, CheckSquare, Trophy, ChevronRight, HelpCircle } from 'lucide-react';

interface RoadmapWeek {
  id: number;
  weekNumber: number;
  topicTitle: string;
  conceptSummary: string;
  practiceProblems: string;
  revisionTasks: string;
  mockTests: string;
}

interface RoadmapData {
  id: number;
  targetCompany: string;
  currentSkillLevel: string;
  weakTopics: string;
  weeks: RoadmapWeek[];
}

const Roadmap: React.FC = () => {
  const { api } = useAuth();
  const navigate = useNavigate();

  const [roadmap, setRoadmap] = useState<RoadmapData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Form State for Roadmap Generation
  const [showForm, setShowForm] = useState(false);
  const [targetCompany, setTargetCompany] = useState('');
  const [currentSkillLevel, setCurrentSkillLevel] = useState('BEGINNER');
  const [weakTopics, setWeakTopics] = useState('');
  const [generating, setGenerating] = useState(false);

  const fetchRoadmap = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get('/api/roadmap/current');
      setRoadmap(res.data);
    } catch (err: any) {
      if (err.response?.status === 404) {
        // No roadmap exists
        setShowForm(true);
      } else {
        setError('Could not load roadmap data.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoadmap();
  }, []);

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    setGenerating(true);
    setError(null);

    try {
      const res = await api.post('/api/roadmap/generate', {
        targetCompany,
        currentSkillLevel,
        weakTopics
      });
      setRoadmap(res.data);
      setShowForm(false);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error generating roadmap plan.');
    } finally {
      setGenerating(false);
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
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 2 }}>
        <Box>
          <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 800 }}>
            Personalized Roadmap
          </Typography>
          <Typography variant="body1" sx={{ color: '#94a3b8' }}>
            AI-driven structure detailing weekly concepts and mock objectives to align you with target company expectations.
          </Typography>
        </Box>
        {roadmap && !showForm && (
          <Button 
            variant="outlined" 
            startIcon={<Sparkles size={16} />}
            onClick={() => setShowForm(true)}
            sx={{ mt: 1 }}
          >
            Regenerate Roadmap
          </Button>
        )}
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {showForm ? (
        <Card className="glass-panel" sx={{ maxWidth: '700px', mx: 'auto', p: { xs: 2, sm: 4 } }}>
          <CardContent sx={{ p: 0 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 3 }}>
              <Sparkles size={24} style={{ color: '#8b5cf6' }} />
              <Typography variant="h5" sx={{ fontWeight: 700 }}>Configure Roadmap Settings</Typography>
            </Box>
            <form onSubmit={handleGenerate}>
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Target Company"
                    variant="outlined"
                    required
                    value={targetCompany}
                    onChange={(e) => setTargetCompany(e.target.value)}
                    placeholder="e.g. Google, Amazon, Microsoft"
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    select
                    label="Current Skill Level"
                    variant="outlined"
                    value={currentSkillLevel}
                    onChange={(e) => setCurrentSkillLevel(e.target.value)}
                  >
                    <MenuItem value="BEGINNER">Beginner (Basic coding)</MenuItem>
                    <MenuItem value="INTERMEDIATE">Intermediate (Understand simple trees/searches)</MenuItem>
                    <MenuItem value="ADVANCED">Advanced (Graph, DP & complex queries)</MenuItem>
                  </TextField>
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Weak Topics"
                    variant="outlined"
                    value={weakTopics}
                    onChange={(e) => setWeakTopics(e.target.value)}
                    placeholder="e.g. Dynamic Programming, Graphs, Trees"
                    helperText="Comma-separated topics you want to emphasize."
                  />
                </Grid>
              </Grid>

              <Box sx={{ display: 'flex', gap: 2, mt: 4, justifyContent: 'flex-end' }}>
                {roadmap && (
                  <Button 
                    variant="outlined" 
                    onClick={() => setShowForm(false)}
                    disabled={generating}
                  >
                    Cancel
                  </Button>
                )}
                <Button
                  type="submit"
                  variant="contained"
                  disabled={generating}
                  sx={{ background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)' }}
                  startIcon={generating ? <CircularProgress size={16} color="inherit" /> : null}
                >
                  {generating ? 'AI Generating roadmap...' : 'Generate Roadmap'}
                </Button>
              </Box>
            </form>
          </CardContent>
        </Card>
      ) : roadmap ? (
        <Grid container spacing={4}>
          {/* Metadata Card */}
          <Grid item xs={12}>
            <Card sx={{ bgcolor: 'rgba(139, 92, 246, 0.05)', border: '1px solid rgba(139, 92, 246, 0.15)', p: 2 }}>
              <Grid container spacing={2} sx={{ textAlign: 'center' }}>
                <Grid item xs={12} sm={4}>
                  <Typography variant="body2" sx={{ color: '#a78bfa', fontWeight: 'bold' }}>Target Company</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{roadmap.targetCompany}</Typography>
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Typography variant="body2" sx={{ color: '#a78bfa', fontWeight: 'bold' }}>Current Skill Profile</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{roadmap.currentSkillLevel}</Typography>
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Typography variant="body2" sx={{ color: '#a78bfa', fontWeight: 'bold' }}>Emphasizing Topic Gaps</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{roadmap.weakTopics || 'None'}</Typography>
                </Grid>
              </Grid>
            </Card>
          </Grid>

          {/* Timeline Cards */}
          <Grid item xs={12}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4, position: 'relative', pl: { xs: 2, sm: 4 } }}>
              {/* Timeline bar */}
              <Box sx={{ 
                position: 'absolute', 
                top: '20px', 
                bottom: '20px', 
                left: { xs: '9px', sm: '21px' }, 
                width: '4px', 
                bgcolor: 'rgba(139, 92, 246, 0.2)',
                zIndex: 0
              }} />

              {roadmap.weeks?.map((week, idx) => (
                <Box key={week.id} sx={{ display: 'flex', gap: 3, position: 'relative', zIndex: 1 }}>
                  {/* Circle Pin indicator */}
                  <Box sx={{ 
                    width: 20, 
                    height: 20, 
                    borderRadius: '50%', 
                    bgcolor: '#8b5cf6', 
                    border: '4px solid #07040f',
                    mt: 3,
                    boxShadow: '0 0 10px #8b5cf6'
                  }} />

                  <Card className="glass-panel" sx={{ flex: 1 }}>
                    <CardContent sx={{ p: 4 }}>
                      <Grid container spacing={3}>
                        <Grid item xs={12} md={7}>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                            <Calendar size={16} style={{ color: '#a78bfa' }} />
                            <Typography variant="caption" sx={{ color: '#a78bfa', fontWeight: 'bold', letterSpacing: '0.05rem' }}>
                              WEEK {week.weekNumber}
                            </Typography>
                          </Box>
                          <Typography variant="h5" gutterBottom sx={{ fontWeight: 700, color: 'white' }}>
                            {week.topicTitle}
                          </Typography>
                          <Typography variant="body2" sx={{ color: '#cbd5e1', lineHeight: 1.6, mt: 2 }}>
                            {week.conceptSummary}
                          </Typography>
                        </Grid>

                        <Grid item xs={12} md={5}>
                          <Box sx={{ bgcolor: 'rgba(255,255,255,0.01)', p: 3, borderRadius: 3, border: '1px solid rgba(255,255,255,0.03)', height: '100%' }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                              <BookOpen size={16} style={{ color: '#06b6d4' }} />
                              <Typography variant="body2" sx={{ fontWeight: 700, color: 'white' }}>Practice Focus</Typography>
                            </Box>
                            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mb: 3 }}>
                              {week.practiceProblems.split(',').map(prob => (
                                <Chip key={prob} label={prob.trim()} size="small" sx={{ bgcolor: 'rgba(6, 182, 212, 0.1)', color: '#22d3ee', border: '1px solid rgba(6, 182, 212, 0.2)' }} />
                              ))}
                            </Box>

                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                              <CheckSquare size={16} style={{ color: '#10b981' }} />
                              <Typography variant="body2" sx={{ fontWeight: 700, color: 'white' }}>Revision Goal</Typography>
                            </Box>
                            <Typography variant="caption" sx={{ color: '#cbd5e1', display: 'block', mb: 3 }}>
                              {week.revisionTasks}
                            </Typography>

                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                              <Trophy size={16} style={{ color: '#eab308' }} />
                              <Typography variant="body2" sx={{ fontWeight: 700, color: 'white' }}>Mock Assessment</Typography>
                            </Box>
                            <Typography variant="caption" sx={{ color: '#cbd5e1', display: 'block' }}>
                              {week.mockTests}
                            </Typography>
                          </Box>
                        </Grid>
                      </Grid>
                    </CardContent>
                  </Card>
                </Box>
              ))}
            </Box>
          </Grid>
        </Grid>
      ) : (
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', py: 8 }}>
          <HelpCircle size={64} style={{ color: '#94a3b8', marginBottom: '16px' }} />
          <Typography variant="body1" gutterBottom sx={{ color: '#94a3b8' }}>
            No personalized roadmap exists yet.
          </Typography>
          <Button 
            variant="contained" 
            onClick={() => setShowForm(true)}
            sx={{ mt: 2, background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)' }}
          >
            Create Learning Plan
          </Button>
        </Box>
      )}
    </Box>
  );
};

export default Roadmap;
