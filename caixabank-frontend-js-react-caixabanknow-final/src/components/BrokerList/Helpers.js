import React from 'react';
import { Typography, CircularProgress, IconButton, ListItem as MuiListItem, ListItemText } from '@mui/material';
import BusinessCenterIcon from '@mui/icons-material/BusinessCenter';
import VisibilityIcon from '@mui/icons-material/Visibility';

export const Header = ({ title }) => (
    <Typography variant="h6" sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <BusinessCenterIcon sx={{ mr: 1, color: '#007eae' }} />
        {title}
    </Typography>
);

// Use data-testid="loading-spinner"
export const Loading = () => <CircularProgress data-testid="loading-spinner" />;

// Use data-testid="error-message"
export const ErrorMessage = ({ message }) => (
    <Typography color="error" data-testid="error-message">
        {message}
    </Typography>
);

// Use data-testid="broker-item
export const ListItem = ({ title, subtitle, onClick }) => (
    <MuiListItem
        button
        onClick={onClick}
        data-testid="broker-item"
        sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
    >
        <ListItemText primary={title} secondary={subtitle} />
        <VisibilityIcon sx={{ color: '#007eae' }} />
    </MuiListItem>
);
