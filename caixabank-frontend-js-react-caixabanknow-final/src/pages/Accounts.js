import React, { useState } from 'react';
import { Container, Grid, Box } from '@mui/material';
import AddAccountButton from '../components/AddAccountButton';
import AccountCard from '../components/AccountCard';
import AddAccountDialog from '../components/AddAccountDialog';
import DeleteAccountDialog from '../components/DeleteAccountDialog';
import AccountMenu from '../components/AccountMenu';
import SnackbarNotification from '../components/SnackbarNotification';
import { useStore } from '@nanostores/react';
import { accountsStore, addAccount, deleteAccount } from '../contexts/GlobalState';
import '../styles/Buttons.css';

const Accounts = () => {

    const { accounts } = useStore(accountsStore); // access to accounts from Store

    const [anchorEl, setAnchorEl] = useState(null); // state for menu position
    const [selectedAccount, setSelectedAccount] = useState(null); // selected account for the menu

    const [open, setOpen] = useState(false); // track dialog visibility

    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarSeverity, setSnackbarSeverity] = useState('success'); // 'success' | 'error' | 'warning' | 'info'



    const handleMenuOpen = (event, accountId) => {
        setAnchorEl(event.currentTarget); // save reference of clicked element
        setSelectedAccount(accountId); // save selected account ID
    };

    const handleMenuClose = () => {
        setAnchorEl(null); // delete reference
        setSelectedAccount(null); // delete selected account
    };

    const handleDialogOpen = () => {
        setOpen(true);
    };

    const handleDialogClose = () => {
        setOpen(false);
    };

    const handleAddAccount = (accountData) => {
        const currentAccounts = accountsStore.get().accounts;

        //create new account object with unique ID
        const newAccount = {
            id: currentAccounts.length ? Math.max(...currentAccounts.map(acc => acc.id))+ 1 : 1, //auto incremental ID
            name: accountData.accountName,
            balance: Number(accountData.accountBalance) || 0,
            accountNumber: Math.floor(1000000000 + Math.random() * 9000000000).toString(), // Generate random 10-digit number
            type: accountData.type,
            currency: accountData.currency,
        };

        addAccount(newAccount); //update global state
        setOpen(false);

        setSnackbarMessage(`Account "${newAccount.name}" added successfully!`);
        setSnackbarSeverity('success');
        setSnackbarOpen(true);
    };

    const handleDeleteAccount = () => {
        if (selectedAccount) {
            deleteAccount(selectedAccount); //delete and reset selected account
            setDeleteDialogOpen(false);
            setSelectedAccount(null);
        }

        setSnackbarMessage('Account deleted successfully!');
        setSnackbarSeverity('info');
        setSnackbarOpen(true);
    };


    const handleDeleteDialogOpen = () => {
        setDeleteDialogOpen(true);
    };

    const handleDeleteDialogClose = () => {
        setDeleteDialogOpen(false);
    };

    const handleSnackbarClose = (event, reason) => {
        if (reason === 'clickaway') return; //no closing when clicking outside

        setSnackbarOpen(false);
    };



    return (
        <Container sx={{ mt: 10, mb: 4 }}>
            <AddAccountButton onDialogOpen={handleDialogOpen} />
            <Grid container spacing={2}>
                {accounts.map(account => (
                    <Grid item xs={12} sm={6} md={4} key={account.id}>
                        <AccountCard account={account} onMenuOpen={handleMenuOpen} />
                    </Grid>
                ))}
            </Grid>
            <AccountMenu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
                onDeleteOpen={handleDeleteDialogOpen}
            />
            <AddAccountDialog
                open={open}
                setOpen={setOpen}
                onClose={handleDialogClose}
                onAddAccount={handleAddAccount}
            />

            <DeleteAccountDialog
                open={deleteDialogOpen}
                onClose={handleDeleteDialogClose}
                onDelete={handleDeleteAccount}
            />

            <SnackbarNotification
                open={snackbarOpen}
                onClose={handleSnackbarClose}
                severity={snackbarSeverity}
                message={snackbarMessage}
            />
        </Container>
    );
};

export default Accounts;
