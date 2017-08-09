package fourofakind;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

/**
 * The DiscardPile class is a "Stack" of Card objects.
 * 
 * @author William Hess
 */

public class DiscardPile extends ArrayDeque<Card> implements Deque<Card> {
    private PileOfCardsPane discardPane; // give access to the discardPane

    public DiscardPile(PileOfCardsPane discardPane) {
        this.discardPane = discardPane;
    }
    
    public void discard(Card card) {
        this.push(card);
        Game.logger.log(Level.WARNING, (this.toString() + " after discard\n"));
    }
    
    public Card pickUpCard() {
        return this.pop();
    }
    
    /**
     * The getTopCard() method returns, but does not remove, the top card in the
     * discard pile.
     * @return 
     */
    
    public Card getTopCard() {
        return this.peek();
    }

    public PileOfCardsPane getDiscardPane() {
        return discardPane;
    }
    
    @Override
    public String toString() {
        return "Discard Pile Size: " + this.size();
    }
}
