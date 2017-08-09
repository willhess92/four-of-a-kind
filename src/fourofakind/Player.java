package fourofakind;

import java.util.ArrayList;

/**
 * The Player class represents a player in the game of Four of a Kind with a hand
 * of four cards. The player can add/remove cards to/from their hand.
 * 
 * @author William Hess
 */

public class Player {
    // The order of the cards in the hand is not reflective of the order displayed in the gui
    private ArrayList<Card> hand = new ArrayList<>(4);
    private HandPane handPane;
    private Card tempCard;

    /**
     * @param rotateHandPane true: for computer player, handSlots above tempCardSlot
     *                       false: for human player, tempCardSlot above handSlots
     */
    
    public Player (boolean rotateHandPane) {
        handPane = new HandPane(rotateHandPane);
    }

    /**
     * The initializeHand() method takes an array of cards and adds them to the 
     * players hand.
     * 
     * @param cards 
     */
    
    public void initializeHand(Card[] cards) {
        for (Card card : cards) {
            handPane.setHandSlot(card, hand.size());
            hand.add(card);
        }
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * The addToHand() method takes a card to be added to the player's hand, adds
     * the card, and places the image of that card into an empty hand slot in the
     * handPane.
     * 
     * @param card 
     */
    
    public void addToHand(Card card) {
        hand.add(card);
        handPane.findEmptySlot();
        int slotNum = handPane.getEmptyHandSlot();
        handPane.setHandSlot(card, slotNum);
    }

    public HandPane getHandPane() {
        return handPane;
    }

    public Card getTempCard() {
        return tempCard;
    }

    public void setTempCard(Card tempCard) {
        this.tempCard = tempCard; // set tempCard
        handPane.setTempSlot(tempCard); // show image of tempCard in tempCardSlot
    }

    public void removeTempCard() {
        tempCard = null;
    }

    public void removeCard(Card card) {
        hand.remove(card);
    }

    @Override
    public String toString() {
        return "Player: " + hand;
    }

}
