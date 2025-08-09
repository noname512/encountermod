package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.JuzuBracelet;
import com.megacrit.cardcrawl.relics.TinyChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import encountermod.EncounterMod;
import encountermod.patches.IdeaPatch;
import encountermod.patches.RefreshPatch;
import encountermod.vfx.IdeaFlashEffect;
import jdk.internal.org.jline.reader.impl.DefaultParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GraffitiOfTheEraOfHope extends CustomRelic{

    public static final String ID = "encountermod:GraffitiOfTheEraOfHope";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/GraffitiOfTheEraOfHope.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/GraffitiOfTheEraOfHope_p.png");
    public GraffitiOfTheEraOfHope() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        EncounterMod.ideaCount += 2;
        IdeaPatch.topEffect.add(new IdeaFlashEffect());
        RefreshPatch.roomWeight.put("Monster", 2);
        RefreshPatch.roomWeight.put("Elite", 0);
        RefreshPatch.totalWeight = 9;
        if (AbstractDungeon.player.hasRelic(JuzuBracelet.ID)) {
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).description = DESCRIPTIONS[1];
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).counter = 0;
            RefreshPatch.roomWeight.put("Event", 8);
            RefreshPatch.totalWeight = 15;
            refreshTips(AbstractDungeon.player.getRelic(JuzuBracelet.ID));
        }
        if (AbstractDungeon.player.hasRelic(TinyChest.ID)) {
            AbstractDungeon.player.getRelic(TinyChest.ID).description = DESCRIPTIONS[2];
            refreshTips(AbstractDungeon.player.getRelic(TinyChest.ID));
        }
    }

    void refreshTips(AbstractRelic r) {
        try {
            r.tips.clear();
            r.tips.add(new PowerTip(r.name, r.description));
            Method method = AbstractRelic.class.getDeclaredMethod("initializeTips");
            method.setAccessible(true);
            method.invoke(r);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if ((room instanceof EventRoom) && (AbstractDungeon.player.hasRelic(JuzuBracelet.ID))) {
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).flash();
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).counter ++;
        }
        if ((room instanceof EventRoom) && (AbstractDungeon.player.hasRelic(TinyChest.ID))) {
            AbstractDungeon.player.getRelic(TinyChest.ID).flash();
            AbstractDungeon.player.getRelic(TinyChest.ID).counter ++;
            if (AbstractDungeon.player.getRelic(TinyChest.ID).counter == 4) {
                AbstractDungeon.player.getRelic(TinyChest.ID).counter = 0;
            }
        }
    }

    @Override
    public void atPreBattle() {
        if (AbstractDungeon.player.hasRelic(JuzuBracelet.ID)) {
            int amount = AbstractDungeon.player.getRelic(JuzuBracelet.ID).counter / 4;
            if (amount > 0) {
                AbstractDungeon.player.getRelic(JuzuBracelet.ID).flash();
                addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, amount)));
                addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, amount)));
            }
        }
    }

    @Override
    public void onUnequip() {
        RefreshPatch.roomWeight.put("Monster", 4);
        RefreshPatch.roomWeight.put("Elite", 1);
        RefreshPatch.roomWeight.put("Event", 2);
        RefreshPatch.totalWeight = 12;
        if (AbstractDungeon.player.hasRelic(JuzuBracelet.ID)) {
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).description = AbstractDungeon.player.getRelic(JuzuBracelet.ID).getUpdatedDescription();
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).counter = -1;
            refreshTips(AbstractDungeon.player.getRelic(JuzuBracelet.ID));
        }
        if (AbstractDungeon.player.hasRelic(TinyChest.ID)) {
            AbstractDungeon.player.getRelic(TinyChest.ID).description = AbstractDungeon.player.getRelic(TinyChest.ID).getUpdatedDescription();
            refreshTips(AbstractDungeon.player.getRelic(TinyChest.ID));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new GraffitiOfTheEraOfHope();
    }

    @SpirePatch(clz = EventHelper.class, method = "roll", paramtypez = {Random.class})
    public static class RollEventPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(Random eventRng) {
            if (AbstractDungeon.player.hasRelic(ID)) {
                if (AbstractDungeon.player.hasRelic(TinyChest.ID) && AbstractDungeon.player.getRelic(TinyChest.ID).counter == 0) {
                    AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, relic);
                }
                return SpireReturn.Return(EventHelper.RoomResult.EVENT);
            } else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = AbstractRelic.class, method = "onEquip")
    public static class EquipPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(AbstractRelic __instance) {
            if (AbstractDungeon.player.hasRelic(ID)) {
                if (__instance instanceof JuzuBracelet) {
                    __instance.counter = 0;
                    RefreshPatch.roomWeight.put("Event", 8);
                    RefreshPatch.totalWeight = 15;
                }
            }
            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = JuzuBracelet.class, method = "makeCopy")
    public static class JuzuMakeCopyPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(AbstractRelic __instance) {
            if ((AbstractDungeon.player != null) && (AbstractDungeon.player.hasRelic(ID)) ||
                (CardCrawlGame.saveFile != null) && (CardCrawlGame.saveFile.relics != null) && (CardCrawlGame.saveFile.relics.contains(ID))) {
                JuzuBracelet juzu = new JuzuBracelet();
                juzu.description = DESCRIPTIONS[1];
                try {
                    juzu.tips.clear();
                    juzu.tips.add(new PowerTip(juzu.name, juzu.description));
                    Method method = AbstractRelic.class.getDeclaredMethod("initializeTips");
                    method.setAccessible(true);
                    method.invoke(juzu);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return SpireReturn.Return(juzu);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = TinyChest.class, method = "makeCopy")
    public static class TinyChestMakeCopyPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(AbstractRelic __instance) {
            if (((AbstractDungeon.player != null) && (AbstractDungeon.player.hasRelic(ID))) ||
                (CardCrawlGame.saveFile != null) && (CardCrawlGame.saveFile.relics != null) && (CardCrawlGame.saveFile.relics.contains(ID))) {
                TinyChest tiny = new TinyChest();
                tiny.description = DESCRIPTIONS[2];
                try {
                    tiny.tips.clear();
                    tiny.tips.add(new PowerTip(tiny.name, tiny.description));
                    Method method = AbstractRelic.class.getDeclaredMethod("initializeTips");
                    method.setAccessible(true);
                    method.invoke(tiny);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return SpireReturn.Return(tiny);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }
}
