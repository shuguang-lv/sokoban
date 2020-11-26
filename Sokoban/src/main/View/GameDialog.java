package View;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The type Game dialog.
 */
// dialog class extracted
public class GameDialog {
    /**
     * The Dialog.
     */
    private final Stage dialog;
    /**
     * The Dialog title.
     */
    private String dialogTitle;
    /**
     * The Dialog message.
     */
    private String dialogMessage;
    /**
     * The Dialog message effect.
     */
    private Effect dialogMessageEffect;

    /**
     * Instantiates a new Game dialog.
     *
     * @param primaryStage        the primary stage
     * @param dialogTitle         the dialog title
     * @param dialogMessage       the dialog message
     * @param dialogMessageEffect the dialog message effect
     */
    public GameDialog(final Stage primaryStage, final String dialogTitle, final String dialogMessage, final Effect dialogMessageEffect) {
        dialog = setDialogTitle(primaryStage, dialogTitle);
        Text text1 = setDialogMessage(dialogMessage, dialogMessageEffect);
        setDialogScene(text1);
    }

    /**
     * Sets dialog scene.
     *
     * @param text1 the text 1
     */
    private void setDialogScene(Text text1) {
        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setBackground(Background.EMPTY);
        dialogVbox.getChildren().add(text1);

        Scene dialogScene = new Scene(dialogVbox, 350, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Sets dialog message.
     *
     * @param dialogMessage       the dialog message
     * @param dialogMessageEffect the dialog message effect
     * @return the dialog message
     */
    private Text setDialogMessage(String dialogMessage, Effect dialogMessageEffect) {
        Text text1 = new Text(dialogMessage);
        text1.setTextAlignment(TextAlignment.CENTER);
        text1.setFont(javafx.scene.text.Font.font(14));

        if (dialogMessageEffect != null) {
            text1.setEffect(dialogMessageEffect);
        }
        return text1;
    }

    /**
     * Sets dialog title.
     *
     * @param primaryStage the primary stage
     * @param dialogTitle  the dialog title
     * @return the dialog title
     */
    private Stage setDialogTitle(Stage primaryStage, String dialogTitle) {
        final Stage dialog;
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setResizable(false);
        dialog.setTitle(dialogTitle);
        return dialog;
    }
}