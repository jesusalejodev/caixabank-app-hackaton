import React, { createContext, useContext, useState, useEffect } from 'react';

const TourContext = createContext();

export const TourProvider = ({ children }) => {
    const [run, setRun] = useState(false);

    useEffect(() => {
        const tourViewed = localStorage.getItem('caixabank-tour-viewed');
        if (!tourViewed) {
            localStorage.setItem('caixabank-tour-viewed', '1');
            setRun(true);  // Start tour automatically on first visit
        }
    }, []);

    return (
        <TourContext.Provider value={{ run, setRun }}>
            {children}
        </TourContext.Provider>
    );
};

export const useTourContext = () => useContext(TourContext);
