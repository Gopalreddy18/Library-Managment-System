import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext.tsx';
import { 
  AppBar, Toolbar, Typography, Button, IconButton, Menu, MenuItem, Box, 
  Avatar, Tooltip, Container, useMediaQuery, useTheme, Drawer, List, ListItem, ListItemText, ListItemButton
} from '@mui/material';
import { Menu as MenuIcon, LogOut, Code, Calendar, Award, User, Home, BookOpen } from 'lucide-react';

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const [anchorElUser, setAnchorElUser] = useState<null | HTMLElement>(null);
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handleNavigate = (path: string) => {
    navigate(path);
    setMobileOpen(false);
  };

  const handleLogout = async () => {
    handleCloseUserMenu();
    await logout();
    navigate('/login');
  };

  const menuItems = [
    { text: 'Dashboard', path: '/', icon: <Home size={18} /> },
    { text: 'Problems', path: '/problems', icon: <BookOpen size={18} /> },
    { text: 'Roadmap', path: '/roadmap', icon: <Calendar size={18} /> },
    { text: 'Mock Interview', path: '/interview', icon: <Award size={18} /> },
  ];

  const drawer = (
    <Box sx={{ width: 250, bgcolor: '#0b0716', height: '100%', color: 'white', pt: 2 }}>
      <Typography variant="h6" sx={{ px: 2, pb: 2, fontWeight: 'bold' }} className="gradient-text">
        DSA MENTOR
      </Typography>
      <List>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton 
              onClick={() => handleNavigate(item.path)}
              selected={location.pathname === item.path}
              sx={{
                '&.Mui-selected': { bgcolor: 'rgba(139, 92, 246, 0.15)', borderLeft: '4px solid #8b5cf6' },
                color: location.pathname === item.path ? '#a78bfa' : 'white',
              }}
            >
              <Box sx={{ mr: 2, display: 'flex', alignItems: 'center' }}>{item.icon}</Box>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Box>
  );

  return (
    <AppBar position="sticky" sx={{ 
      background: 'rgba(11, 7, 22, 0.7)', 
      backdropFilter: 'blur(20px)',
      boxShadow: 'none',
      borderBottom: '1px solid rgba(255,255,255,0.06)'
    }}>
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <Code style={{ color: '#8b5cf6', marginRight: '8px' }} size={28} />
          <Typography
            variant="h6"
            noWrap
            onClick={() => navigate('/')}
            sx={{
              mr: 4,
              display: { xs: 'none', md: 'flex' },
              fontWeight: 800,
              letterSpacing: '.1rem',
              cursor: 'pointer',
            }}
            className="gradient-text"
          >
            DSA Mentor
          </Typography>

          {/* Mobile menu toggle */}
          <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
            <IconButton
              size="large"
              aria-label="open drawer"
              onClick={() => setMobileOpen(true)}
              color="inherit"
            >
              <MenuIcon />
            </IconButton>
            <Drawer
              anchor="left"
              open={mobileOpen}
              onClose={() => setMobileOpen(false)}
              ModalProps={{ keepMounted: true }}
            >
              {drawer}
            </Drawer>
          </Box>

          <Code style={{ color: '#8b5cf6', marginRight: '8px', display: isMobile ? 'block' : 'none' }} size={24} />
          <Typography
            variant="h5"
            noWrap
            onClick={() => navigate('/')}
            sx={{
              flexGrow: 1,
              display: { xs: 'flex', md: 'none' },
              fontWeight: 800,
              letterSpacing: '.1rem',
              cursor: 'pointer',
            }}
            className="gradient-text"
          >
            DSA Mentor
          </Typography>

          {/* Desktop Links */}
          <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' }, gap: 1 }}>
            {menuItems.map((item) => (
              <Button
                key={item.text}
                onClick={() => navigate(item.path)}
                sx={{
                  my: 2,
                  color: location.pathname === item.path ? '#a78bfa' : '#94a3b8',
                  display: 'flex',
                  alignItems: 'center',
                  gap: 1,
                  fontSize: '15px',
                  '&:hover': { color: 'white', backgroundColor: 'rgba(255,255,255,0.03)' },
                }}
              >
                {item.icon}
                {item.text}
              </Button>
            ))}
          </Box>

          {/* User Menu */}
          <Box sx={{ flexGrow: 0, display: 'flex', alignItems: 'center', gap: 2 }}>
            <Typography variant="body2" sx={{ display: { xs: 'none', sm: 'block' }, color: '#94a3b8' }}>
              Welcome, <span style={{ color: 'white', fontWeight: 600 }}>{user?.name}</span>
            </Typography>
            <Tooltip title="Open Profile Options">
              <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                <Avatar 
                  sx={{ bgcolor: '#8b5cf6', width: 40, height: 40 }}
                  alt={user?.name}
                >
                  {user?.name?.charAt(0).toUpperCase()}
                </Avatar>
              </IconButton>
            </Tooltip>
            <Menu
              sx={{ mt: '45px', '& .MuiPaper-root': { bgcolor: '#120c24', border: '1px solid rgba(255,255,255,0.08)' } }}
              id="menu-appbar"
              anchorEl={anchorElUser}
              anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
              keepMounted
              transformOrigin={{ vertical: 'top', horizontal: 'right' }}
              open={Boolean(anchorElUser)}
              onClose={handleCloseUserMenu}
            >
              <MenuItem onClick={() => { handleCloseUserMenu(); navigate('/profile'); }}>
                <User size={16} style={{ marginRight: '8px' }} />
                <Typography textAlign="center">My Profile</Typography>
              </MenuItem>
              <MenuItem onClick={handleLogout}>
                <LogOut size={16} style={{ marginRight: '8px', color: '#ef4444' }} />
                <Typography textAlign="center" sx={{ color: '#ef4444' }}>Logout</Typography>
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Navbar;
