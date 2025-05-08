package encountermod.vfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.vfx.MapDot;

public class HorizonMapDot extends MapDot {
    private final float x;
    private final float y;
    private final float rotation;
    private static final float OFFSET_Y = 172.0F * Settings.scale;
    private static final Texture MY_MAP_DOT = ImageMaster.loadImage("resources/encountermod/images/ui/dot.png");
    public HorizonMapDot(float x, float y, float rotation, boolean jitter) {
        super(x, y, rotation, jitter);
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.draw(MY_MAP_DOT, this.x - 8.0F, this.y - 8.0F + DungeonMapScreen.offsetY + OFFSET_Y, 8.0F, 8.0F, 16.0F, 16.0F, Settings.scale, Settings.scale, this.rotation, 0, 0, 16, 16, false, false);
    }
}
