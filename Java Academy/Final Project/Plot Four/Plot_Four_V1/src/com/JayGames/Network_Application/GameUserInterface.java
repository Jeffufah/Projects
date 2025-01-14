package com.JayGames.Network_Application;

import com.JayGames.PlotFour_Multiplayer.PlotFourGame;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 *
 * Course ID: EYF-649 
 * Date: 2019/03/30
 * @author Jeffrey McMullen II
 * 
 * The GameUserInterface works with the GameClient class to receive
 * updated information from the server to display to the end user. This class
 * also handles input from the user.
 */
public class GameUserInterface
{    
    private final PageLoader pageLoader;
    
    private final GameClient gameClient; //The partner class for server interaction.
    
    private final PlotFourGame plotFourGameMultiplayer;
    
    private VBox chatWindow = new VBox(5);
    
    private Label membersLabel = new Label();

    private Label hostIPLabel = new Label();

    private Label pingLabel = new Label();
    
    private String lobbyMemberHeader = "(Lobby Members)                      "
            + "\n____________________________________\n";
    
    private ArrayList<AnchorPane> tempMessages = new ArrayList();
    
    private Date lastTempMsgTimeStamp = new Date();
    
    /**
     * Constructs this class by requiring the pageLoader for navigating pages,
     * and the gameClient to enable the user to interact with the server
     * via network.
     * @param pageLoader An object of type PageLoader to navigate menus.
     * @param gameClient An object of type GameClient to interact with the
     * server.
     * @param plotFourGameMultiplayer An object of type PlotFourGame that will be
     * displayed to the userInterface for game interaction between player one and
     * player two.
     */
    public GameUserInterface(PageLoader pageLoader, GameClient gameClient, PlotFourGame plotFourGameMultiplayer) 
    {
        this.pageLoader = pageLoader;
        this.gameClient = gameClient;
        this.plotFourGameMultiplayer = plotFourGameMultiplayer;

        chatWindow.setPrefWidth(300);
        ScrollPane chatScrollPane = new ScrollPane(chatWindow);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.vvalueProperty().bind(chatWindow.heightProperty());

        TextField textField = new TextField();
        textField.setMinSize(600, 45);
        textField.setOnAction(e ->
        {
            if (!textField.getText().equals(""))
            {
                try
                {
                    Message clientMessage = new Message(gameClient.getName(), textField.getText(), false, false, false);
                    gameClient.sendMessage(clientMessage);
                    textField.setText("");
                }
                catch (RemoteException ex)
                {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button inputButton = new Button();
        inputButton.setText("Send Message");
        inputButton.setMinHeight(45);
        inputButton.setMinWidth(75);
        inputButton.setOnAction(buttonClicked ->
        {
            if (!textField.getText().equals(""))
            {
                try
                {
                    Message clientMessage = new Message(gameClient.getName(), textField.getText(), false, false, false);
                    gameClient.sendMessage(clientMessage);
                    textField.setText("");
                }
                catch (RemoteException ex)
                {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button convenienceButton = new Button();
        if (pageLoader.getGameServer() != null)
        {
            convenienceButton.setText("Close Server");
        }
        else
        {
            convenienceButton.setText("Disconnect");
        }
        convenienceButton.setMinHeight(45);
        convenienceButton.setMinWidth(75);
        convenienceButton.setOnAction(buttonClicked ->
        {
            if (pageLoader.getGameServer() != null)
            {
                 System.exit(0);
            }
            else
            {
                gameClient.setKeepPolling(false);
                pageLoader.setGameClient(null);
                pageLoader.loadMainMenu();
            }
        });

        HBox headerBarHBox = new HBox();
        headerBarHBox.getChildren().addAll(hostIPLabel, new Label(), pingLabel, new Label());
        headerBarHBox.setAlignment(Pos.TOP_RIGHT);
        headerBarHBox.setSpacing(30);

        membersLabel = new Label();
        membersLabel.setMaxWidth(180);
        membersLabel.setText(lobbyMemberHeader);

        HBox chatBarHBox = new HBox();
        chatBarHBox.setAlignment(Pos.CENTER_RIGHT);
        chatBarHBox.getChildren().addAll(textField, inputButton, convenienceButton);
        chatBarHBox.setMaxHeight(90);
        
        BorderPane gameBorderPane = new BorderPane(this.plotFourGameMultiplayer.getRoot(), headerBarHBox, chatScrollPane, chatBarHBox, membersLabel);

        
        
        pageLoader.setMainScene(new Scene(gameBorderPane, 1366, 768));

        Stage stage = pageLoader.getPrimaryStage();
        stage.setTitle("Plot Four - " + gameClient.getHostName() + "'s lobby.");
        stage.setScene(pageLoader.getMainScene());
        pageLoader.getMainScene().getWindow().centerOnScreen();
        stage.show();
    }
    
    /**
     * Creates a label to be placed into the chat window with a client message
     * contained within.
     * @param message A String containing a message from a client or server.
     * @param tempMessage A Boolean of true if the message should be removed
     * after some time.
     */
    public void updateChat(String message, boolean tempMessage)
    {
        AnchorPane anchorPane = new AnchorPane();
        Label label = new Label(message);
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setMaxWidth(285);
        AnchorPane.setLeftAnchor(label, 5.0);
        AnchorPane.setTopAnchor(label, 5.0);
        anchorPane.getChildren().addAll(label);
        chatWindow.getChildren().add(anchorPane);
        
        if (tempMessage)
        {
            System.out.println("temp message found");
            addTempMessage(anchorPane);
        }
    }

    /**
     * This method is called when a new member joins the lobby or an existing 
     * member leaves the lobby. The names of the members are appended and 
     * redisplayed as a visible list to the end user.
     */
    public void updateLobbyMembersDisplay()
    {
        StringBuilder namesCollection = new StringBuilder();

        String[] lobbyMembers = gameClient.getLobbyMembers();
        
        for (int i = 0; i < lobbyMembers.length; i++)
        {
            if (i < lobbyMembers.length - 1)
            {
                String clientName = lobbyMembers[i] + "\n";
                namesCollection.append(clientName);
            }
            else
            {
                namesCollection.append(lobbyMembers[i]);
            }
        }

        membersLabel.setText(lobbyMemberHeader + namesCollection.toString());
    }
    
    /**
     * Puts the incoming message into the chat window on the javafx thread.
     * @param message A String containing a message from the client or server.
     * @param tempMessage A Boolean of true if the message should be removed
     * after some time.
     */
    public void setMessage(String message, boolean tempMessage)
    {
        Platform.runLater(() -> updateChat(message, tempMessage));
    }

    /**
     * Displays the client's current ping to the server.
     * @param ping A long value containing the client's ping.
     */
    public void setPingLabel(long ping)
    {
        String pingString;

        if (ping < 10)
        {
            pingString = "00" + ping;
        }
        else if (ping < 100)
        {
            pingString = "0" + ping;
        }
        else
        {
            pingString = Long.toString(ping);
        }

        Platform.runLater(() -> pingLabel.setText("Ping: " + pingString + " ms"));
    }
    
    /**
     * This method is called when the server has gone offline. The menu this 
     * method displays notifies the user of the issue and provides a main menu
     * button to navigate back to the beginning of the program.
     */
    public void displayMainMenuWindow()
    {
        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();

        Label messageLabel = new Label("The host server is no longer available.");

        HBox messageHBox = new HBox();
        messageHBox.getChildren().addAll(messageLabel);

        Button mainMenuButton = new Button();
        mainMenuButton.setText("Main Menu");
        mainMenuButton.setMaxHeight(45);
        mainMenuButton.setMaxWidth(75);
        mainMenuButton.setOnAction(buttonClicked ->
        {
            gameClient.setKeepPolling(false);
            pageLoader.setGameClient(null);
            pageLoader.loadMainMenu();
            stage.close();
        });

        HBox buttonHBox = new HBox();

        buttonHBox.getChildren().addAll(mainMenuButton);

        borderPane.setCenter(messageHBox);
        borderPane.setBottom(buttonHBox);

        Scene scene = new Scene(borderPane, 300, 60);
        stage.setScene(scene);
        scene.getWindow().centerOnScreen();
        stage.setTitle("Host offline.");
        stage.show();
    }

    /**
     * This method is called when the user has lost their internet connection
     * and cannot reach the server. The menu displayed notifies the user of the
     * issue and allows them to either reconnect, or go back to the main menu.
     */
    public void displayReconnectWindow()
    {
        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();

        Label messageLabel = new Label("Check your internet connection and try again.");

        HBox messageHBox = new HBox();
        messageHBox.getChildren().addAll(messageLabel);

        Button reconnectButton = new Button();
        reconnectButton.setText("Reconnect");
        reconnectButton.setMaxHeight(45);
        reconnectButton.setMaxWidth(75);
        reconnectButton.setOnAction(buttonClicked ->
        {
            setMessage("Attempting to reconnect to the server...", false);
            gameClient.setKeepPolling(true);
            gameClient.startPollingThread();
            stage.close();
        });

        Button mainMenuButton = new Button();
        mainMenuButton.setText("Main Menu");
        mainMenuButton.setMaxHeight(45);
        mainMenuButton.setMaxWidth(75);
        mainMenuButton.setOnAction(buttonClicked ->
        {
            gameClient.setKeepPolling(false);
            pageLoader.setGameClient(null);
            pageLoader.loadMainMenu();
        });

        HBox buttonHBox = new HBox();

        buttonHBox.getChildren().addAll(reconnectButton, mainMenuButton);

        borderPane.setCenter(messageHBox);
        borderPane.setBottom(buttonHBox);

        Scene scene = new Scene(borderPane, 300, 60);
        stage.setScene(scene);
        scene.getWindow().centerOnScreen();
        stage.setTitle("Reconnect failed.");
        stage.show();
    }
    
    /**
     * This method is called when the client has just connected to the server
     * and will also initialize labels that have not yet been assigned values
     * so the user has information to see.
     */
    public void initializeLobbyInfo()
    {
        if (gameClient.checkIsServerLocal())
        {
            Platform.runLater(() -> hostIPLabel.setText("Host IP: " + gameClient.getHostIP()));
            setMessage("Server awaiting connections...", false);
        }
        else
        {
            setMessage("You are connected.", false);
        }

        StringBuilder namesCollection = new StringBuilder();
        
        String[] clients = gameClient.getLobbyMembers();

        for (String clientName : clients)
        {
            clientName += "\n";
            namesCollection.append(clientName);
        }

        membersLabel.setText(lobbyMemberHeader + namesCollection.toString());
    }
    
    /**
     * Puts temporary messages into an ArrayList to provide a reference for
     * when the message should be removed.
     * 
     * @param tempMessage An object of type AnchorPane which holds a message. 
     */
    public void addTempMessage(AnchorPane tempMessage)
    {
        lastTempMsgTimeStamp = new Date();
        tempMessages.add(tempMessage);
    }
    
    /**
     * Checks if a temporary message has been around longer than the allotted time
     * and removes it when its the case.
     */
    public void clearTempMessages()
    {
        long diff = DateExpressions.getDateDiff(lastTempMsgTimeStamp, new Date(), TimeUnit.SECONDS);
        int timeoutThreshold = 5;
        if (diff > timeoutThreshold)
        {
            for (AnchorPane pane : tempMessages)
            {
                chatWindow.getChildren().remove(pane);
            }
        }
    }
}