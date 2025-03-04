package main.database;

public final class Stats {
    private String name;
    private int remainedTime;
    private String repeat;
    private boolean shuffle;
    private boolean paused;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getRemainedTime() {
        return remainedTime;
    }

    public void setRemainedTime(final int remainedTime) {
        this.remainedTime = remainedTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(final String repeat) {
        this.repeat = repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
}
