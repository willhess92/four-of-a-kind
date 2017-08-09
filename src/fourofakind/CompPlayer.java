package fourofakind;

import java.util.HashMap;

/**
 * The CompPlayer is similar to that of a regular Player, with the exception of 
 * basic AI to win the game.
 * 
 * @author William Hess
 */

public class CompPlayer extends Player {
    private HashMap<Integer, Integer> cardFrequencies = new HashMap<>(); // <Card Value, Frequency of Card>
    // Since the computer must carry out its own moves:
    // give access to the deckPane, discardPane, and discardPile
    private PileOfCardsPane deckPane;
    private PileOfCardsPane discardPane;
    private DiscardPile discardPile;


    public CompPlayer(PileOfCardsPane deckPane, PileOfCardsPane discardPane, DiscardPile discardPile) {
        super(true);
        this.deckPane = deckPane;
        this.discardPane = discardPane;
        this.discardPile = discardPile;
    }

    /**
     * The initializeHand() method of the CompPlayer does everything it would for 
     * a regular Player, in addition to storing an initial frequency of the card
     * values in hand.
     * 
     * @param cards 
     */
    
    @Override
    public void initializeHand(Card[] cards) {
        super.initializeHand(cards);
        for (Card card :
                cards) {
            if (cardFrequencies.containsKey(card.getValue())) { // update old value for that kind of card
                int oldValue = cardFrequencies.get(card.getValue());
                cardFrequencies.replace(card.getValue(), ++oldValue);
            } else { // create a new frequency pair
                cardFrequencies.put(card.getValue(), 1);
            }
        }
    }

    public HashMap<Integer, Integer> getCardFrequencies() {
        return cardFrequencies;
    }

    /**
     * 
     * @return the least frequent card value currently in hand
     */
    
    public int minFrequency() {
        int min = (Integer) cardFrequencies.values().toArray()[0]; // set some frequency to min from hash map values
        for (Integer frequency :
                cardFrequencies.values()) {
            if (frequency < min) { // if the frequency is less than the current min, update min
                min = frequency;
            }
        }
        return min;
    }

    /**
     * 
     * @return card value with frequency of three in hand, if exists
     */
    
    public int cardWithFreqOf3() {
        for (Card card : getHand()) {
            if (cardFrequencies.get(card.getValue()) == 3) {
                return card.getValue();
            }
        }
        return -1;
    }
    
    /**
     * The play() method for the CompPlayer contains the basic AI for the 
     * CompPlayer to win the game.  The computer must first pick a card from 
     * either the deck or discard pile.  Then, the computer must discard the card 
     * just drawn or discard a card already in the computer's hand to then be 
     * replaced by the card just drawn.
     * 
     * @param action "pickup" or "discard"
     */
    
    public void play(String action) {
        // Basic computer AI
        int threeOfAKind = cardWithFreqOf3(); // special case
        switch (action) {
            case "pickup": // computer decides where to pickup card from
                // if the card at the top of the discard pile has a value already in the computers hand
                if (cardFrequencies.containsKey(discardPile.getTopCard().getValue())) {
                    if (threeOfAKind != -1) { // some card has a frequency of three
                        // check if the card in the discard pile is of frequency 3 in hand
                        if (discardPile.getTopCard().getValue() != threeOfAKind) { // if not
                            deckPane.getPickUpBtn().fire(); // pickup from deck
                            return;
                        }
                    }
                    discardPane.getPickUpBtn().fire(); // pickup from discardPile
                } else {
                    deckPane.getPickUpBtn().fire(); // pickup from deck
                }
                break;
            case "discard": // computer decides what to discard
                // If some card has frequency of 3 , and the card in the tempSlot doesn't have the value of this card
                // get rid of the tempSlot card
                if (threeOfAKind != -1) { // some card has a frequency of three
                    // check if the card in the tempSlot doesn't have the same value as this card
                    if (getTempCard().getValue() != threeOfAKind) {
                        getHandPane().getTempSlot().getDiscardBtn().fire(); // discard card just picked up
                        return;
                    }
                }
                
                // if the card picked up has a value that is already in the computer's hand
                if (cardFrequencies.containsKey(getTempCard().getValue())) {
                    // find a card with the least frequency to discard, and whereby keeping the tempCard
                    // find min frequency
                    int frequencyToDiscard = minFrequency();
                    for (CardSlot slot :
                            getHandPane().getHandSlots()) {
                        // discard some card that has the lowest frequency, and isn't of the value that the computer needs
                        if (cardFrequencies.get(slot.getCard().getValue()) == frequencyToDiscard && slot.getCard().getValue() != getTempCard().getValue()) {
                            slot.getDiscardBtn().fire();
                            break;
                        }
                    }
                    
                } else {
                    getHandPane().getTempSlot().getDiscardBtn().fire(); // discard card just picked up
                }
                break;
        }
    }

    @Override
    public void addToHand(Card card) {
        super.addToHand(card);
        // update the old frequency of that cards value
        int oldValue = cardFrequencies.get(card.getValue());
        cardFrequencies.replace(card.getValue(), ++oldValue);
    }

    @Override
    public void removeCard(Card card) {
        super.removeCard(card);
        // if the card is removed, update the frequency of that cards value in the computers hand
        int oldValue = cardFrequencies.get(card.getValue());
        if (oldValue == 1) {
            cardFrequencies.remove(card.getValue()); // ie no cards of this value are in the hand anymore
        } else {
            cardFrequencies.replace(card.getValue(), --oldValue);
        }
    }

    @Override
    public String toString() {
        return "Computer Player: " + super.getHand() + cardFrequencies.toString();
    }

}
