import React, {useEffect} from 'react';
import { Paper, List } from '@mui/material';
import { Header, Loading, ErrorMessage, ListItem } from './Helpers';
import useFetch from "../../hooks/useFetch";


const BrokerList = ({ onSelectBroker }) => {


    const { data, loading, error } = useFetch(
        '/api/v1/web/fn-a089d91a-d109-4f83-b366-fa7151812c8d/default/BrokerList'
    );

    useEffect(() => {
        console.log("Fetched data:", data); // ✅ Log the fetched data
    }, [data]);

    return (
        <Paper sx={{ p: 2 }}>
            <Header title="Broker List" aria-labelledby="broker-list-header"/>

            {/* loading state */}
            {loading && <Loading data-testid="loading-spinner" />}

            {/* error message if API request fails */}
            {error && <ErrorMessage message={error} data-testid="error-message" />}

            {/* render broker list when data is available */}
            {data && (
                <List aria-label="List of Brokers">
                    {data.map((broker) => (
                        <ListItem
                            key={broker.id}
                            title={broker.nombre}
                            subtitle={broker.pais}
                            onClick={() => onSelectBroker(broker.id)}
                            tabIndex={0} // Enables keyboard navigation
                            role="button"
                            aria-pressed="false"
                            data-testid={`broker-item-${broker.id}`}
                        />
                    ))}
                </List>
            )}

        </Paper>
    );
};

export default BrokerList;
