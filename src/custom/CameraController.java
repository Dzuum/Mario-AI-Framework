package custom;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import engine.core.MarioGame;
import engine.core.MarioWorld;

public class CameraController extends KeyAdapter {
    private MarioGame game;
    private MarioWorld world;

    private float movementSpeed = 32f;

    public CameraController(MarioGame game, MarioWorld world) {
        this.game = game;
        this.world = world;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
    }

    private void toggleKey(int keyCode, boolean isPressed) {
        switch (keyCode) {
            case KeyEvent.VK_P:
                game.pause = !game.pause;

                break;
            case KeyEvent.VK_LEFT:
                if (game.pause) {
                    world.cameraX -= movementSpeed;
                }

                break;
            case KeyEvent.VK_RIGHT:
                if (game.pause) {
                    world.cameraX += movementSpeed;
                }

                break;
            case KeyEvent.VK_UP:
                if (game.pause) {
                    world.cameraY -= movementSpeed;
                }

                break;
            case KeyEvent.VK_DOWN:
                if (game.pause) {
                    world.cameraY += movementSpeed;
                }

                break;
        }
    }
}
