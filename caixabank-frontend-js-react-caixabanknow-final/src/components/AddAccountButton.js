import React from 'react';
import { Box, Button, Typography } from '@mui/material';
import caixabankIcon from '../assets/caixabank-icon-blue.png';

const AddAccountButton = ({ onDialogOpen }) => (
    <Box display="flex" alignItems="center" sx={{ mb: 4 }}>
        <img
            src={caixabankIcon}
            alt="CaixaBank"
            style={{ height: '40px', marginRight: '10px' }}
        />
        <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h4" component="div">
                Accounts
            </Typography>
            <Typography variant="subtitle1" color="textSecondary">
                Manage your bank accounts, including adding and deleting accounts.
            </Typography>
        </Box>
        <Button
            variant="contained"
            color="primary"
            className="button-custom"
            onClick={onDialogOpen}
            data-testid="add-account-button"
        >
            Add Account
        </Button>
    </Box>
);

export default AddAccountButton;
