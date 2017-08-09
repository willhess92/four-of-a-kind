package fourofakind;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CS1181L-C05
 * Instructor: Dr. Cheatham
 * Project3
 * 
 * This project implements a GUI for a simple game representation of the Four of 
 * a Kind hand in poker.  
 * 
 *      -The game consists of two players: a human player, and a computer player. 
 * 
 *      -The object of the game is to obtain four cards of the same value from 
 *       each suit in the player's hand.  
 * 
 *      -The first player to contain a hand with contents of the previous sentence 
 *       wins the game.
 * 
 * @author William Hess
 */

public class Game extends Application {

    /**
     * The isWinner() method takes a Player object and returns whether or not
     * this player given has a Four of a Kind in hand.
     * 
     * @param player
     * @return // true: the player won
     *         // false: the player doesn't have a Four of a Kind yet
     */
    
    public static boolean isWinner(Player player) {
        if (player instanceof CompPlayer) {// computer player
            // check if computer has four of a kind (ie check hashmap for value of 4)
            return ((CompPlayer)player).getCardFrequencies().containsValue(4);
        } else { // human player
            // check if human has four of a kind
            int cardValue = player.getHand().get(0).getValue();
            int cardFreq = 1;
            for (int i = 1; i < player.getHand().size(); i++) {
                // if each successive card in the player's hand has the same
                // card value as the first card, increment the frequency of
                // the selected card value
                if (player.getHand().get(i).getValue() == cardValue) {
                    cardFreq++;
                }
            }
            return cardFreq == 4;
        }
    }

    public static void main(String[] args){
        launch(args);
    }

    public static final Logger logger = Logger.getGlobal();
    private Player human;
    private CompPlayer comp;
    private ArrayList<FadeTransition> showCompCards = new ArrayList<>();
    private Image cardBackImage = new Image("cards/back2.png", 100, 145, false, true);
    private DiscardPile discardPile;
    private Deck deck;
    private CardSlot humanTempSlot;
    private CardSlot compTempSlot;
    private VBox vBox;
    private boolean humansTurn; // should be set to false after human discards
                                // should be set to true after computer discards

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.setLevel(Level.OFF);

        StackPane root = new StackPane();
        BorderPane borderPane = new BorderPane();
        root.getChildren().add(borderPane);
        Button startBtn = new Button("Start Game!");

        WinLosePane winLosePane = new WinLosePane();

        PileOfCardsPane deckPane = new PileOfCardsPane(false, true);
        PileOfCardsPane discardPane = new PileOfCardsPane(false, false);
        discardPile = new DiscardPile(discardPane);
        deck = new Deck(discardPile);

        human = new Player(false);
        humansTurn = true;
        comp = new CompPlayer(deckPane, discardPane, discardPile);

        humanTempSlot = human.getHandPane().getTempSlot();
        compTempSlot = comp.getHandPane().getTempSlot();

        vBox = new VBox(comp.getHandPane(), startBtn, human.getHandPane());
        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);
        borderPane.setLeft(deckPane);
        borderPane.setRight(discardPane);

        // Action for Start Button
        startBtn.setOnAction(e -> {
            // Animate the dealing of cards
            // Get the starting coordinates of the deckPane
            Bounds deckBounds = deckPane.localToScene(deckPane.getBoundsInLocal());
            NodePosition deckPosition = new NodePosition(deckBounds.getMaxX() - 55, deckBounds.getMaxY() / 2);

            // Get the coordinates of human and computer hand slots and store them
            NodePosition[] humanSlotPos = new NodePosition[4];
            NodePosition[] compSlotPos = new NodePosition[4];

            CardSlot[] humanHandSlots = human.getHandPane().getHandSlots();
            CardSlot[] compHandSlots = comp.getHandPane().getHandSlots();

            for (int i = 0; i < 4; i++) {
                // Store human hand Positions
                Bounds slotBounds = humanHandSlots[i].localToScene(humanHandSlots[i].getBoundsInLocal());
                humanSlotPos[i] = new NodePosition(slotBounds.getMaxX() - 50, slotBounds.getMaxY() - 72);
                // Store computer hand Positions
                slotBounds = compHandSlots[i].localToScene(compHandSlots[i].getBoundsInLocal());
                compSlotPos[i] = new NodePosition(slotBounds.getMaxX() - 50, slotBounds.getMaxY() - 72);
            }

            // Based on the deckPane's position and the hand slot positions, create a path transition with card backs
//            Animate computer hand
            final int duration = 1000;
            for (NodePosition slotPos :
                    compSlotPos) {
                Line path = new Line(deckPosition.getX(), deckPosition.getY(), slotPos.getX(), slotPos.getY());
                ImageView cardBack = new ImageView(cardBackImage);
                borderPane.getChildren().add(cardBack); // add the card to the borderPane pane
                PathTransition pt = new PathTransition(Duration.millis(duration), path, cardBack);

                // When the path transition is finished, initialize computer hand
                pt.setOnFinished(e1 -> {
                    // initialize human hand
                    try {
                        comp.initializeHand(new Card[]{deck.pickUp()});
                    } catch (DeckIsEmptyException ex) {
                        ex.printStackTrace();
                    }
                    FadeTransition ft = new FadeTransition(Duration.millis(duration), cardBack);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.0);
                    showCompCards.add(ft);
                    // In the rare case that the computer wins after drawing 4 cards
                    if (comp.getHand().size() == 4 && isWinner(comp)) {
                        showCompCards.get(0).setOnFinished(e2 -> {
                            winLosePane.setGameResult("computer");
                            root.getChildren().add(winLosePane);
                        });
                        // show computer's hand
                        for (FadeTransition showCards :
                                showCompCards) {
                            showCards.play();
                        }
                    }
                    // This line is for debugging purposes!
//                    borderPane.getChildren().remove(cardBack); // Show the cards in the computers hand slots
                });
                pt.play();
            }

            // Animate human hand
            for (NodePosition slotPos :
                    humanSlotPos) {
                Line path = new Line(deckPosition.getX(), deckPosition.getY(), slotPos.getX(), slotPos.getY());
                ImageView cardBack = new ImageView(cardBackImage);
                borderPane.getChildren().add(cardBack); // add the card to the borderPane pane
                PathTransition pt = new PathTransition(Duration.millis(duration), path, cardBack);

                // When the path transition is finished, initialize human hand, and fade out the card backs
                pt.setOnFinished(e1 -> {
                    // initialize human hand
                    try {
                        human.initializeHand(new Card[]{deck.pickUp()}); // since this will run in loop 4 times, only initialize
                                                                         // one card at a time
                    } catch (DeckIsEmptyException ex) {
                        ex.printStackTrace();
                    }

                    FadeTransition ft = new FadeTransition(Duration.millis(duration), cardBack);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.0);
                    // When fade out is finished, remove cardBack nodes, and allow pick up from deck
                    ft.setOnFinished(e2 -> {
                        // In the rare case that the human wins after drawing 4 cards
                        if (human.getHand().size() == 4 && isWinner(human)) {
                            winLosePane.setGameResult("human");
                            root.getChildren().add(winLosePane);
                        } else { // human did not win, proceed with game
                            // allow human to pickup from deck
                            deckPane.showPickUpBtn();
                            borderPane.getChildren().remove(cardBack);
                        }
                    });
                    ft.play();
                });
                pt.play();
            }

            // hide the startBtn
            startBtn.setVisible(false);
        });

        // Action for the Deck Pick Up Button
        deckPane.getPickUpBtn().setOnAction(e -> {
            if (humansTurn) { // human wishes to pickup from the deck
                // Obtain deckPane position
                Bounds slotBounds = deckPane.localToScene(deckPane.getBoundsInLocal());
                NodePosition deckPos = new NodePosition(slotBounds.getMaxX() - 50, slotBounds.getMaxY() / 2);
                // Obtain human temp slot position
                slotBounds = humanTempSlot.localToScene(humanTempSlot.getBoundsInLocal());
                NodePosition tempSlotPos = new NodePosition(slotBounds.getMaxX() / 2 + 70, slotBounds.getMaxY() - 72);

                int duration = 700; // in milliseconds
                Line path = new Line(deckPos.getX(), deckPos.getY(), tempSlotPos.getX(), tempSlotPos.getY());
                ImageView cardBack = new ImageView(cardBackImage);
                borderPane.getChildren().add(cardBack); // add the card to the borderPane pane
                PathTransition pt = new PathTransition(Duration.millis(duration), path, cardBack);

                // When the path transition is finished, pickup card from deck, fadeout cardBack
                pt.setOnFinished(e1 -> {
                    // set the temp card to the card being drawn from the deck
                    try {
                        human.setTempCard(deck.pickUp());
                    } catch (DeckIsEmptyException e2) {
                        System.out.println(e2.getMessage());
                    }
                    // fade out cardback to reveal the card just drawn
                    FadeTransition ft = new FadeTransition(Duration.millis(duration), cardBack);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.0);
                    // When fade out is finished, remove cardBack node from borderPane
                    ft.setOnFinished(e2 -> {
                        borderPane.getChildren().remove(cardBack);
                    });
                    ft.play();
                });
                pt.play();
                deckPane.hidePickUpBtn();
                discardPane.hidePickUpBtn();
            } else { // computer wishes to pickup from the deck

                // Obtain deckPane position
                Bounds slotBounds = deckPane.localToScene(deckPane.getBoundsInLocal());
                NodePosition deckPos = new NodePosition(slotBounds.getMaxX() - 50, slotBounds.getMaxY() / 2);
                // Obtain computer temp slot position
                slotBounds = compTempSlot.localToScene(compTempSlot.getBoundsInLocal());
                NodePosition tempSlotPos = new NodePosition(slotBounds.getMaxX() / 2 + 70, slotBounds.getMaxY() - 72);

                int duration = 700; // in milliseconds
                Line path = new Line(deckPos.getX(), deckPos.getY(), tempSlotPos.getX(), tempSlotPos.getY());
                ImageView cardBack = new ImageView(cardBackImage);
                borderPane.getChildren().add(cardBack); // add the card to the borderPane pane
                cardBack.setTranslateX(-200); // so that when the node is added, the card is not briefly flashed in the
                                              // upper left corner of the borderPane pane
                PathTransition pt = new PathTransition(Duration.millis(duration), path, cardBack);

                // When the path transition is finished, pickup card from deck
                pt.setOnFinished(e1 -> {
                    // pause for effect
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    // set the temp card to the card being drawn from the deck
                    try {
                        comp.setTempCard(deck.pickUp());
                    } catch (DeckIsEmptyException e2) {
                        System.out.println(e2.getMessage());
                    }
                    comp.play("discard");
                    borderPane.getChildren().remove(cardBack);
                    
                    // The following is not needed for the computer player, but can be used for debugging purposes
//                    // fade out cardback to reveal the card just drawn
//                    FadeTransition ft = new FadeTransition(Duration.millis(duration), cardBack);
//                    ft.setFromValue(1.0);
//                    ft.setToValue(0.0);
//                    // When fade out is finished, remove cardBack node from borderPane
//                    ft.setOnFinished(e2 -> {
//                        borderPane.getChildren().remove(cardBack);
//                    });
//                    ft.play();
                });
                pt.play();
            }
        });

        // Action for Discard Pile Pick Up Button
        discardPane.getPickUpBtn().setOnAction(e -> {
            if (humansTurn) { // the human wants to pick up the last discarded card
                // Obtain discardPane position
                Bounds slotBounds = discardPane.localToScene(discardPane.getBoundsInLocal());
                NodePosition discardPos = new NodePosition(slotBounds.getMaxX() - 50, slotBounds.getMaxY() / 2);
                // Obtain human temp slot position
                slotBounds = humanTempSlot.localToScene(humanTempSlot.getBoundsInLocal());
                NodePosition tempSlotPos = new NodePosition(slotBounds.getMaxX() / 2 + 70, slotBounds.getMaxY() - 72);

                int duration = 700; // in milliseconds
                Line path = new Line(discardPos.getX(), discardPos.getY(), tempSlotPos.getX(), tempSlotPos.getY());
                ImageView cardView = new ImageView(discardPile.getTopCard().getCardImage());
                borderPane.getChildren().add(cardView); // add the card to the borderPane pane
                PathTransition pt = new PathTransition(Duration.millis(duration), path, cardView);

                // When the path transition is finished, pickup card from discard pile, remove cardView
                pt.setOnFinished(e1 -> {
                    // set the temp card to the card being drawn from the discard pile
                    human.setTempCard(discardPile.pickUpCard());
                    human.getHandPane().getTempSlot().getKeepBtn().fire(); // the player intends to keep this card
                    if (!discardPile.isEmpty() && discardPile != null) {
                        discardPane.showTopCard(discardPile.getTopCard());
                    } else {
                        discardPane.setTopCardToCardBack();
                    }
                    borderPane.getChildren().remove(cardView);
                });
                pt.play();
                discardPane.setTopCardToCardBack();
                deckPane.hidePickUpBtn();
                discardPane.hidePickUpBtn();
            } else { // the computer wants to pick up the last discarded card
                // Obtain discardPane position
                Bounds slotBounds = discardPane.localToScene(discardPane.getBoundsInLocal());
                NodePosition discardPos = new NodePosition(slotBounds.getMaxX() - 50, slotBounds.getMaxY() / 2);
                // Obtain computer temp slot position
                slotBounds = compTempSlot.localToScene(compTempSlot.getBoundsInLocal());
                NodePosition tempSlotPos = new NodePosition(slotBounds.getMaxX() / 2 + 70, slotBounds.getMaxY() - 72);

                int duration = 700; // in milliseconds
                Line path = new Line(discardPos.getX(), discardPos.getY(), tempSlotPos.getX(), tempSlotPos.getY());
                ImageView cardView = new ImageView(discardPile.getTopCard().getCardImage());
                borderPane.getChildren().add(cardView); // add the card to the borderPane pane
                cardView.setTranslateX(-200); // so that when the node is added, the card is not briefly flashed in the
                                              // upper left corner of the borderPane pane
                PathTransition pt = new PathTransition(Duration.millis(duration), path, cardView);

                // When the path transition is finished, pickup card from discard pile, remove cardView
                pt.setOnFinished(e1 -> {
                    // pause for effect
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    // set the temp card to the card being drawn from the discard pile
                    comp.setTempCard(discardPile.pickUpCard());
                    if (!discardPile.isEmpty() && discardPile != null) {
                        discardPane.showTopCard(discardPile.getTopCard());
                    } else {
                        discardPane.setTopCardToCardBack();
                    }

                    borderPane.getChildren().remove(cardView);
                    comp.play("discard");
                });
                pt.play();
                discardPane.setTopCardToCardBack();
            }
        });

//-----------------------------------Start of human actions ---------------------------------------------

        humanTempSlot.getKeepBtn().setOnAction(e -> {
            // show discard btns in hand slots
            human.getHandPane().showHandSlotDiscardBtns();
            // hide keep and discard btns of temp slot
            humanTempSlot.hideKeepBtn();
            humanTempSlot.hideDiscardBtn();
        });

        humanTempSlot.getDiscardBtn().setOnAction(e -> {
            // reset the temp slot to default settings
            humanTempSlot.resetSlot();

            // Obtain human temp slot position
            Bounds slotBounds = humanTempSlot.localToScene(humanTempSlot.getBoundsInLocal());
            NodePosition tempSlotPos = new NodePosition(slotBounds.getMaxX() / 2 + 70, slotBounds.getMaxY() - 72);
            // Obtain discardPane position
            slotBounds = discardPane.localToScene(discardPane.getBoundsInLocal());
            NodePosition discardPos = new NodePosition(slotBounds.getMaxX() - 60, slotBounds.getMaxY() / 2);

            int duration = 700; // in milliseconds
            Line path = new Line(tempSlotPos.getX(), tempSlotPos.getY(), discardPos.getX(), discardPos.getY());
            ImageView cardView = new ImageView(human.getTempCard().getCardImage());
            borderPane.getChildren().add(cardView); // add the card to the borderPane pane
            PathTransition pt = new PathTransition(Duration.millis(duration), path, cardView);

            // When the path transition is finished, add card to discard pile, remove image view of card used for transition
            pt.setOnFinished(e1 -> {
                // push card to discard pile
                discardPile.discard(human.getTempCard());
                // remove tempCard from human
                human.removeTempCard();
                // show discarded card in discardPane
                discardPane.showTopCard(discardPile.getTopCard());
                // remove cardView used for transition
                borderPane.getChildren().remove(cardView);
                // end human player's turn
                humansTurn = false;
                logger.log(Level.INFO, human.toString());

                // start computers turn
                comp.play("pickup");


            });
            pt.play();
        });

        //      Actions for the discard buttons of the human hand slots
        for (CardSlot slot : human.getHandPane().getHandSlots()) {
            slot.getDiscardBtn().setOnAction(e -> {
                // Obtain human temp slot position
                Bounds slotBounds = humanTempSlot.localToScene(humanTempSlot.getBoundsInLocal());
                NodePosition tempSlotPos = new NodePosition(slotBounds.getMaxX() / 2 + 70, slotBounds.getMaxY() - 72);
                // Obtain HandSlot position
                slotBounds = slot.localToScene(slot.getBoundsInLocal());
                NodePosition handSlotPos = new NodePosition(slotBounds.getMaxX() - 50, slotBounds.getMaxY() - 72);
                // Obtain discardPane position
                slotBounds = discardPane.localToScene(discardPane.getBoundsInLocal());
                NodePosition discardPos = new NodePosition(slotBounds.getMaxX() - 60, slotBounds.getMaxY() / 2);

                int duration = 700; // in milliseconds

                // Transition from hand to discard pile
                Line handToDiscardPath = new Line(handSlotPos.getX(), handSlotPos.getY(), discardPos.getX(), discardPos.getY());
                ImageView handCardView = new ImageView(slot.getCard().getCardImage());
                borderPane.getChildren().add(handCardView); // add the card to the borderPane pane
                PathTransition handToDiscardTrans = new PathTransition(Duration.millis(duration), handToDiscardPath, handCardView);

                // Transition from temp slot to hand
                Line tempToHandPath = new Line(tempSlotPos.getX(), tempSlotPos.getY(), handSlotPos.getX(), handSlotPos.getY());
                ImageView tempView = new ImageView(human.getTempCard().getCardImage());
                borderPane.getChildren().add(tempView); // add the card to the borderPane pane
                PathTransition tempToHandTrans = new PathTransition(Duration.millis(duration), tempToHandPath, tempView);

                handToDiscardTrans.setOnFinished(e1 -> {
                    // show discarded card in discardPane
                    discardPane.showTopCard(discardPile.getTopCard());
                    // remove cardView used for transition
                    borderPane.getChildren().remove(handCardView);
                });

                tempToHandTrans.setOnFinished(e1 -> {
                    // add card to players hand
                    human.addToHand(human.getTempCard());
                    // remove cardView used for transition
                    borderPane.getChildren().remove(tempView);
                    // reset tempCard for human
                    human.removeTempCard();
                    logger.log(Level.INFO, human.toString());


                    //check if human won
                    if (isWinner(human)) {
                        winLosePane.setGameResult("human");
                        root.getChildren().add(winLosePane);
                        logger.log(Level.INFO, "Human Won!");
                    } else {
                        // start computers turn
                        comp.play("pickup");
                    }
                });


                handToDiscardTrans.play();
                tempToHandTrans.play();

                // push card to discard pile
                discardPile.discard(slot.getCard());
                // remove card from players hand
                human.removeCard(slot.getCard());
                // reset the hand slot
                slot.resetSlot();
                // reset the tempSlot
                humanTempSlot.resetSlot();
                // hide discard buttons
                human.getHandPane().hideHandSlotDiscardBtns();
                // end human player's turn
                humansTurn = false;
            });
        }

//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^End of human actions ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

//-------------------------------------Start computer actions--------------------------------------------

        compTempSlot.getDiscardBtn().setOnAction(e -> {
            // reset the temp slot to default settings
            compTempSlot.resetSlot();

            // Obtain comp temp slot position
            Bounds slotBounds = compTempSlot.localToScene(compTempSlot.getBoundsInLocal());
            NodePosition tempSlotPos = new NodePosition(slotBounds.getMaxX() / 2 + 70, slotBounds.getMaxY() - 72);
            // Obtain discardPane position
            slotBounds = discardPane.localToScene(discardPane.getBoundsInLocal());
            NodePosition discardPos = new NodePosition(slotBounds.getMaxX() - 60, slotBounds.getMaxY() / 2);

            int duration = 700; // in milliseconds
            Line path = new Line(tempSlotPos.getX(), tempSlotPos.getY(), discardPos.getX(), discardPos.getY());
            ImageView cardBack = new ImageView(cardBackImage);
            borderPane.getChildren().add(cardBack); // add the card to the borderPane pane
            cardBack.setTranslateX(-200); // so that when the node is added, the card is not briefly flashed in the
                                          // upper left corner of the borderPane pane
            PathTransition pt = new PathTransition(Duration.millis(duration), path, cardBack);

            // When the path transition is finished, add card to discard pile, remove image view of card used for transition
            pt.setOnFinished(e1 -> {
                // push card to discard pile
                discardPile.discard(comp.getTempCard());
                // remove tempCard from computer
                comp.removeTempCard();
                // show discarded card in discardPane
                discardPane.showTopCard(discardPile.getTopCard());
                // fade out and remove cardView used for transition
                FadeTransition ft = new FadeTransition(Duration.millis(duration), cardBack);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.setOnFinished(e2 -> {
                    borderPane.getChildren().remove(cardBack);
                    // show pickup buttons for human
                    deckPane.showPickUpBtn();
                    if (!discardPile.isEmpty() && discardPile != null) { // only show the pick up btn for the human discard pile as long as it is not empty (deck may have reshuffled)
                        discardPane.showPickUpBtn();
                    }
                });
                ft.play();
                // end computer player's turn
                humansTurn = true;
                logger.log(Level.INFO, comp.toString());
            });
            pt.play();
        });

        //      Actions for the discard buttons of the computer hand slots
        for (CardSlot slot : comp.getHandPane().getHandSlots()) {
            slot.getDiscardBtn().setOnAction(e -> {
                // Obtain comp temp slot position
                Bounds slotBounds = compTempSlot.localToScene(compTempSlot.getBoundsInLocal());
                NodePosition tempSlotPos = new NodePosition(slotBounds.getMaxX() / 2 + 70, slotBounds.getMaxY() - 72);
                // Obtain HandSlot position
                slotBounds = slot.localToScene(slot.getBoundsInLocal());
                NodePosition handSlotPos = new NodePosition(slotBounds.getMaxX() - 50, slotBounds.getMaxY() - 72);
                // Obtain discardPane position
                slotBounds = discardPane.localToScene(discardPane.getBoundsInLocal());
                NodePosition discardPos = new NodePosition(slotBounds.getMaxX() - 60, slotBounds.getMaxY() / 2);

                int duration = 700; // in milliseconds

                // Transition from hand to discard pile
                Line handToDiscardPath = new Line(handSlotPos.getX(), handSlotPos.getY(), discardPos.getX(), discardPos.getY());
                ImageView handCardView = new ImageView(cardBackImage);
                borderPane.getChildren().add(handCardView); // add the card to the borderPane pane
                handCardView.setTranslateX(-200); // so that when the node is added, the card is not briefly flashed in the
                                              // upper left corner of the borderPane pane
                PathTransition handToDiscardTrans = new PathTransition(Duration.millis(duration), handToDiscardPath, handCardView);

                // Transition from temp slot to hand
                Line tempToHandPath = new Line(tempSlotPos.getX(), tempSlotPos.getY(), handSlotPos.getX(), handSlotPos.getY());
                ImageView tempView = new ImageView(cardBackImage);
                borderPane.getChildren().add(tempView); // add the card to the borderPane pane
                tempView.setTranslateX(-200); // so that when the node is added, the card is not briefly flashed in the
                                              // upper left corner of the borderPane pane
                PathTransition tempToHandTrans = new PathTransition(Duration.millis(duration), tempToHandPath, tempView);

                handToDiscardTrans.setOnFinished(e1 -> {
                    // show discarded card in discardPane
                    discardPane.showTopCard(discardPile.getTopCard());
                    // remove cardView used for transition
                    borderPane.getChildren().remove(handCardView);
                });

                tempToHandTrans.setOnFinished(e1 -> {
                    // add card to computer players hand
                    comp.addToHand(comp.getTempCard());
                    // remove cardView used for transition
                    borderPane.getChildren().remove(tempView);
                    // reset tempCard for comp
                    comp.removeTempCard();
                    logger.log(Level.INFO, comp.toString());

                    //check if computer won
                    if (isWinner(comp)) {
                        showCompCards.get(0).setOnFinished(e2 -> {
                            winLosePane.setGameResult("computer");
                            root.getChildren().add(winLosePane);
                        });
                        // show computer's hand
                        for (FadeTransition ft :
                                showCompCards) {
                            ft.play();
                        }
                        logger.log(Level.INFO, "Computer Won!");
                    } else {
                        // show pick up buttons for human's turn
                        deckPane.showPickUpBtn();
                        if (!discardPile.isEmpty() && discardPile != null) { // only show the pick up btn for the human discard pile as long as it is not empty (deck may have reshuffled)
                            discardPane.showPickUpBtn();
                        }
                    }

                });


                handToDiscardTrans.play();
                tempToHandTrans.play();

                // push card to discard pile
                discardPile.discard(slot.getCard());
                // remove card from players hand
                comp.removeCard(slot.getCard());
                // reset the hand slot
                slot.resetSlot();
                // reset the tempSlot
                compTempSlot.resetSlot();
                // end computer player's turn
                humansTurn = true;
            });
        }

//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^End computer actions^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

        winLosePane.getPlayAgainBtn().setOnAction(e -> {
            try {
                logger.log(Level.INFO, "Starting new Game...");
                start(primaryStage);
                logger.log(Level.INFO, deck.toString());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        Scene scene = new Scene(root);
        scene.getStylesheets().add("view/custom.css");

        primaryStage.setTitle("Four Of A Kind");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
