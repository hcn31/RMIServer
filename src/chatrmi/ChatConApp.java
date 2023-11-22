/// Mehdi 

package chatrmi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;





public class ChatConApp extends Application {
	 private HBox userContainer;
    private ChatConCliente user;
    private TextArea msgTextArea;
    private Button buttonSend;
    private ScrollPane scrollMsg;
    private String lastUserMessage;
    private VBox root;
    private Map<String, VBox> connectedUsers;
   
    private Integer i = 0;

    public ChatConApp() {
    	
    	 userContainer = new HBox();
         userContainer.setSpacing(10);
         userContainer.setPadding(new Insets(10));
         connectedUsers = new HashMap<>();
   
        user = new ChatConCliente();

        lastUserMessage = "";
       
        Image iconImage = new Image(getClass().getResourceAsStream("send.png"));
        ImageView iconImageView = new ImageView(iconImage);
        iconImageView.setFitWidth(25);
        iconImageView.setFitHeight(25);

        // Create a button with the icon and text
        buttonSend = new Button("Envoyer", iconImageView);
        buttonSend.setContentDisplay(ContentDisplay.LEFT);
        buttonSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                envoyer();
                msgTextArea.clear();
            }
        });
        buttonSend.setMinSize(100, 20);

        msgTextArea = new TextArea();

        //msgTextArea.setMinHeight(3);
        msgTextArea.setPrefHeight(2);
        msgTextArea.setMaxHeight(4);
        msgTextArea.setWrapText(true);

        msgTextArea.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (msgTextArea.getText().length() > 500) {
                    String s = msgTextArea.getText().substring(0, 500);
                    msgTextArea.setText(s);
                }
            }

        });

        msgTextArea.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            if (key.getCode() == KeyCode.ENTER && !key.isShiftDown()) {
                envoyer();
                msgTextArea.clear();
            } else if (key.getCode() == KeyCode.ENTER && key.isShiftDown()) {
                msgTextArea.setText(msgTextArea.getText() + "\n");
                msgTextArea.positionCaret(msgTextArea.getText().length());
            }
        }
        );

        scrollMsg = new ScrollPane();

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                lastUserMessage = user.getNewMessage();
                
                

                if (!lastUserMessage.equals("")) {
                    Label newMessage = new Label();

                    newMessage.setMaxHeight(Double.MAX_VALUE);

                    newMessage.setWrapText(true);

                    newMessage.setStyle("-fx-border-color: white;");

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");

                    Date date = new Date();

                    Label timeStamp = new Label(dateFormat.format(date));

                    timeStamp.setStyle("-fx-font-size: 10px;");

                    VBox messageBox = new VBox();
                    
               

                    messageBox.setAlignment(Pos.TOP_LEFT);

                    String outro = lastUserMessage.substring(0, lastUserMessage.indexOf(":"));

                    if (outro.equals(user.getNom())) {
                        newMessage.setPadding(new Insets(0, 1, 0, 0));
                        
                        newMessage.setStyle("-fx-background-color: #DEB992; " +
                                "-fx-border-color: #A0522D; " +
                                "-fx-border-radius: 3px; " +
                                "-fx-padding: 4px;"
                                + "-fx-alignment: top-right; -fx-column-halignment: right;");

                        //newMessage.setTextFill(Color.web("#ffffff"));

                        newMessage.setAlignment(Pos.TOP_RIGHT);

                        messageBox.setAlignment(Pos.TOP_RIGHT);

                        newMessage.setText(lastUserMessage.trim().substring(lastUserMessage.indexOf(":") + 2));
                    } else {
                    	
                    		 // Create the main text
                            Text mainText = new Text(lastUserMessage.trim().split(":")[0]);
                            mainText.setStyle("-fx-font-weight: bold;");

                            // Create the subtext
                            Text subText = new Text(lastUserMessage.trim().split(":")[1]);
                           

                            // Set the main text and subtext in the Label
                            newMessage.setGraphic(new VBox(mainText, subText));
                            //newMessage.setText(lastClntMessage.trim());ùmù
                            
                            newMessage.setStyle("-fx-background-color: #A0AECD; " +
                                    "-fx-border-color: #00458B; " +
                                    "-fx-border-radius: 3px; " +
                                    "-fx-padding: 4px;");
                           
                            addUser(lastUserMessage.trim().split(":")[0], "user.png");
                           
                    	
                    }
                   
                    //newMessage.setPrefHeight(100);
                    newMessage.textOverrunProperty().set(OverrunStyle.CLIP);

                    newMessage.setMinHeight(Label.USE_PREF_SIZE);

                    
                    messageBox.getChildren().addAll(newMessage, timeStamp);
               
                    root.getChildren().add(messageBox);

                    scrollMsg.setVvalue(1.0);
                    scrollMsg.setHvalue(1.0);
                    scrollMsg.setPrefViewportHeight(1.0);

                    i++;
                }
            }

        }.start();
    }

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");
        hbox.getChildren().addAll(msgTextArea);

        return hbox;
    }
    
    
    private void addUser(String username, String iconFilename) {
    	 // Check if user already exists
        if (connectedUsers.containsKey(username)) {
            return;
        }
 
        Image iconImage = new Image(getClass().getResourceAsStream(iconFilename));
        ImageView iconImageView = new ImageView(iconImage);
        iconImageView.setFitWidth(50);
        iconImageView.setFitHeight(50);


        Label userLabel = new Label(username);
        userLabel.setStyle("-fx-font-weight: bold;");
     
        VBox userBox = new VBox(iconImageView, userLabel);
        userBox.setAlignment(Pos.CENTER);
        userContainer.getChildren().add(userBox);
        connectedUsers.put(username, userBox);
    }

 

    public void envoyer() {
        if (msgTextArea.getText().trim().length() > 0) {
            user.envoyerMessage(msgTextArea.getText().trim());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.getIcons().add(new Image("file:send.png"));

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
                user.exit();
            }
        });

        String nom = "";

        TextInputDialog dialog = new TextInputDialog("Entrez votre nom ici.");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:send.png"));

        dialog.setTitle("Identifiant");
        dialog.setHeaderText("Identifiant ");
        dialog.setContentText("Votre nom :");

        Optional<String> result = null;
        boolean validUsername = false;
        while (!validUsername) {
            result = dialog.showAndWait();

            if (result.isPresent()) {
                nom = result.get();
            } else {
                Platform.exit();
                System.exit(0);
            }

         
        }

        user.login(nom);

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (user) {
                    user.run();
                }
            }
        }).start();

        BorderPane border = new BorderPane();
        Scene scene = new Scene(border, 800, 600);

        HBox hBox = addHBox();

        StackPane stack = new StackPane();

        stack.getChildren().addAll(buttonSend);
        stack.setAlignment(Pos.CENTER_RIGHT);
    
        StackPane.setMargin(buttonSend, new Insets(0, 25, 0, 25));
        stack.setStyle("-fx-background-color: #336699;");

        GridPane grid = new GridPane();

        hBox.prefWidthProperty().bind(primaryStage.widthProperty());
        msgTextArea.prefWidthProperty().bind(hBox.prefWidthProperty());

        grid.add(hBox, 0, 0);
        grid.add(stack, 1, 0);

        root = new VBox();

        root.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: #336699;");

        VBox.setVgrow(root, Priority.ALWAYS);

 
        scrollMsg.setContent(root);

        VBox test = new VBox();

        test.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: #336699;");

        test.getChildren().add(scrollMsg);

        scrollMsg.prefWidthProperty().bind(test.widthProperty());
        scrollMsg.prefHeightProperty().bind(test.heightProperty());

        scrollMsg.setFitToHeight(true);
        scrollMsg.setFitToWidth(true);

        border.setTop(userContainer);
        border.setBottom(grid);
        border.setCenter(scrollMsg);

        primaryStage.setTitle("Chat Application - "+user.getNom());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

 
    public static void main(String[] args) {
        launch(args);
    }

}
