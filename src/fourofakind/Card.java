package fourofakind;

import javafx.scene.image.Image;

/**
 * The Card class is representative of a Card object having:
 *      -a value
 *      -a suit
 *      -an image associated with the card
 * 
 * @author William Hess
 */

public class Card {
    private Image cardImage;
    private int value;   // (1-13) 1:ace, 11:jack, 12:queen, 13:king
    private String suit; // s for spades, d for diamonds, h for hearts, c for clubs

    public Card(String imageLocation, int value, String suit) {
        cardImage = new Image(imageLocation, 100, 0, true, true);
        this.value = value;
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public Image getCardImage() {
        return cardImage;
    }

    @Override
    public String toString() {
        return value + suit;
    }
}
