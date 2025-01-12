package server.models;

import com.sun.security.jgss.GSSUtil;

import java.sql.SQLOutput;
import java.util.Arrays;

public class Board {
    private Symbol[][] tiles;
    public enum Symbol{
        EMPTY,O,X
    }
    public Board() {
        tiles = new Symbol[3][3]; // row , column
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                tiles[i][j] = Symbol.EMPTY;
            }
        }
    }

    public boolean checkWin(Symbol symbol){
        //vertical
        for(int column = 0; column<3; column++){
            Symbol[] columnTiles = new Symbol[3];
            for(int row = 0; row<3; row++){
                columnTiles[row] = tiles[row][column];
            }
            if(areIdentical(columnTiles, symbol))
                return true;
        }
        //horizontal
        for(int row = 0; row<3; row++){
            Symbol[] rowTiles = new Symbol[3];
            for(int column = 0; column<3; column++){
                rowTiles[column] = tiles[row][column];
            }
            if(areIdentical(rowTiles, symbol))
                return true;
        }

        //cross
        Symbol[] crossTilesDown = {tiles[0][0], tiles[1][1], tiles[2][2] };
        if(areIdentical(crossTilesDown, symbol))
            return true;
        Symbol[] crossTilesUp = {tiles[0][2], tiles[1][1], tiles[2][0] };
        if(areIdentical(crossTilesUp, symbol))
            return true;

        return false;
    }
    public boolean checkDraw(){
        for(int row = 0; row<3; row++){
            for(int column = 0; column<3; column++){
                if(tiles[row][column] == Symbol.EMPTY)
                    return false;
            }
        }
        return true;
    }
    //Sprawdzanie czy wygrana
    public boolean areIdentical(Symbol[] symbols, Symbol symbol){
        for(int i=0; i<symbols.length; i++){
            if(symbols[i] != symbol)
                return false;
        }
        return true;
    }
    public boolean makeMove(int row, int column, Symbol symbol){
        if(tiles[row][column] == Symbol.EMPTY){
            tiles[row][column] = symbol;
            return true;
        }
        return false;
    }
    public String[][] getBoard(){
        String[][] map = new String[3][3];
        for(int row=0; row<3; row++){
            for(int column=0; column<3; column++){
                if(tiles[row][column] == Symbol.EMPTY)
                    map[row][column] = "-";
                else
                    map[row][column] = tiles[row][column].toString();
            }
        }
        return map;
    }
}
