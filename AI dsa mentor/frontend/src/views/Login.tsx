import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  Box, Container, Typography, TextField, Button, Alert, Card, CardContent, CircularProgress 
} from '@mui/material';
import { Code, Key, Mail } from 'lucide-react';

const Login: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await login(email, password);
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      p: 2
    }}>
      <Container maxWidth="sm">
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
                DSA Mentor
              </Typography>
              <Typography variant="body2" sx={{ color: '#94a3b8', textAlign: 'center' }}>
                Elevate your coding skills with AI-powered personalized mentorship.
              </Typography>
            </Box>

            {error && (
              <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
                {error}
              </Alert>
            )}

            <form onSubmit={handleSubmit}>
              <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 3 }}>
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

              <Box sx={{ display: 'flex', alignItems: 'flex-end', mb: 4 }}>
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

              <Button
                type="submit"
                fullWidth
                variant="contained"
                disabled={loading}
                sx={{
                  py: 1.5,
                  mb: 3,
                  background: 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)',
                  boxShadow: '0 4px 14px 0 rgba(139, 92, 246, 0.4)',
                  '&:hover': {
                    background: 'linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%)',
                  }
                }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : 'Sign In'}
              </Button>
            </form>

            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
              <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                Don't have an account?{' '}
                <Link to="/register" style={{ color: '#a78bfa', textDecoration: 'none', fontWeight: 600 }}>
                  Create account
                </Link>
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
};

export default Login;
