package org.example.game.world;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tile {
    private int x;
    private int y;
    private final boolean isSolid;


    @JsonCreator
    public Tile(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("isSolid") boolean isSolid) {
        this.x = x;
        this.y = y;
        this.isSolid = isSolid;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @JsonProperty("isSolid")
    public boolean isSolid() { // Getter remains isSolid()
        return isSolid;
    }
}