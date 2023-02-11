package net.cassite.hottapcassistant.status;

import net.cassite.hottapcassistant.i18n.I18n;

public enum StatusComponent {
    MACRO(I18n.get().statusComponentMacro(), 20),
    TOOL(I18n.get().statusComponentTool(), 50),
    MODULE(I18n.get().statusComponentModule(), 100),
    ;
    public final String text;
    public final int sortOrder;

    StatusComponent(String text, int sortOrder) {
        this.text = text;
        this.sortOrder = sortOrder;
    }
}
