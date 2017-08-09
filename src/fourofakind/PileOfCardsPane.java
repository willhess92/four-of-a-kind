package fourofakind;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * The PileOfCardsPane is a visual representation of some object that is essentially
 * a pile of cards (ie the Deck and Discard Pile).
 * 
 * @author William Hess
 */

public class PileOfCardsPane extends StackPane {
    private Image pileOfCardsImage = new Image("cards/pileofback.png", 110, 0, true, true);
    private Image cardBack = new Image("cards/back2.png", 100, 0, true, true);
    private ImageView topCardView = new ImageView(pileOfCardsImage); // default imageview is a pile of card backs
    private Button pickUpBtn = new Button("Pick Up");

    public PileOfCardsPane(boolean allowPickUp, boolean isDeck) {
        if (!isDeck) { // if the object being represented is not the Deck (so the DiscardPile)
            topCardView.setImage(cardBack);
        }
        this.setPadding(new Insets(10, 10, 10, 10));
        this.getChildren().addAll(topCardView, pickUpBtn);
        pickUpBtn.setVisible(allowPickUp);
    }


    public Button getPickUpBtn() {
        return pickUpBtn;
    }

    public void showPickUpBtn() {
        pickUpBtn.setVisible(true);
    }

    public void hidePickUpBtn() {
        pickUpBtn.setVisible(false);
    }

    /**
     * The showTopCard() is intended for the discardPile.  This method takes a card
     * (card at the top of the discard pile), and set the image view of this pane
     * to the image of the card given.
     * 
     * @param card 
     */
    
    public void showTopCard(Card card) {
        topCardView.setImage(card.getCardImage());
    }

    public void setTopCardToCardBack() {
        topCardView.setImage(cardBack);
    }

}
