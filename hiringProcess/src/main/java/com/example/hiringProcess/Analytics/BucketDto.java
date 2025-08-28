package com.example.hiringProcess.Analytics;

public class BucketDto {
    private int from;      // inclusive (0, 10, 20, …, 90)
    private int to;        // exclusive for all εκτός του 100 (10, 20, …, 100)
    private long count;    // πλήθος υποψηφίων στο bucket
    private String range;  // "0–10", "10–20", …, "90–100"

    public BucketDto() {}

    public BucketDto(int from, int to, long count) {
        this.from = from;
        this.to = to;
        this.count = count;
        this.range = from + "–" + to;
    }

    public int getFrom() { return from; }
    public void setFrom(int from) { this.from = from; }
    public int getTo() { return to; }
    public void setTo(int to) { this.to = to; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }
}