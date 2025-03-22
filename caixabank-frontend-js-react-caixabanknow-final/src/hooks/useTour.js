import { useCallback, useEffect, useMemo, useState } from "react";
import Joyride, { STATUS } from "react-joyride";
import { useTheme } from "@mui/material/styles";

export default function useTour(steps, localStorageKey) {
    const theme = useTheme();
    const [run, setRun] = useState(false);

    // Check localStorage on first load to auto-start if necessary
    useEffect(() => {
        if (!localStorageKey) {
            setRun(true);
            return;
        }
        const tourViewed = window.localStorage.getItem(localStorageKey);
        if (!tourViewed) {
            window.localStorage.setItem(localStorageKey, "1");
            setRun(true); // First time visit = Start tour
        }
    }, [localStorageKey]);

    const handleJoyrideCallback = useCallback((data) => {
        const { status } = data;
        if ([STATUS.FINISHED, STATUS.SKIPPED].includes(status)) {
            setRun(false); // Tour ends
        }
    }, []);

    const joyrideStyles = useMemo(() => ({
        options: {
            zIndex: 10000,
            primaryColor: theme.palette.primary.main,
            backgroundColor: theme.palette.background.paper,
            textColor: theme.palette.text.primary,
            overlayColor: "rgba(0, 0, 0, 0.5)"
        }
    }), [theme]);

    const tour = useMemo(() => (
        <Joyride
            callback={handleJoyrideCallback}
            continuous
            run={run}
            scrollToFirstStep
            showProgress
            showSkipButton
            steps={steps}
            styles={joyrideStyles}
        />
    ), [steps, handleJoyrideCallback, run, joyrideStyles]);

    return { tour, setRun }; // Expose setRun for manual triggering
}
