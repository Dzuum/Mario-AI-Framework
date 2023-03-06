package engine.core;

public class MarioAgentEvent {
    protected boolean[] actions;
    protected float marioX;
    protected float marioY;
    protected int marioState;
    protected boolean marioOnGround;
    protected int timeTicks;
    protected long timeMillis;

    public MarioAgentEvent(boolean[] actions, float marioX, float marioY, int marioState, boolean marioOnGround, int timeTicks) {
        this.actions = actions;
        this.marioX = marioX;
        this.marioY = marioY;
        this.marioState = marioState;
        this.marioOnGround = marioOnGround;
        this.timeTicks = timeTicks;
        this.timeMillis = System.currentTimeMillis();
    }

    /**
     * <pre>
     * Boolean representation of the input.
     * 
     * Indices for the input:
     *   0: Left, 1: Right, 2: Down, 3: Speed, 4: Jump
     * </pre>
     */
    public boolean[] getActions() {
        return this.actions;
    }

    public float getMarioX() {
        return this.marioX;
    }

    public float getMarioY() {
        return this.marioY;
    }

    /**
     *  0 = small, 1 = large, 2 = fire power-up.
     */
    public int getMarioState() {
        return this.marioState;
    }

    public boolean getMarioOnGround() {
        return this.marioOnGround;
    }

    /**
     * Total amount of update ticks at the time of this event.
     */
    public int getTimeTicks() {
        return this.timeTicks;
    }
    
    /**
     * Current time in milliseconds from epoch at the time of this event.
     */
    public long getTimeMillis() {
        return this.timeMillis;
    }
}
