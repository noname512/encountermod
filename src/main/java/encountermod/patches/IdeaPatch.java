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
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import encountermod.EncounterMod;
import encountermod.reward.IdeaReward;

import java.util.ArrayList;

public class IdeaPatch {
    public static ArrayList<AbstractGameEffect> topEffect;

    @SpirePatch(clz = AbstractPlayer.class, method = "initializeStarterDeck")
    public static class InitializePatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer _inst) {
            EncounterMod.ideaCount = 1;
            EncounterMod.prob = 3;
            EncounterMod.firstEvent = true;
            SaveData.fromSaveFile = false;
            EncounterMod.isLastOpRefresh = false;
            HorizonEdgePatch.moveCost = 2;
            RefreshPatch.init();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnVictoryPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer _inst) {
            if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
                AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
            }
            else if (AbstractDungeon.miscRng.random(9) < EncounterMod.prob) {
                AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
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
                float INFO_TEXT_Y = Settings.HEIGHT - 24.0F * Settings.scale;
                float CENTER_X = Settings.WIDTH - 690.0F * Settings.scale - ICON_W / 2;
                float CENTER_Y = Settings.HEIGHT - ICON_W / 2;
                sb.draw(EncounterMod.ideaImg, CENTER_X - 32.0F, CENTER_Y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(EncounterMod.ideaCount), Settings.WIDTH - 690.0F * Settings.scale, INFO_TEXT_Y, Color.WHITE);
                EncounterMod.ideaHb.update(); // 偷个懒小孩子不要学（x
                EncounterMod.ideaHb.render(sb);
                if (EncounterMod.ideaHb.hovered) {
                    TipHelper.renderGenericTip(Settings.WIDTH - 750.0F * Settings.scale, Settings.HEIGHT - 120.0F * Settings.scale, EncounterMod.TEXT[2], EncounterMod.TEXT[3]);
                }

                for (AbstractGameEffect e : topEffect) {
                    e.update();
                    e.render(sb);
                }
                topEffect.removeIf(e -> e.isDone);
            }
        }
    }
}
