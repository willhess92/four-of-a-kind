package fourofakind;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/**
 * The HandPane class is used for displaying the contents of a player's hand
 * in a GUI.
 * 
 * @author William Hess
 */

public class HandPane extends VBox {
    private CardSlot tempSlot = new CardSlot();
    private HBox handSlots = new HBox(20);
    private int emptyHandSlot = -1; // location of emptyHandSlot in handSlots
                                    // default: -1 (no handSlot is empty)
    
    public HandPane(boolean rotate) {
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
        handSlots.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10, 10, 10, 10));
        // add four CardSlots to handSlots pane for the four cards in a players hand
        for (int i = 0; i < 4; i++) {
            handSlots.getChildren().add(new CardSlot());
        }
                   
        if (rotate) { // true: for computer player, handSlots above tempCardSlot
            this.getChildren().addAll(handSlots, tempSlot);
        } else { // false: for human player, tempCardSlot above handSlots
           this.getChildren().addAll(tempSlot, handSlots); 
        }
    }

    public void setTempSlot(Card card) {
        tempSlot.setCard(card);
        tempSlot.showKeepBtn(); // allow player to keep card
        tempSlot.showDiscardBtn(); // allow player to discard card
    }

    public CardSlot getTempSlot() {
        return tempSlot;
    }
    
    public CardSlot[] getHandSlots() {
        CardSlot[] slots = new CardSlot[4];
        int i = 0;
        for (Node slot : handSlots.getChildren()) {
            slots[i++] = ((CardSlot) slot);
        }
        return slots;
    }

    /**
     * The setHandSlot() method sets a specified card to a specific CardSlot, 
     * given by the slotNum index.
     * 
     * @param card
     * @param slotNum // index of CardSlot in handSlots
     */
    
    public void setHandSlot(Card card, int slotNum) {
        ((CardSlot) handSlots.getChildren().get(slotNum)).setCard(card);
    }

    /**
     * The findEmptySlot() loops through the card slots in the player's handSlots
     * and tries to return the index of at least 1 CardSlot that is empty. If an 
     * empty slot cannot be found, the method returns -1;
     * 
     * @return 
     */
    
    public int findEmptySlot() {
        for (int i = 0; i < 4; i++) {
            CardSlot slot = (CardSlot) handSlots.getChildren().get(i);
            if (slot.isEmpty()) { // CardSlot doesn't have a card in it
                emptyHandSlot = i;
                return emptyHandSlot;
            }
        }
        emptyHandSlot = -1; // empty slot was not found
        return emptyHandSlot;
    }

    public int getEmptyHandSlot() {
        return emptyHandSlot;
    }

    /**
     * The showHandSlotDiscardBtns() makes all the discard buttons in the CardSlots
     * of the HandPane visible to the player.
     */
    
    public void showHandSlotDiscardBtns() {
        for (Node slot : handSlots.getChildren()) {
            ((CardSlot)slot).showDiscardBtn();
        }
    }

    /**
     * The hideHandSlotDiscardBtns() makes all the discard buttons in the CardSlots
     * of the HandPane not visible to the player.
     */
    
    public void hideHandSlotDiscardBtns() {
        for (Node slot : handSlots.getChildren()) {
            ((CardSlot)slot).hideDiscardBtn();
        }
    }
}
