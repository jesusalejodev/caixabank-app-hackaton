import React from 'react';
import Joyride from 'react-joyride';
import { useTheme } from '@mui/material/styles';
import { useTourContext } from '../contexts/TourContext';

const steps = [
    {
        target: "#dashboard",
        content: "Welcome to CaixaBankNow! This is your dashboard where you can see your overall finances at a glance.",
    },
    {
        target: "#accounts",
        content: "In the Accounts section, you can view and manage all your bank accounts.",
    },
    {
        target: "#movements",
        content: "Here, you can review all recent account movements and transactions.",
    },
    {
        target: "#transfers",
        content: "The Transfers section lets you easily send money between accounts.",
    },
    {
        target: "#brokers",
        content: "In Brokers, you can manage your investment portfolios and brokers.",
    },
    {
        target: "#deposits",
        content: "The Deposits section allows you to create and monitor savings deposits.",
    },
    {
        target: "#cards",
        content: "Manage all your credit and debit cards here, including adding new ones and viewing existing ones.",
    },
    {
        target: "#navbar-tour-button",
        content: "At any time, you can restart this tour using this Start Tour button in the navigation bar.",
    },
    {
        target: "#dashboard",
        content: "That concludes the tour! You're now ready to explore CaixaBankNow on your own.",
    }
];


const AppTour = () => {
    const { run, setRun } = useTourContext();
    const theme = useTheme();

    const handleJoyrideCallback = (data) => {
        const { status } = data;
        if (["finished", "skipped"].includes(status)) {
            setRun(false);  // End tour after finishing or skipping
        }
    };

    return (
        <Joyride
            callback={handleJoyrideCallback}
            continuous
            run={run}
            scrollToFirstStep
            showProgress
            showSkipButton
            steps={steps}
            styles={{
                options: {
                    zIndex: 10000,
                    primaryColor: theme.palette.primary.main,
                    backgroundColor: theme.palette.background.paper,
                    textColor: theme.palette.text.primary,
                    overlayColor: 'rgba(0, 0, 0, 0.5)',
                }
            }}
        />
    );
};

export default AppTour;
