package com.edifecs.xboard.portal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppBar implements Serializable {
    private static final long serialVersionUID = 91;

    private final List<AppBarButton> buttons = new ArrayList<>();
    
    public AppBar(AppBarButton... buttons) {
        for (AppBarButton button : buttons)
            this.buttons.add(button);
    }
    
    public AppBar addButton(AppBarButton button) {
        buttons.add(button);
        return this;
    }
    
    public List<AppBarButton> getButtons() {
        return buttons;
    }
}
