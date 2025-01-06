package org.example.game.world;

import org.example.game.entities.*;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final List<Tile> tiles;
    private final List<Spike> spikes;
    private final List<Orb> orbs;
    private static List<Pad> pads;
    private final List<Portal> portals;
    private final List<SpeedPortal> speedPortals = new ArrayList<>();
    private boolean isPlatformer;
    private final List<LevelEnd> levelEnds;
    private final List<Checkpoint> checkpoints;




    public World() {
        tiles = new ArrayList<>();
        spikes = new ArrayList<>();
        orbs = new ArrayList<>();
        pads = new ArrayList<>();
        portals = new ArrayList<>();
        isPlatformer = false;
        levelEnds = new ArrayList<>();
        checkpoints = new ArrayList<>();
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public void addLevelEnd(LevelEnd levelEnd) {
        levelEnds.add(levelEnd);
    }

    public List<LevelEnd> getLevelEnds() {
        return levelEnds;
    }



    public void addSpeedPortal(SpeedPortal speedPortal) {
        speedPortals.add(speedPortal);
    }

    public List<SpeedPortal> getSpeedPortals() {
        return speedPortals;
    }

    public void addPortal(Portal portal){
        portals.add(portal);
    }

    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    public List<Spike> getSpikes() {
        return spikes;
    }

    public boolean isPlatformer() {
        return isPlatformer;
    }

    public void setPlatformer(boolean platformer) {
        isPlatformer = platformer;
    }

    public void addSpike(Spike spike) {
        spikes.add(spike);
    }

    public List<Orb> getOrbs() {
        return orbs;
    }

    public void addOrb(Orb orb) {
        orbs.add(orb);
    }

    public void addPad(Pad pad) {
        pads.add(pad);
    }

    public static List<Pad> getPads() {
        return pads;
    }


    public LevelData toLevelData() {
        LevelData levelData = new LevelData();
        levelData.setTiles(this.tiles);
        levelData.setSpikes(this.spikes);
        levelData.setOrbs(this.orbs);
        levelData.setPads(pads);
        levelData.setPortals(this.portals);
        levelData.setSpeedPortals(this.speedPortals);
        levelData.setLevelEnds(this.levelEnds);
        levelData.setPlatformer(this.isPlatformer);
        levelData.setCheckpoints(this.checkpoints);
        return levelData;
    }

    public void loadLevelData(LevelData levelData) {
        this.tiles.clear();
        this.spikes.clear();
        this.orbs.clear();
        pads.clear();
        portals.clear();
        speedPortals.clear();
        this.checkpoints.clear();

        if(levelData == null) return;

        if (levelData.getTiles() != null) {
            this.tiles.addAll(levelData.getTiles());
        }
        if (levelData.getSpikes() != null) {
            this.spikes.addAll(levelData.getSpikes());
        }
        if (levelData.getOrbs() != null) {
            this.orbs.addAll(levelData.getOrbs());
        }

        if (levelData.getLevelEnds() != null) {
            this.levelEnds.addAll(levelData.getLevelEnds());
        }

        if(levelData.getPads() != null){
            pads.addAll(levelData.getPads());
        }

        if (levelData.getPortals() != null) {
            this.portals.addAll(levelData.getPortals());
        }

        if (levelData.getSpeedPortals() != null) {
            this.speedPortals.addAll(levelData.getSpeedPortals());
        }

        if (levelData.getCheckpoints() != null) {
            this.checkpoints.addAll(levelData.getCheckpoints());
        }

        this.isPlatformer = levelData.isPlatformer();
    }
}