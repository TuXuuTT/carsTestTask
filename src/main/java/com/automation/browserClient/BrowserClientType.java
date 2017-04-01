package com.automation.browserClient;

public enum BrowserClientType {
    IE("ie"),
    FF("ff"),
    GC("gc");

    private String browserClientName;

    private BrowserClientType(final String browserClientName) {
        this.browserClientName = browserClientName;
    }

    @Override
    public String toString() {
        return browserClientName;
    }
}
