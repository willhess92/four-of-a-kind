package fourofakind;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

/**
 * The Deck class is a "Queue" of Card objects.
 * 
 * @author William Hess
 */

public class Deck extends LinkedList<Card> implements Queue<Card> {
    private DiscardPile discardPile; // give access to the discardPile
    private PileOfCardsPane discardPane;

    public Deck(DiscardPile discardPile) {
        // Give the deck access to the discard pile
        this.discardPile = discardPile;
        this.discardPane = discardPile.getDiscardPane();
        // Initialize the deck
        // s for spades, d for diamonds, h for hearts, c for clubs
        String[] suits = {"s", "d", "h", "c"};
        for (String suit : suits) {
            for (int value = 1; value <= 13; value++) {
                Card card = new Card("cards/" + value + suit + ".png", value, suit);
                this.offer(card);
            }
        }
        // Shuffle deck
        shuffle();
    }

    /**
     * The resetDeck() method moves all cards from discard pile to the deck and
     * shuffles the deck.
     */
    
    private void resetDeck() {
        System.out.println("Shuffling deck...");
        // move all cards in discard pile to deck
        while(discardPile != null && !discardPile.isEmpty()) { // while the discard pile is not null and not empty
            this.offer(discardPile.pickUpCard());
        }
        // Shuffle deck
        shuffle();
        // reset discardPane view
        discardPane.setTopCardToCardBack();
    }
    
    public void shuffle() {
        Collections.shuffle(this);
    }
    
    /**
     * The pickUp() method uses the poll() method to return a card object to the 
     * user.  When the deck only has one card left, this card is returned to the 
     * user, and the deck is then reset (move all cards from discard pile to deck,
     * reshuffle). If for some reason the deck fails to reset itself when low, 
     * a DeckIsEmptyException is thrown.
     * 
     * @return Card to pickUp
     * @throws DeckIsEmptyException 
     */
    
    public Card pickUp() throws DeckIsEmptyException {
        Game.logger.log(Level.WARNING, (this.toString() + " before pickup\n"));
        if (this.size() == 1) { // if the deck only has one card left, return this card but reset the deck
            Card lastCard = this.poll(); // remove lastCard deck
            resetDeck(); // reset the deck
            return lastCard;
        } else if (this.size() > 1) {
            return this.poll();
        } else {
            throw new DeckIsEmptyException();
        }
        
    }

    @Override
    public String toString() {
        return "Remaining Deck Cards: " + this.size();
    }
}
