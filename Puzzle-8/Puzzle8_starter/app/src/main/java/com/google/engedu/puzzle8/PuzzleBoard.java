package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {


    public static int NUM_TILES = 5;

    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private int stepNumber;

    private PuzzleBoard previousBoard;

    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }

    public void setPreviousBoard(PuzzleBoard previousBoard) {
        this.previousBoard = previousBoard;
    }

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        stepNumber = 0;
        tiles = new ArrayList<>();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);
        for(int y = 0;y<NUM_TILES;y++){
            for(int x = 0;x<NUM_TILES;x++){
                int tileNumber = y*NUM_TILES + x;
                if(tileNumber != NUM_TILES*NUM_TILES - 1){

                    Bitmap tileBitmap = Bitmap.createBitmap(scaledBitmap,
                            x *scaledBitmap.getWidth() / NUM_TILES,
                            y * scaledBitmap.getHeight() / NUM_TILES,
                            parentWidth / NUM_TILES,
                            parentWidth / NUM_TILES);

                    PuzzleTile tile = new PuzzleTile(tileBitmap,tileNumber);
                    tiles.add(tile);
                }
                else{
                    tiles.add(null);
                }
            }
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard, int stepNumber) {
        previousBoard = otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        this.stepNumber = stepNumber + 1;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> neighbours = new ArrayList<>();
        int emptyTileX = 0;
        int emptyTileY = 0;

        for(int i = 0;i< NUM_TILES * NUM_TILES;i++){
            if(tiles.get(i) == null){
                emptyTileX = i%NUM_TILES;
                emptyTileY = i/NUM_TILES;
                break;
            }
        }
        for (int[] coordinates : NEIGHBOUR_COORDS) {
            int neighbourX = emptyTileX + coordinates[0];
            int neighbourY = emptyTileY + coordinates[1];
            if (neighbourX >= 0 && neighbourX < NUM_TILES
                    && neighbourY >= 0 && neighbourY < NUM_TILES) {

                PuzzleBoard neigbourBoard =
                        new PuzzleBoard(this,stepNumber);

                neigbourBoard.swapTiles(
                        XYtoIndex(neighbourX, neighbourY),
                        XYtoIndex(emptyTileX, emptyTileY));

                neighbours.add(neigbourBoard);
            }
        }
        return neighbours;
    }

    public int priority() {

        int Manhattan_Distance =0;

        for(int i =0; i <NUM_TILES*NUM_TILES;i++){

            PuzzleTile tile = tiles.get(i);
            if(tile != null){
                int correctPosition = tile.getNumber();

                int correctX = correctPosition%NUM_TILES;
                int correctY = correctPosition/NUM_TILES;

                int currentX = i%NUM_TILES;
                int currentY = i/NUM_TILES;

                Manhattan_Distance += Math.abs(currentX- correctX)
                        + Math.abs(currentY - correctY);
            }
        }
        return Manhattan_Distance + stepNumber;
    }

}
