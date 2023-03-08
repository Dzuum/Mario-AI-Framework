package custom;

import java.util.ArrayList;

import engine.core.MarioAgentEvent;
import engine.helper.MarioActions;

public class EventRange extends MarioAgentEvent {
    private float endX;
    private float endY;

    private int distance;

    private boolean isStart;
    private boolean isEnd;

    public EventRange(MarioAgentEvent start, MarioAgentEvent end) {
        super(start.getActions(), start.getMarioX(), start.getMarioY(), start.getMarioState(), start.getMarioOnGround(), end.getTimeTicks() - start.getTimeTicks());

        timeMillis = end.getTimeMillis() - start.getTimeMillis();
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
    public int getDistance() {
        return distance;
    }

    /**
     * Set the distance to the previous EventRange.
     */
    public void setDistance(int distance) {
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

    public String getString() {
        String fieldSeparator = ";";
        String actionSeparator = ", ";

        String result = getActionString(actionSeparator);
        result += fieldSeparator + " Ticks: " + getTimeTicks();
        result += fieldSeparator + " Millis: " + getTimeMillis();
        return result;
    }

    // #region Helper Functions

    public boolean hasHorizontalInput() {
        return isMovingLeft() || isMovingRight();
    }

    public boolean isMovingLeft() {
        return actions[MarioActions.LEFT.getValue()];
    }

    public boolean isMovingRight() {
        return actions[MarioActions.RIGHT.getValue()];
    }

    // #endregion
}
