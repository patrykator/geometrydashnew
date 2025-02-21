package org.example.game.world;

import org.example.game.entities.*;


import java.util.ArrayList;
import java.util.List;

public class LevelData {
    private List<Tile> tiles = new ArrayList<>();
    private List<Spike> spikes = new ArrayList<>();
    private List<Orb> orbs = new ArrayList<>();
    private List<Pad> pads = new ArrayList<>();
    private List<Portal> portals = new ArrayList<>();
    private List<SpeedPortal> speedPortals = new ArrayList<>();
    private List<LevelEnd> levelEnds = new ArrayList<>();
    private List<Checkpoint> checkpoints = new ArrayList<>();
    private boolean isPlatformer;

    public boolean isPlatformer() {
        return isPlatformer;
    }

    public void setPlatformer(boolean platformer) {
        isPlatformer = platformer;
    }


    public List<Tile> getTiles() {
        return tiles;
    }

    public List<SpeedPortal> getSpeedPortals() {
        return speedPortals;
    }

    public void setSpeedPortals(List<SpeedPortal> speedPortals) {
        this.speedPortals = speedPortals;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public void setPortals(List<Portal> portals) {
        this.portals = portals;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public List<LevelEnd> getLevelEnds() {
        return levelEnds;
    }

    public void setLevelEnds(List<LevelEnd> levelEnds) {
        this.levelEnds = levelEnds;
    }

    public List<Spike> getSpikes() {
        return spikes;
    }

    public void setSpikes(List<Spike> spikes) {
        this.spikes = spikes;
    }

    public List<Orb> getOrbs() {
        return orbs;
    }

    public void setOrbs(List<Orb> orbs) {
        this.orbs = orbs;
    }

    public List<Pad> getPads() {
        return pads;
    }

    public void setPads(List<Pad> pads) {
        this.pads = pads;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }
}