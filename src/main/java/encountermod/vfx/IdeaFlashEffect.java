package encountermod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import encountermod.EncounterMod;

public class IdeaFlashEffect extends AbstractGameEffect {
    private static final float ICON_W = 64.0F * Settings.scale;
    public static final float CENTER_X = Settings.WIDTH - 690.0F * Settings.scale - ICON_W / 2;
    public static final float CENTER_Y = Settings.HEIGHT - ICON_W / 2;

    public IdeaFlashEffect() {
        duration = 1.0F;
        color = Color.WHITE.cpy();
    }

    public void update() {
        scale = Interpolation.exp10In.apply(Settings.scale, 16.0F * Settings.scale, duration);
        if (duration > 0.3F) {
            color.a = Interpolation.pow2.apply(0.4F, 0.05F, this.duration);
        } else {
            color.a = duration * 2.0F;
        }

        duration -= Gdx.graphics.getDeltaTime();

        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.draw(EncounterMod.ideaImg, CENTER_X - 32.0F, CENTER_Y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale, scale, 0.0F, 0, 0, 64, 64, false, false);
    }

    @Override
    public void dispose() {}
}
