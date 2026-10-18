package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Faction {
    private final UUID id;
    private String name;
    private String description;
    private int power;
    private int maxPower;
    private Date creationDate;
    private List<Faction> allies;
    private List<Faction> enemies;
    private List<Faction> truces;
    private List<FactionPlayer> members;
    private FactionPlayer leader;
    private Set<String> claims;

    public Faction(UUID id, FactionPlayer owner, String name) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.creationDate = new Date();
        this.allies = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.truces = new ArrayList<>();
        this.members = new ArrayList<>();
        this.claims = new HashSet<>();
        owner.setRole(FactionRole.LEADER);
        this.members.add(owner);
        this.leader = owner;
    }

    public UUID getId() {
        return id;
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
        return claims.size();
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

    public Set<String> getClaims() {
        return claims;
    }

    public void setClaims(Set<String> claims) {
        this.claims = claims;
    }

    public FactionPlayer getMember(UUID playerId) {
        for (FactionPlayer member : members) {
            if (member.getUniqueId().equals(playerId)) {
                return member;
            }
        }
        return null;
    }

    public boolean isMember(UUID playerId) {
        return getMember(playerId) != null;
    }

    public void addMember(FactionPlayer member) {
        member.setRole(FactionRole.MEMBER);
        members.add(member);
    }

    public void removeMember(UUID playerId) {
        members.removeIf(member -> member.getUniqueId().equals(playerId));
    }
}
