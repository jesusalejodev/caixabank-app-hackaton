import React from 'react';
import { Typography, Box, Button } from '@mui/material';

export const Header = ({ title, subtitle, onAddCard }) => (
    <Box display="flex" alignItems="center" sx={{ mb: 4 }}>
        {/* Use data-testid="caixabank-icon" */}

        <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h4" component="div" data-testid="cards-title">
                {title}
            </Typography>
            <Typography variant="subtitle1" color="textSecondary" data-testid="cards-subtitle">
                {subtitle}
            </Typography>
        </Box>
        <AddCardButton onClick={onAddCard} />
    </Box>
);

// Use data-testid="add-card-button"
export const AddCardButton = ({ onClick }) => (
    <Button
        variant="contained"
        color="primary"
        onClick={onClick}
        data-testid="add-card-button"
    >
        Add Card
    </Button>
);
