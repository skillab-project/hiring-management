package com.example.hiringProcess.Analytics;

public class BucketDto {
    private int from;
    private int to;
    private long count;
    private String range;

    public BucketDto() {}

    public BucketDto(int from, int to, long count) {
        this.from = from;
        this.to = to;
        this.count = count;
        recomputeRange();
    }

    public int getFrom() { return from; }
    public void setFrom(int from) { this.from = from; recomputeRange(); }

    public int getTo() { return to; }
    public void setTo(int to) { this.to = to; recomputeRange(); }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }

    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }

    private void recomputeRange() {
        this.range = (this.to == 100) ? (this.from + "–100") : (this.from + "–" + this.to);
    }
}
