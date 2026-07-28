import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  Box, Typography, Card, CardContent, Table, TableBody, TableCell, TableContainer, 
  TableHead, TableRow, Button, Chip, TextField, MenuItem, CircularProgress, Alert, 
  Paper, IconButton, InputAdornment 
} from '@mui/material';
import { Search, CheckCircle, ExternalLink, RefreshCw } from 'lucide-react';

interface Problem {
  id: number;
  title: string;
  difficulty: string;
  tags: string;
  expectedComplexity: string;
}

const ProblemList: React.FC = () => {
  const { api } = useAuth();
  const navigate = useNavigate();

  const [problems, setProblems] = useState<Problem[]>([]);
  const [solvedIds, setSolvedIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filters
  const [search, setSearch] = useState('');
  const [difficultyFilter, setDifficultyFilter] = useState('ALL');

  const fetchProblemsAndSolvedStatus = async () => {
    setLoading(true);
    setError(null);
    try {
      const probRes = await api.get('/api/problems');
      setProblems(probRes.data);

      const subRes = await api.get('/api/submissions');
      const solved = subRes.data
        .filter((sub: any) => sub.result === 'ACCEPTED')
        .map((sub: any) => sub.problem.id);
      setSolvedIds(solved);
    } catch (err: any) {
      setError('Failed to retrieve DSA problems.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProblemsAndSolvedStatus();
  }, []);

  const handleResetFilters = () => {
    setSearch('');
    setDifficultyFilter('ALL');
  };

  const getDifficultyColor = (diff: string) => {
    switch (diff.toUpperCase()) {
      case 'EASY': return 'success';
      case 'MEDIUM': return 'warning';
      case 'HARD': return 'error';
      default: return 'default';
    }
  };

  // Filter Logic
  const filteredProblems = problems.filter(problem => {
    const matchSearch = problem.title.toLowerCase().includes(search.toLowerCase()) || 
                        problem.tags.toLowerCase().includes(search.toLowerCase());
    const matchDifficulty = difficultyFilter === 'ALL' || problem.difficulty.toUpperCase() === difficultyFilter;
    return matchSearch && matchDifficulty;
  });

  return (
    <Box className="animate-fade-in" sx={{ mt: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 800 }}>
            Problem Bank
          </Typography>
          <Typography variant="body1" sx={{ color: '#94a3b8' }}>
            Sharpen your fundamentals by tackling structured data structures & algorithms challenges.
          </Typography>
        </Box>
        <IconButton onClick={fetchProblemsAndSolvedStatus} color="primary" sx={{ border: '1px solid rgba(255,255,255,0.06)' }}>
          <RefreshCw size={20} />
        </IconButton>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Filter panel */}
      <Card className="glass-panel" sx={{ mb: 4 }}>
        <CardContent sx={{ p: 3, display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
          <TextField
            size="small"
            placeholder="Search title or tag..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            sx={{ flex: 1, minWidth: '250px' }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search size={18} style={{ color: '#94a3b8' }} />
                </InputAdornment>
              ),
            }}
          />

          <TextField
            select
            size="small"
            label="Difficulty"
            value={difficultyFilter}
            onChange={(e) => setDifficultyFilter(e.target.value)}
            sx={{ width: '150px' }}
          >
            <MenuItem value="ALL">All Levels</MenuItem>
            <MenuItem value="EASY">Easy</MenuItem>
            <MenuItem value="MEDIUM">Medium</MenuItem>
            <MenuItem value="HARD">Hard</MenuItem>
          </TextField>

          <Button size="small" onClick={handleResetFilters} sx={{ color: '#94a3b8' }}>
            Reset Filters
          </Button>
        </CardContent>
      </Card>

      {/* Problems Table */}
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress color="primary" />
        </Box>
      ) : (
        <TableContainer component={Paper} className="glass-panel" sx={{ border: 'none', overflow: 'hidden' }}>
          <Table sx={{ minWidth: 650 }} aria-label="problems table">
            <TableHead sx={{ bgcolor: 'rgba(255,255,255,0.01)', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 'bold', color: '#94a3b8' }}>Status</TableCell>
                <TableCell sx={{ fontWeight: 'bold', color: '#94a3b8' }}>Problem Title</TableCell>
                <TableCell sx={{ fontWeight: 'bold', color: '#94a3b8' }}>Tags</TableCell>
                <TableCell sx={{ fontWeight: 'bold', color: '#94a3b8' }}>Complexity Goal</TableCell>
                <TableCell sx={{ fontWeight: 'bold', color: '#94a3b8' }}>Difficulty</TableCell>
                <TableCell sx={{ fontWeight: 'bold', color: '#94a3b8' }} align="right">Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredProblems.map((problem) => {
                const isSolved = solvedIds.includes(problem.id);
                return (
                  <TableRow 
                    key={problem.id}
                    sx={{ 
                      '&:last-child td, &:last-child th': { border: 0 },
                      borderBottom: '1px solid rgba(255,255,255,0.04)',
                      '&:hover': { bgcolor: 'rgba(255,255,255,0.01)' }
                    }}
                  >
                    <TableCell>
                      {isSolved ? (
                        <CheckCircle size={20} style={{ color: '#10b981' }} />
                      ) : (
                        <Box sx={{ width: 20, height: 20, borderRadius: '50%', border: '2px dashed rgba(255,255,255,0.15)' }} />
                      )}
                    </TableCell>
                    <TableCell sx={{ fontWeight: 600, fontSize: '15px' }}>
                      {problem.title}
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                        {problem.tags.split(',').map(tag => (
                          <Chip key={tag} label={tag.trim()} size="small" variant="outlined" sx={{ fontSize: '11px', borderColor: 'rgba(255,255,255,0.08)' }} />
                        ))}
                      </Box>
                    </TableCell>
                    <TableCell sx={{ color: '#94a3b8', fontSize: '13px' }}>
                      {problem.expectedComplexity || 'Not defined'}
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={problem.difficulty} 
                        color={getDifficultyColor(problem.difficulty)} 
                        size="small" 
                        sx={{ fontWeight: 'bold', fontSize: '11px' }}
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Button
                        variant="text"
                        onClick={() => navigate(`/problems/${problem.id}`)}
                        endIcon={<ExternalLink size={14} />}
                        sx={{ color: '#a78bfa' }}
                      >
                        Solve
                      </Button>
                    </TableCell>
                  </TableRow>
                );
              })}
              {filteredProblems.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 6, color: '#94a3b8' }}>
                    No problems match the current search or filters.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Box>
  );
};

export default ProblemList;
