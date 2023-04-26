package engine.core;

import java.io.Serializable;

import engine.helper.MarioActions;

public class MarioAgentEvent implements Serializable {
    public enum MovementDirection { Left, Right, None }
    public enum GroundState { Crouching, Running, None }
    
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
    public boolean[] getActions() { return this.actions; }
    public void setActions(boolean[] newActions) { this.actions = newActions; }

    public float getMarioX() { return this.marioX; }
    public void setMarioX(float newX) { this.marioX = newX; }

    public float getMarioY() { return this.marioY; }
    public void setMarioY(float newY) { this.marioY = newY; }

    /**
     *  0 = small, 1 = large, 2 = fire power-up.
     */
    public int getMarioState() { return this.marioState; }
    public void setMarioState(int newState) { this.marioState = newState; }

    public boolean getMarioOnGround() { return this.marioOnGround; }
    public void setMarioOnGround(boolean newVal) { this.marioOnGround = newVal; }

    /**
     * Total amount of update ticks at the time of this event.
     */
    public int getTimeSinceStartTicks() { return this.timeTicks; }
    public void setTimeSinceStartTicks(int newTicks) { this.timeTicks = newTicks; }
    
    /**
     * Current time in milliseconds from epoch at the time of this event.
     */
    public long getTimeSinceEpochMillis() { return this.timeMillis; }
    public void setTimeSinceEpochMillis(long newMillis) { this.timeMillis = newMillis; }

    // #region Helper Functions

    public MovementDirection getMovementDirection() {
        if (getActions()[MarioActions.LEFT.getValue()] && !getActions()[MarioActions.RIGHT.getValue()])
            return MovementDirection.Left;

        if (getActions()[MarioActions.RIGHT.getValue()] && !getActions()[MarioActions.LEFT.getValue()])
            return MovementDirection.Right;

        return MovementDirection.None;
    }

    public GroundState getGroundState() {
        // Can only crouch when not small Mario
        // Crouching overrides all movement
        if (getActions()[MarioActions.DOWN.getValue()] && getMarioState() > 0)
            return GroundState.Crouching;

        if (getActions()[MarioActions.SPEED.getValue()])
            return GroundState.Running;

        return GroundState.None;
    }

    // #endregion
}
