package encountermod.vfx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.vfx.MapDot;

import java.util.ArrayList;

public class HorizonMapEdge extends MapEdge {
    private static final float SPACE_X = Settings.isMobile ? (140.8F * Settings.xScale) : (128.0F * Settings.xScale);
    private static final float SPACING = Settings.isMobile ? (40.0F * Settings.xScale) : (34.0F * Settings.xScale);
    private static final float ICON_SRC_RADIUS = 29.0F * Settings.scale;
    private static final float ICON_DST_RADIUS = 20.0F * Settings.scale;
    private final ArrayList<MapDot> dots = new ArrayList<>();
    public HorizonMapEdge revertEdge;
    public HorizonMapEdge(int srcX, int srcY, float srcOffsetX, float srcOffsetY, int dstX, int dstY, float dstOffsetX, float dstOffsetY, boolean generateDots) {
        super(srcX, srcY, dstX, dstY);

        if (generateDots) {
            float tmpSX = getX(srcX) + srcOffsetX;
            float tmpDX = getX(dstX) + dstOffsetX;
            float tmpSY = srcY * Settings.MAP_DST_Y + srcOffsetY;
            float tmpDY = dstY * Settings.MAP_DST_Y + dstOffsetY;

            Vector2 vec2 = (new Vector2(tmpDX, tmpDY)).sub(new Vector2(tmpSX, tmpSY));
            float length = vec2.len();
            float START = SPACING * MathUtils.random() / 2.0F;

            float tmpRadius = ICON_DST_RADIUS;
            float i;
            for (i = START + tmpRadius; i < length - ICON_SRC_RADIUS; i += SPACING) {
                vec2.clamp(length - i, length - i);
                if (i != START + tmpRadius && i <= length - ICON_SRC_RADIUS - SPACING) {
                    dots.add(new HorizonMapDot(tmpSX + vec2.x, tmpSY + vec2.y, (new Vector2(tmpSX - tmpDX, tmpSY - tmpDY)).nor().angle() + 90.0F, true));
                } else {
                    dots.add(new HorizonMapDot(tmpSX + vec2.x, tmpSY + vec2.y, (new Vector2(tmpSX - tmpDX, tmpSY - tmpDY)).nor().angle() + 90.0F, false));
                }
            }
        }
    }

    private static float getX(int x) {
        return x * SPACE_X + MapRoomNode.OFFSET_X;
    }

    @Override
    public void markAsTaken() {
        taken = true;
        color = MapRoomNode.AVAILABLE_COLOR;
        if (!revertEdge.taken) {
            revertEdge.markAsTaken();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        for (MapDot d : this.dots)
            d.render(sb);
    }
}
