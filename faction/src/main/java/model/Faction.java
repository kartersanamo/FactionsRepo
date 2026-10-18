package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Faction {
    private final UUID id;
    private String name;
    private String description;
    private String rules;
    private int power;
    private int maxPower;
    private int powerBoost;
    private int points;
    private int tnt;
    private int strikes;
    private int maxVaults;
    private double money;
    private boolean open;
    private boolean peaceful;
    private boolean permanent;
    private boolean grace;
    private Date creationDate;
    private Set<UUID> allyIds;
    private Set<UUID> enemyIds;
    private Set<UUID> truceIds;
    private Set<UUID> banned;
    private List<FactionPlayer> members;
    private FactionPlayer leader;
    private FactionRole defaultRank;
    private Set<String> claims;
    private String baseClaimKey;
    private NamedLocation home;
    private Map<String, NamedLocation> warps;

    public Faction(UUID id, FactionPlayer owner, String name) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.rules = "";
        this.creationDate = new Date();
        this.allyIds = new HashSet<>();
        this.enemyIds = new HashSet<>();
        this.truceIds = new HashSet<>();
        this.banned = new HashSet<>();
        this.members = new ArrayList<>();
        this.claims = new HashSet<>();
        this.warps = new HashMap<>();
        this.defaultRank = FactionRole.MEMBER;
        this.maxVaults = 1;
        this.maxPower = 10;
        this.power = 10;
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
        this.description = description == null ? "" : description;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules == null ? "" : rules;
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

    public int getPowerBoost() {
        return powerBoost;
    }

    public void setPowerBoost(int powerBoost) {
        this.powerBoost = powerBoost;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTnt() {
        return tnt;
    }

    public void setTnt(int tnt) {
        this.tnt = Math.max(0, tnt);
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = Math.max(0, strikes);
    }

    public int getMaxVaults() {
        return maxVaults;
    }

    public void setMaxVaults(int maxVaults) {
        this.maxVaults = Math.max(0, maxVaults);
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isPeaceful() {
        return peaceful;
    }

    public void setPeaceful(boolean peaceful) {
        this.peaceful = peaceful;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isGrace() {
        return grace;
    }

    public void setGrace(boolean grace) {
        this.grace = grace;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<UUID> getAllyIds() {
        return allyIds;
    }

    public void setAllyIds(Set<UUID> allyIds) {
        this.allyIds = allyIds;
    }

    public Set<UUID> getEnemyIds() {
        return enemyIds;
    }

    public void setEnemyIds(Set<UUID> enemyIds) {
        this.enemyIds = enemyIds;
    }

    public Set<UUID> getTruceIds() {
        return truceIds;
    }

    public void setTruceIds(Set<UUID> truceIds) {
        this.truceIds = truceIds;
    }

    public Set<UUID> getBanned() {
        return banned;
    }

    public void setBanned(Set<UUID> banned) {
        this.banned = banned;
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

    public FactionRole getDefaultRank() {
        return defaultRank;
    }

    public void setDefaultRank(FactionRole defaultRank) {
        this.defaultRank = defaultRank;
    }

    public Set<String> getClaims() {
        return claims;
    }

    public void setClaims(Set<String> claims) {
        this.claims = claims;
    }

    public String getBaseClaimKey() {
        return baseClaimKey;
    }

    public void setBaseClaimKey(String baseClaimKey) {
        this.baseClaimKey = baseClaimKey;
    }

    public NamedLocation getHome() {
        return home;
    }

    public void setHome(NamedLocation home) {
        this.home = home;
    }

    public Map<String, NamedLocation> getWarps() {
        return warps;
    }

    public void setWarps(Map<String, NamedLocation> warps) {
        this.warps = warps;
    }

    public FactionPlayer getMember(UUID playerId) {
        for (FactionPlayer member : members) {
            if (member.getUniqueId().equals(playerId)) {
                return member;
            }
        }
        return null;
    }

    public FactionPlayer getMemberByName(String name) {
        for (FactionPlayer member : members) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }

    public boolean isMember(UUID playerId) {
        return getMember(playerId) != null;
    }

    public void addMember(FactionPlayer member) {
        if (member.getRole() == null) {
            member.setRole(defaultRank == FactionRole.LEADER ? FactionRole.MEMBER : defaultRank);
        }
        members.add(member);
        recalculatePower();
    }

    public void removeMember(UUID playerId) {
        members.removeIf(member -> member.getUniqueId().equals(playerId));
        recalculatePower();
    }

    public void recalculatePower() {
        this.maxPower = members.size() * 10 + powerBoost;
        this.power = Math.min(power, maxPower);
        if (power < members.size() * 10) {
            this.power = Math.min(members.size() * 10 + powerBoost, maxPower);
        }
    }
}
