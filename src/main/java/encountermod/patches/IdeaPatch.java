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
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import encountermod.EncounterMod;

public class IdeaPatch {
    @SpirePatch(clz = AbstractPlayer.class, method = "initializeStarterDeck")
    public static class InitializePatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer _inst) {
            EncounterMod.ideaCount = 1;
            EncounterMod.prob = 3;
            EncounterMod.firstEvent = true;
            SaveData.fromSaveFile = false;
            EncounterMod.isLastOpRefresh = false;
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnVictoryPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer _inst) {
            if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
                EncounterMod.ideaCount++;
                return;
            }
            if (AbstractDungeon.miscRng.random(9) < EncounterMod.prob) {
                EncounterMod.ideaCount++;
                // TODO: vfx
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "render")
    public static class RenderTopPanelPatch {
        @SpirePostfixPatch
        public static void Postfix(TopPanel _inst, SpriteBatch sb) {
            if (!Settings.hideTopBar) {
                sb.setColor(Color.WHITE);
                float ICON_W = 64.0F * Settings.scale;
                float ICON_Y = Settings.HEIGHT - ICON_W;
                float INFO_TEXT_Y = Settings.HEIGHT - 24.0F * Settings.scale;
                sb.draw(EncounterMod.ideaImg, Settings.WIDTH - 740.0F * Settings.scale, ICON_Y, ICON_W, ICON_W);
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(EncounterMod.ideaCount), Settings.WIDTH - 680.0F * Settings.scale, INFO_TEXT_Y, Color.WHITE);
                EncounterMod.ideaHb.update(); // 偷个懒小孩子不要学（x
                EncounterMod.ideaHb.render(sb);
                if (EncounterMod.ideaHb.hovered) {
                    TipHelper.renderGenericTip(Settings.WIDTH - 740.0F * Settings.scale, Settings.HEIGHT - 120.0F * Settings.scale, EncounterMod.TEXT[2], EncounterMod.TEXT[3]);
                }
            }
        }
    }
}
