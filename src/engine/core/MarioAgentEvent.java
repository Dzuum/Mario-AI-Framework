package engine.core;

import engine.helper.MarioActions;

public class MarioAgentEvent {
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
    public int getTimeSinceStartTicks() {
        return this.timeTicks;
    }
    
    /**
     * Current time in milliseconds from epoch at the time of this event.
     */
    public long getTimeSinceEpochMillis() {
        return this.timeMillis;
    }

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
