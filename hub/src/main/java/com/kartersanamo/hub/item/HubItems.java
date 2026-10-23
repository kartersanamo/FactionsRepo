package com.kartersanamo.hub.item;

import com.kartersanamo.core.item.CustomItem;
import com.kartersanamo.core.item.CustomItems;

public enum HubItems implements CustomItems {
    SERVER_SELECTOR(new ServerSelector());

    private final CustomItem item;

    HubItems(CustomItem item) {
        this.item = item;
    }

    @Override
    public CustomItem item() {
        return item;
    }
}
