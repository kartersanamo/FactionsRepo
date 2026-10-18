package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Faction {
    private String name;
    private String description;
    private int power;
    private int land;
    private int maxPower;
    private Date creationDate;
    private List<Faction> allies;
    private List<Faction> enemies;
    private List<Faction> truces;
    private List<FactionPlayer> members;
    private FactionPlayer leader;

    public Faction(FactionPlayer owner, String name) {
        this.name = name;
        this.description = "";
        this.creationDate = new Date();
        this.allies = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.truces = new ArrayList<>();
        this.members = new ArrayList<>();
        this.members.add(owner);
        this.leader = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getLand() {
        return land;
    }

    public void setLand(int land) {
        this.land = land;
    }

    public int getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<Faction> getAllies() {
        return allies;
    }

    public void setAllies(List<Faction> allies) {
        this.allies = allies;
    }

    public List<Faction> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Faction> enemies) {
        this.enemies = enemies;
    }

    public List<Faction> getTruces() {
        return truces;
    }

    public void setTruces(List<Faction> truces) {
        this.truces = truces;
    }

    public List<FactionPlayer> getMembers() {
        return members;
    }

    public void setMembers(List<FactionPlayer> members) {
        this.members = members;
    }

    public FactionPlayer getLeader() {
        return leader;
    }

    public void setLeader(FactionPlayer leader) {
        this.leader = leader;
    }
}
