package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Faction {
    private String name;
    private List<FactionPlayer> members;

    public Faction(FactionPlayer owner, String name) {
        this.name = name;
        this.members = Collections.singletonList(owner);
    }
}
