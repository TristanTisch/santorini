package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;

public class Main extends Application {
    Tile formerTile;
    int formerTileNumber;
    boolean newGamePhase;
    boolean startPhase;
    boolean movePhase;
    boolean selectPhase;
    Player p1 = new Player(1);
    Player p2 = new Player(2);
    Player playerOnturn = p1;
    int roundNumber;
    private Tile[][] tiles = new Tile[5][5];
    Text infoText = new Text("Hiya! Press any tile to start");
    Rectangle infoTextRect = new Rectangle(500, 50, Color.WHEAT);

    @Override
    public void start(Stage primaryStage) throws Exception{
        roundNumber = 1;
        newGamePhase = true;
        startPhase = false;
        movePhase = false;
        selectPhase = false;
        Pane root = new Pane();
        root.setPrefSize(500, 550);
        for(int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                Tile tile = new Tile(x, y);
                tiles[x][y] = tile;
                root.getChildren().add(tile);
            }
        }
        StackPane infoPane = new StackPane();
        infoText.setFill(Color.WHITE);
        infoText.setFont(Font.font("Verdana", 20));
        infoPane.getChildren().addAll(infoTextRect, infoText);
        infoPane.setTranslateX(0);
        infoPane.setTranslateY(500);
        root.getChildren().add(infoPane);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private class Tile extends StackPane{
        int x, y, height;
        boolean playerOnTile;
        private Rectangle border = new Rectangle(92, 92);
        private Text text = new Text();
        public Tile(int x, int y) {
            height = 0;
            this.x = x;
            this.y = y;
            border.setStroke(Color.LIGHTBLUE);
            border.setFill(Color.LIGHTBLUE);
            border.setStrokeWidth(4);
            this.setTranslateX(x * 100);
            this.setTranslateY(y * 100);
            this.getChildren().addAll(border, text);
            this.setOnMouseClicked(e -> action());
        }
        private LinkedList<Tile> getAdjacentTiles(){
            LinkedList<Tile> adjlist = new LinkedList<>();
            int[][] lookup = new int[][]{{-1, -1},{0, -1},{1, -1},{1, 0},{1, 1},{0, 1},{-1, 1},{-1, 0}};
            for(int i = 0; i < 8; i++) {
                int adjx = lookup[i][0];
                int adjy = lookup[i][1];
                adjx += this.x;
                adjy += this.y;
                if(adjx < 5 && adjy < 5 && adjx > -1 && adjy > -1) adjlist.add(tiles[adjx][adjy]);
            }
            return adjlist;
        }
        private void action() {
            if(newGamePhase) {
                if(!infoText.getText().equals("Hiya! Press any tile to start")) {
                    for (int i = 0; i < 5; i++) {
                        for(int j = 0; j < 5; j++) {
                            tiles[i][j].text.setText("");
                            tiles[i][j].border.setStroke(Color.LIGHTBLUE);
                            tiles[i][j].playerOnTile = false;
                            tiles[i][j].height = 0;
                            playerOnturn = p1;
                            roundNumber = 1;
                        }
                    }
                }
                newGamePhase = false;
                startPhase = true;
                infoText.setText("Player " + playerOnturn.number + ": Please set your " + roundNumber + ". piece");
                infoTextRect.setFill(Color.RED);
            }
            else if(startPhase) {
                playerOnTile = true;
                //Set stroke and tile
                this.border.setStroke((playerOnturn == p1) ? Color.RED : Color.BLUE);
                if(playerOnturn == p1) p1.tile[roundNumber-1] = this;
                else p2.tile[roundNumber-1] = this;
                //If each player has set their piece, the real game starts
                togglePlayer();
                if (roundNumber != 3) {
                    startPhase = true;
                    movePhase = false;
                    infoText.setText("Player " + playerOnturn.number + ": Please set your " + roundNumber + ". piece");
                }
                else infoText.setText("Player " + playerOnturn.number + ": Please select a piece to move with");
            }
            else if(selectPhase) {
                if((playerOnturn == p1 && (this == p1.tile[1] || this == p1.tile[0])) || (playerOnturn == p2 && (this == p2.tile[1] || this == p2.tile[0]))) {
                    formerTile = this;
                    formerTileNumber = (this == p1.tile[0] || this == p2.tile[0])? 0 : 1;
                    selectPhase = false;
                    movePhase = true;
                    infoText.setText("Player " + playerOnturn.number + ": Please select a tile to move to");
                }
            }
            else if(movePhase) {
                if(formerTile.getAdjacentTiles().contains(this) && !this.playerOnTile && this.height <= formerTile.height+1 && this.height != 4) {
                    //Update formerTile
                    formerTile.border.setStroke(Color.LIGHTBLUE);
                    formerTile.playerOnTile = false;
                    //Update Tile and Player
                    this.border.setStroke((playerOnturn == p1)? Color.RED : Color.BLUE);
                    this.playerOnTile = true;
                    playerOnturn.tile[formerTileNumber] = this;
                    formerTile = this;
                    movePhase = false;
                    infoText.setText("Player " + playerOnturn.number + ": Please selct a tile to build on");
                    if(this.height == 3) {
                        infoText.setText("Player " + playerOnturn.number + " has won! Click any tile to start anew");
                        newGamePhase = true;
                    }
                }
            }
            else{
                if(formerTile.getAdjacentTiles().contains(this) && !this.playerOnTile && this.height < 4) {
                    this.height++;
                    this.text.setText(String.valueOf(this.height));
                    togglePlayer();
                    infoText.setText("Player " + playerOnturn.number + ": Please select a piece to move with");
                }
            }
        }
        private void togglePlayer() {
            if(playerOnturn == p1) {
                playerOnturn = p2;
                infoTextRect.setFill(Color.BLUE);
            }
            else {
                playerOnturn = p1;
                infoTextRect.setFill(Color.RED);
            }
            if(playerOnturn == p1)roundNumber++;
            selectPhase = true;
            startPhase = false;
        }

    }

    private class Player{
        int number;
        Tile[] tile = new Tile[2];
        public Player(int number) {
            this.number = number;
        }
    }
}
