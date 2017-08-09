package fourofakind;

/**
 * This DeckIsEmptyException is thrown when the Deck is empty and failed to 
 * reshuffle when the last card was picked up.
 * 
 * EDIT: This exception may no longer be needed with the current implementation 
 *       of the pickUp() method in the Deck class.
 * 
 * @author William Hess
 */

public class DeckIsEmptyException extends Exception {
    public DeckIsEmptyException() {
        super("Deck is empty and failed to reshuffle!");
    }
}
