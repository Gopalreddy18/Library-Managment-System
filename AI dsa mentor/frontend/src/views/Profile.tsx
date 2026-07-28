import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  Box, Typography, Card, CardContent, Button, MenuItem, TextField, 
  CircularProgress, Alert, Grid, Avatar, Chip, Divider 
} from '@mui/material';
import { User, Mail, GraduationCap, Building, Briefcase, Award, Save } from 'lucide-react';

const Profile: React.FC = () => {
  const { api } = useAuth();

  const [profile, setProfile] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Edit fields
  const [name, setName] = useState('');
  const [college, setCollege] = useState('');
  const [graduationYear, setGraduationYear] = useState<number>(2026);
  const [targetCompany, setTargetCompany] = useState('');
  const [targetRole, setTargetRole] = useState('');
  const [currentDsaLevel, setCurrentDsaLevel] = useState('BEGINNER');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await api.get('/api/users/profile');
        setProfile(res.data);
        setName(res.data.name);
        setCollege(res.data.college || '');
        setGraduationYear(res.data.graduationYear || 2026);
        setTargetCompany(res.data.targetCompany || '');
        setTargetRole(res.data.targetRole || '');
        setCurrentDsaLevel(res.data.currentDsaLevel || 'BEGINNER');
      } catch (err: any) {
        setError('Failed to load profile.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const res = await api.put('/api/users/profile', {
        name,
        college,
        graduationYear,
        targetCompany,
        targetRole,
        currentDsaLevel
      });
      setProfile(res.data);
      setSuccess('Profile updated successfully!');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error updating profile.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' }}>
        <CircularProgress color="primary" />
      </Box>
    );
  }

  const strengthsList = profile?.topicStrengths ? profile.topicStrengths.split(',').map((s: string) => s.trim()) : [];
  const weaknessesList = profile?.topicWeaknesses ? profile.topicWeaknesses.split(',').map((w: string) => w.trim()) : [];

  return (
    <Box className="animate-fade-in" sx={{ mt: 2 }}>
      <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 800, mb: 4 }}>
        User Profile Settings
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 3 }}>{success}</Alert>}

      <Grid container spacing={4}>
        {/* Left Side: Avatar Card */}
        <Grid item xs={12} md={4}>
          <Card className="glass-panel" sx={{ p: 3, textAlign: 'center' }}>
            <CardContent sx={{ p: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
              <Avatar 
                sx={{ bgcolor: '#8b5cf6', width: 96, height: 96, fontSize: '36px', fontWeight: 'bold' }}
                alt={profile?.name}
              >
                {profile?.name?.charAt(0).toUpperCase()}
              </Avatar>
              
              <Box>
                <Typography variant="h5" sx={{ fontWeight: 700 }}>{profile?.name}</Typography>
                <Typography variant="body2" sx={{ color: '#94a3b8', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, mt: 0.5 }}>
                  <Mail size={14} /> {profile?.email}
                </Typography>
              </Box>

              <Chip 
                label={profile?.role === 'ROLE_ADMIN' ? 'Administrator' : 'Student Account'} 
                color={profile?.role === 'ROLE_ADMIN' ? 'error' : 'primary'}
                sx={{ fontWeight: 'bold', mt: 1 }}
              />

              <Divider sx={{ my: 2, width: '100%', borderColor: 'rgba(255,255,255,0.06)' }} />

              <Grid container spacing={2} sx={{ textAlign: 'center' }}>
                <Grid item xs={4}>
                  <Typography variant="caption" sx={{ color: '#94a3b8', display: 'block' }}>Solved</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{profile?.totalProblemsSolved || 0}</Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="caption" sx={{ color: '#94a3b8', display: 'block' }}>Accuracy</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{profile?.accuracy || 0}%</Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="caption" sx={{ color: '#94a3b8', display: 'block' }}>Streak</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{profile?.codingStreak || 0}d</Typography>
                </Grid>
              </Grid>

              <Divider sx={{ my: 2, width: '100%', borderColor: 'rgba(255,255,255,0.06)' }} />

              {/* Strengths / Weaknesses */}
              <Box sx={{ width: '100%', textAlign: 'left' }}>
                <Typography variant="body2" sx={{ color: '#94a3b8', fontWeight: 600, mb: 1 }}>Topic Strengths</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mb: 3 }}>
                  {strengthsList.length > 0 ? strengthsList.map((tag: string) => (
                    <Chip key={tag} label={tag} color="success" size="small" variant="outlined" />
                  )) : <Typography variant="caption" sx={{ color: '#94a3b8' }}>None identified yet.</Typography>}
                </Box>

                <Typography variant="body2" sx={{ color: '#94a3b8', fontWeight: 600, mb: 1 }}>Topic Weaknesses</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {weaknessesList.length > 0 ? weaknessesList.map((tag: string) => (
                    <Chip key={tag} label={tag} color="error" size="small" variant="outlined" />
                  )) : <Typography variant="caption" sx={{ color: '#94a3b8' }}>None identified yet.</Typography>}
                </Box>
              </Box>

            </CardContent>
          </Card>
        </Grid>

        {/* Right Side: Edit Form */}
        <Grid item xs={12} md={8}>
          <Card className="glass-panel" sx={{ p: 4 }}>
            <CardContent sx={{ p: 0 }}>
              <Typography variant="h5" gutterBottom sx={{ fontWeight: 700, mb: 3 }}>
                Profile & Professional Details
              </Typography>
              
              <form onSubmit={handleSave}>
                <Grid container spacing={3}>
                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                      <User style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                      <TextField
                        fullWidth
                        label="Full Name"
                        variant="standard"
                        required
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                      />
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                      <GraduationCap style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                      <TextField
                        fullWidth
                        label="College / University"
                        variant="standard"
                        value={college}
                        onChange={(e) => setCollege(e.target.value)}
                      />
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                      <GraduationCap style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                      <TextField
                        fullWidth
                        label="Graduation Year"
                        variant="standard"
                        type="number"
                        value={graduationYear}
                        onChange={(e) => setGraduationYear(Number(e.target.value))}
                      />
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                      <Building style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                      <TextField
                        fullWidth
                        label="Target Company"
                        variant="standard"
                        value={targetCompany}
                        onChange={(e) => setTargetCompany(e.target.value)}
                        placeholder="e.g. Google, Apple"
                      />
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                      <Briefcase style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                      <TextField
                        fullWidth
                        label="Target Role"
                        variant="standard"
                        value={targetRole}
                        onChange={(e) => setTargetRole(e.target.value)}
                        placeholder="e.g. Backend Developer"
                      />
                    </Box>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                      <Award style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                      <TextField
                        fullWidth
                        select
                        label="Current DSA Level"
                        variant="standard"
                        value={currentDsaLevel}
                        onChange={(e) => setCurrentDsaLevel(e.target.value)}
                      >
                        <MenuItem value="BEGINNER">Beginner</MenuItem>
                        <MenuItem value="INTERMEDIATE">Intermediate</MenuItem>
                        <MenuItem value="ADVANCED">Advanced</MenuItem>
                      </TextField>
                    </Box>
                  </Grid>
                </Grid>

                <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 4 }}>
                  <Button
                    type="submit"
                    variant="contained"
                    disabled={saving}
                    startIcon={saving ? <CircularProgress size={16} color="inherit" /> : <Save size={16} />}
                    sx={{ background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)' }}
                  >
                    Save Profile Changes
                  </Button>
                </Box>
              </form>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Profile;
