package fourofakind;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


/**
 * The WinLosePane contains the result of the game from the human players perspective,
 * (ie "You Win!" or "You Lose!").  This pane also incorporates a play again button,
 * should the user wish to play the game again.
 * 
 * @author William Hess
 */
public class WinLosePane extends VBox {
    private Image youWin = new Image("view/images/YOUWIN.PNG");
    private Image youLose = new Image("view/images/YOULOSE.PNG");
    private ImageView gameResult = new ImageView();

    private Image brian = new Image("view/images/brian.png", 0, 100, true, true);
    private Image brianStewie = new Image("view/images/brianStewie.png", 0, 100, true, true);
    private ImageView fgView = new ImageView();

    private Button playAgainBtn = new Button("Play Again?");

    public WinLosePane() {
        // Create glowing effect
        DropShadow ds = new DropShadow(15, Color.WHITE);
        gameResult.setEffect(ds);
        gameResult.setOpacity(0.8);

        playAgainBtn.setStyle("-fx-font-size: 22;");

        setAlignment(Pos.CENTER);
        getChildren().addAll(gameResult, fgView, playAgainBtn);
    }

    /**
     * The setGameResult() method initializes the gameResult based on the winner.
     * 
     * @param winner -> "human" or "computer"
     */
    
    public void setGameResult(String winner) {
        switch (winner) {
            case "human":
                gameResult.setImage(youWin);
                fgView.setImage(brian);
                break;
            case "computer":
                gameResult.setImage(youLose);
                fgView.setImage(brianStewie);
                break;
        }
    }

    public Button getPlayAgainBtn() {
        return playAgainBtn;
    }

}
