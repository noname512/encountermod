package encountermod.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import encountermod.EncounterMod;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class IdeaPatch {

    @SpirePatch(clz = AbstractPlayer.class, method = "initializeStarterDeck")
    public static class InitializePatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer _inst) throws NoSuchFieldException, IllegalAccessException {
            EncounterMod.ideaCount = 0;
            Field floorXField = TopPanel.class.getDeclaredField("floorX");
            floorXField.setAccessible(true);
            float floorX = floorXField.getFloat(_inst);
            float ICON_W = 64.0F * Settings.scale;
            float ICON_Y = Settings.HEIGHT - ICON_W;
            EncounterMod.ideaHb.move(floorX + 250.0F * Settings.scale, ICON_Y + ICON_W / 2.0F);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnVictoryPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer _inst) {
            if (AbstractDungeon.miscRng.random(9) < 3) {
                EncounterMod.ideaCount++;
                // TODO: vfx
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "render")
    public static class RenderTopPanelPatch {
        @SpirePostfixPatch
        public static void Postfix(TopPanel _inst, SpriteBatch sb) throws NoSuchFieldException, IllegalAccessException {
            if (!Settings.hideTopBar) {
                sb.setColor(Color.WHITE);
                Field floorXField = TopPanel.class.getDeclaredField("floorX");
                floorXField.setAccessible(true);
                float floorX = floorXField.getFloat(_inst);
                float ICON_W = 64.0F * Settings.scale;
                float ICON_Y = Settings.HEIGHT - ICON_W;
                float INFO_TEXT_Y = Settings.HEIGHT - 24.0F * Settings.scale;
                Logger.getLogger(IdeaPatch.class.getName()).info("floorX = " + floorX);
                sb.draw(EncounterMod.ideaImg, floorX + 256.0F * Settings.scale, ICON_Y, ICON_W, ICON_W);
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(EncounterMod.ideaCount), floorX + 316.0F * Settings.scale, INFO_TEXT_Y, Color.WHITE);
                EncounterMod.ideaHb.render(sb);
            }
        }
    }
}
