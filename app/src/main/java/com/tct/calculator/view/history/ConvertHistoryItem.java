package com.tct.calculator.view.history;

/**
 */
public class ConvertHistoryItem {
    public String fromName;
    public String toName;
    public int imgId;
    public long timeStamp;
    public String convertName;

    public ConvertHistoryItem(String convertName, String fromName, String toName, int imgId, long timeStap) {
        this.convertName = convertName;
        this.fromName = fromName;
        this.toName = toName;
        this.imgId = imgId;
        this.timeStamp = timeStap;
    }

    public ConvertHistoryItem() {
    }

    @Override
    public String toString() {
        return "ConvertHistoryItem:convertName=" + convertName + ",fromName=" + fromName + ",toName=" + toName + ",timeStamp=" + timeStamp + ",imgId=" + imgId;
    }
}
