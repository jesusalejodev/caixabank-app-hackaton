import React, { useState } from 'react';
import {Container, Grid, Box, Typography, Button} from '@mui/material';
import { Header, AddCardButton } from './Helpers';
import {useStore} from "@nanostores/react";
import {accountsStore} from "../../contexts/GlobalState";
import CardList from "../../components/CardList";
import AddCardForm from "../../components/AddCardForm";
import caixabankIcon from "../../assets/caixabank-icon-blue.png";

const Cards = () => {

    const [open, setOpen] = useState(false);

    const { cards } = useStore(accountsStore);

    const handleOpen = () => {
        setOpen(true);
    };

    const handleDeleteCard = (cardId) => {
        const updatedCards = cards.filter((card) => card.id !== cardId);
        accountsStore.setKey('cards', updatedCards);
    };


    return (
        <Container sx={{mt: 10, mb: 4}}>
            <Box display="flex" alignItems="center" sx={{ mb: 4 }}>
                <img
                    src={caixabankIcon}
                    alt="CaixaBank"
                    style={{ height: '40px', marginRight: '10px' }}
                />
                <Box sx={{ flexGrow: 1 }}>
                    <Typography variant="h4" component="div">
                        Cards
                    </Typography>
                    <Typography variant="subtitle1" color="textSecondary">
                        Manage your cards, including adding and deleting cards.
                    </Typography>
                </Box>
                <Button
                    variant="contained"
                    color="primary"
                    className="button-custom"
                    onClick={handleOpen}
                    data-testid="add-account-button"
                >
                    Add Card
                </Button>
            </Box>

            <Grid container spacing={2}>
                <Grid item xs={12}>
                    {/* Use data-testid="card-list" */}
                    {cards.length > 0 ? (
                        <CardList cards={cards} onDeleteCard={handleDeleteCard}/>
                    ) : (
                        <p data-testid="no-cards-message">No cards available</p>
                    )}
                </Grid>
            </Grid>

            <AddCardForm open={open} setOpen={setOpen}/>
        </Container>
    );
};

export default Cards;