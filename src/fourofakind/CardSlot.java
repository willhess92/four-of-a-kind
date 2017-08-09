package fourofakind;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;



/**
 * The CardSlot class is a visual representation of some card being shown to the
 * user in a GUI.  If the CardSlot doesn't have a card set, then the slot is considered
 * empty, and a dashed rectangle with reside in its place until set.
 * 
 * @author William Hess
 */

public class CardSlot extends StackPane {
    private Rectangle placeHolder = new Rectangle(98, 143);
    private Button discardBtn = new Button("Discard");
    private Button keepBtn = new Button("Keep");
    private VBox btnBox = new VBox(8);
    private Card card;
    private ImageView cardView = new ImageView();
    private boolean empty;
    
    public CardSlot() {
        btnBox.getChildren().addAll(keepBtn, discardBtn);
        btnBox.setAlignment(Pos.CENTER);
        
        setPlaceHolderProperties();
        
        this.getChildren().addAll(placeHolder, cardView, btnBox);
        
        cardView.setVisible(false);
        hideKeepBtn();
        hideDiscardBtn();
    }

    /**
     * The setPlaceHolderProperties() method modifies the placeHolder rectangle
     * to be dashed with a stoke color of black, and a transparent fill.
     */
    
    private void setPlaceHolderProperties() {
        placeHolder.setStrokeWidth(2);
        placeHolder.setFill(Paint.valueOf("transparent"));
        placeHolder.setStroke(Paint.valueOf("black"));
        placeHolder.setArcHeight(5);
        placeHolder.setArcWidth(5);
        placeHolder.getStrokeDashArray().add(10d);
    }

    /**
     * The resetSlot() method essentially sets the slot to only show the placeHolder
     * rectangle.
     */
    
    public void resetSlot() {
        cardView.setVisible(false);
        hideDiscardBtn();
        hideKeepBtn();
        empty = true;
    }

    public Card getCard() {
        return card;
    }

    /**
     * The setCard() method takes a card, sets the slots card to be this card,
     * and shows an imageView of the card's image in the CardSlot.
     * 
     * @param card 
     */
    
    public void setCard(Card card) {
        this.card = card;
        cardView.setImage(card.getCardImage());
        cardView.setVisible(true);
        empty = false;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = !empty;
    }

    public Button getKeepBtn() {
        return keepBtn;
    }

    public Button getDiscardBtn() {
        return discardBtn;
    }

    public void showDiscardBtn() {
        discardBtn.setVisible(true);
    }

    public void hideDiscardBtn() {
        discardBtn.setVisible(false);
    }

    public void showKeepBtn() {
        keepBtn.setVisible(true);
    }

    public void hideKeepBtn() {
        keepBtn.setVisible(false);
    }

}
