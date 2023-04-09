package custom;

import java.util.ArrayList;
import java.util.List;

import engine.core.MarioAgentEvent;
import engine.helper.MarioActions;

import custom.Settings.TimeScoring;

public class EventRange extends MarioAgentEvent {
    private float endX;
    private float endY;

    private double distance;

    private boolean isStart;
    private boolean isEnd;

    private List<MarioAgentEvent> events = new ArrayList<MarioAgentEvent>();
    
    public EventRange(MarioAgentEvent start, MarioAgentEvent end) {
        super(start.getActions(), start.getMarioX(), start.getMarioY(),
            start.getMarioState(), start.getMarioOnGround(),
            end.getTimeSinceStartTicks() - start.getTimeSinceStartTicks() + 1);

        timeMillis = end.getTimeSinceEpochMillis() - start.getTimeSinceEpochMillis();
        endX = end.getMarioX();
        endY = end.getMarioY();

        distance = 0;

        isStart = false;
        isEnd = false;       
    }

    // #region Get/Set

    public float getEndX() {
        return endX;
    }

    public float getEndY() {
        return endY;
    }

    /**
     * Get the distance to the previous EventRange.
     */
    public double getDistanceGMA() {
        return distance;
    }

    /**
     * Set the distance to the previous EventRange.
     */
    public void setDistanceGMA(double distance) {
        this.distance = distance;
    }

    public boolean isStartBoundary() {
        return isStart;
    }

    public void setStartBoundary() {
        isStart = true;
    }

    public boolean isEndBoundary() {
        return isEnd;
    }

    public void setEndBoundary() {
        isEnd = true;
    }

    public int getDurationTicks() {
        // Calculated in constructor already
        return getTimeSinceStartTicks();
    }

    public long getDurationMillis() {
        // Calculated in constructor already
        return getTimeSinceEpochMillis();
    }

    public List<MarioAgentEvent> getAgentEvents() {
        return events;
    }

    public void setAgentEvents(List<MarioAgentEvent> events) {
        this.events = events;
    }
    
    // #endregion

    /**
     * String representation of the input.
     */
    public String getActionString(String separator) {
        // LEFT(0, "Left"),
        // RIGHT(1, "Right"),
        // DOWN(2, "Down"),
        // SPEED(3, "Speed"),
        // JUMP(4, "Jump");

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < actions.length; i++) {
            if (actions[i]) {
                result.add(MarioActions.getAction(i).getString());
            }
        }

        return String.join(separator, result);
    }

    public String getStateString() {
        String result = "";

        // 1. Movement Direction
        if (getMovementDirection() != MovementDirection.None)
            result += getMovementDirection() + " ";

        // 2. Powerup State
        if (getMarioState() == 0)
            result += "Small ";
        else if (getMarioState() == 1)
            result += "Large ";
        else if (getMarioState() == 2)
            result += "Fire ";

        // 3. Ground State
        if (getGroundState() != GroundState.None)
            result += getGroundState().toString() + " ";

        // 4. Airborne State
        if (!getMarioOnGround())
            result += "Airborne ";

        // 5. Time
        if (Settings.StateTimeScoring == TimeScoring.Millis)
            result += "|| Duration: " + getDurationMillis() + " ms";
        else
            result += "|| Duration: " + getDurationTicks() + " ticks";

        return result;
    }
}
