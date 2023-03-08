package custom;

import java.awt.Graphics;

import engine.core.MarioSprite;
import engine.graphics.MarioImage;
import engine.helper.Assets;
import engine.helper.SpriteType;

public class GestaltSprite extends MarioSprite {
    private MarioImage startSprite;
    private MarioImage endSprite;

    private float startX;
    private float endX;

    public GestaltSprite(float startX, float endX, float y) {
        super(startX, y, SpriteType.BULLET_BILL);

        this.width = 4;
        this.height = 12;
        this.ya = -5;

        this.startX = startX;
        this.endX = endX;

        this.startSprite = new MarioImage(Assets.enemies, 40);
        this.startSprite.originX = 8;
        this.startSprite.originY = 31;
        this.startSprite.width = 16;
        this.startSprite.flipX = false;

        this.endSprite = new MarioImage(Assets.enemies, 40);
        this.endSprite.originX = 8;
        this.endSprite.originY = 31;
        this.endSprite.width = 16;
        this.endSprite.flipX = true;

        // Move to middle of tile
        this.startX += startSprite.width / 2;
        this.endX += endSprite.width / 2;
        this.y += 15;
    }

    @Override
    public MarioSprite clone() {
        GestaltSprite sprite = new GestaltSprite(startX, endX, y);
        sprite.xa = this.xa;
        sprite.ya = this.ya;
        sprite.width = this.width;
        sprite.height = this.height;
        return sprite;
    }

    @Override
    public void render(Graphics og) {
        super.render(og);

        this.startSprite.render(og, (int) (this.startX - this.world.cameraX), (int) (this.y - this.world.cameraY));
        this.endSprite.render(og, (int) (this.endX - this.world.cameraX), (int) (this.y - this.world.cameraY));
    }
}
