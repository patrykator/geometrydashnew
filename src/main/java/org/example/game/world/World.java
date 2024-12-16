package org.example.game.world;

import org.example.game.entities.*;

import java.util.ArrayList;
import java.util.List;

public class World {
    private List<Tile> tiles;
    private List<Spike> spikes;
    private List<Orb> orbs;
    private static List<Pad> pads;
    private List<Portal> portals = new ArrayList<>();
    private List<SpeedPortal> speedPortals = new ArrayList<>();




    public World() {
        tiles = new ArrayList<>();
        spikes = new ArrayList<>();
        orbs = new ArrayList<>();
        pads = new ArrayList<>();
        portals = new ArrayList<>();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public void addSpeedPortal(SpeedPortal speedPortal) {
        speedPortals.add(speedPortal);
    }

    public List<SpeedPortal> getSpeedPortals() {
        return speedPortals;
    }

    public void addPortal(Portal portal){
        System.out.println("Adding portal: " + portal.getTargetGameMode() + " at " + portal.getX() + ", " + portal.getY()); // Debug
        portals.add(portal);
    }

    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    public List<Spike> getSpikes() {
        return spikes;
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
        levelData.setPads(this.pads);
        levelData.setPortals(this.portals);
        levelData.setSpeedPortals(this.speedPortals);
        return levelData;
    }

    public void loadLevelData(LevelData levelData) {
        this.tiles.clear();
        this.spikes.clear();
        this.orbs.clear();
        pads.clear(); // Dodane czyszczenie listy pads

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

        if(levelData.getPads() != null){
            pads.addAll(levelData.getPads());
        }

        if (levelData.getPortals() != null) {
            this.portals.addAll(levelData.getPortals());
        }

        if (levelData.getSpeedPortals() != null) {
            this.speedPortals.addAll(levelData.getSpeedPortals());
        }
    }


    public double getOriginalGameHeight() {
        double maxHeight = 0;
        for (Tile tile : this.tiles) {
            if (tile.getY() > maxHeight) {
                maxHeight = tile.getY();
            }
        }
        return maxHeight;
    }
}