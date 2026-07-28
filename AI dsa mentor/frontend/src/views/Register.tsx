import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  Box, Container, Typography, TextField, Button, Alert, Card, CardContent, 
  CircularProgress, MenuItem, Grid 
} from '@mui/material';
import { Code, User, Mail, Key, GraduationCap, Briefcase, Building } from 'lucide-react';

const Register: React.FC = () => {
  const { signup } = useAuth();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [college, setCollege] = useState('');
  const [graduationYear, setGraduationYear] = useState<number>(2026);
  const [targetCompany, setTargetCompany] = useState('');
  const [targetRole, setTargetRole] = useState('');
  const [currentDsaLevel, setCurrentDsaLevel] = useState('BEGINNER');

  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await signup({
        name,
        email,
        password,
        college,
        graduationYear,
        targetCompany,
        targetRole,
        currentDsaLevel,
        role: 'student' // default signup role
      });
      navigate('/login');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Try again.');
    } finally {
      setLoading(false);
    }
  };

  const dsaLevels = [
    { value: 'BEGINNER', label: 'Beginner (No background / basic array operations)' },
    { value: 'INTERMEDIATE', label: 'Intermediate (Data structures / standard algorithms)' },
    { value: 'ADVANCED', label: 'Advanced (Graph theory / Complex DP / Optimization)' },
  ];

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      p: 2,
      py: 6
    }}>
      <Container maxWidth="md">
        <Card className="glass-panel" sx={{ border: '1px solid rgba(255,255,255,0.08)' }}>
          <CardContent sx={{ p: { xs: 4, sm: 6 } }}>
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 4 }}>
              <Box sx={{ 
                p: 1.5, 
                borderRadius: '50%', 
                background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)', 
                color: 'white',
                mb: 2,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <Code size={32} />
              </Box>
              <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 800 }} className="gradient-text">
                Join DSA Mentor
              </Typography>
              <Typography variant="body2" sx={{ color: '#94a3b8', textAlign: 'center' }}>
                Setup your coding profile and start mastering DSA.
              </Typography>
            </Box>

            {error && (
              <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
                {error}
              </Alert>
            )}

            <form onSubmit={handleSubmit}>
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
                      InputLabelProps={{ style: { color: '#94a3b8' } }}
                    />
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                    <Mail style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                    <TextField
                      fullWidth
                      label="Email Address"
                      variant="standard"
                      type="email"
                      required
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      InputLabelProps={{ style: { color: '#94a3b8' } }}
                    />
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                    <Key style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                    <TextField
                      fullWidth
                      label="Password"
                      variant="standard"
                      type="password"
                      required
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      InputLabelProps={{ style: { color: '#94a3b8' } }}
                    />
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 2 }}>
                    <GraduationCap style={{ color: '#94a3b8', marginRight: '12px', marginBottom: '8px' }} />
                    <TextField
                      fullWidth
                      label="College/University"
                      variant="standard"
                      value={college}
                      onChange={(e) => setCollege(e.target.value)}
                      InputLabelProps={{ style: { color: '#94a3b8' } }}
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
                      InputLabelProps={{ style: { color: '#94a3b8' } }}
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
                      placeholder="e.g. Google, Meta"
                      InputLabelProps={{ style: { color: '#94a3b8' } }}
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
                      placeholder="e.g. Software Engineer"
                      InputLabelProps={{ style: { color: '#94a3b8' } }}
                    />
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    select
                    label="Current DSA Level"
                    variant="standard"
                    value={currentDsaLevel}
                    onChange={(e) => setCurrentDsaLevel(e.target.value)}
                    InputLabelProps={{ style: { color: '#94a3b8' } }}
                  >
                    {dsaLevels.map((option) => (
                      <MenuItem key={option.value} value={option.value}>
                        {option.label}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
              </Grid>

              <Button
                type="submit"
                fullWidth
                variant="contained"
                disabled={loading}
                sx={{
                  py: 1.5,
                  mt: 4,
                  mb: 3,
                  background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)',
                  boxShadow: '0 4px 14px 0 rgba(139, 92, 246, 0.4)',
                  '&:hover': {
                    background: 'linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%)',
                  }
                }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : 'Register Account'}
              </Button>
            </form>

            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
              <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                Already have an account?{' '}
                <Link to="/login" style={{ color: '#a78bfa', textDecoration: 'none', fontWeight: 600 }}>
                  Sign In
                </Link>
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
};

export default Register;
