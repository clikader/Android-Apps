package com.clikader.newsgateway;

public class Source {
    private static final String TAG = "Source";

    private String sourceId;
    private String sourceName;

    public Source(String id, String name) {
        this.sourceId = id;
        this.sourceName = name;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return sourceId + sourceName;
    }
}
