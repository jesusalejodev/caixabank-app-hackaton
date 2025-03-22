import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import AccountDetails from '../../src/components/AccountDetails';
import {accountsStore, GlobalStateProvider} from '../contexts/GlobalState';

const mockAccounts = [
    { id: 1, name: "Checking Account", balance: 1000, accountNumber: "1234567890" },
    { id: 2, name: "Savings Account", balance: 5000, accountNumber: "0987654321" }
];

beforeEach(() => {
    accountsStore.setKey('accounts', mockAccounts); // Set mock data before each test
});

describe('AccountDetails Component', () => {
    test('renders account details when given a valid accountId', () => {
        render(<AccountDetails accountId={1} />);

        //Check if account details are displayed
        expect(screen.getByText("Checking Account")).toBeInTheDocument();
        expect(screen.getByText("Balance: 1000")).toBeInTheDocument();
        expect(screen.getByText("Account Number: 1234567890")).toBeInTheDocument();
    });

    test('shows "Account not found" when given an invalid accountId', () => {
        render(<AccountDetails accountId={99} />);

        //Ensure "Account not found" is displayed
        expect(screen.getByText("Account not found")).toBeInTheDocument();
    });
});