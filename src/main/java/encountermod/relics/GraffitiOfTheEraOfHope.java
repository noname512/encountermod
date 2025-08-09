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
import com.megacrit.cardcrawl.localization.RelicStrings;
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
        }
        if (AbstractDungeon.player.hasRelic(TinyChest.ID)) {
            AbstractDungeon.player.getRelic(TinyChest.ID).description = DESCRIPTIONS[2];
        }
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        /* 这一部分是暂时用来解决这俩遗物没变化的 */
        if (AbstractDungeon.player.hasRelic(JuzuBracelet.ID)) {
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).description = DESCRIPTIONS[1];
            if (AbstractDungeon.player.getRelic(JuzuBracelet.ID).counter == -1) {
                AbstractDungeon.player.getRelic(JuzuBracelet.ID).counter = 0;
            }
            RefreshPatch.roomWeight.put("Event", 8);
            RefreshPatch.totalWeight = 15;
        }
        if (AbstractDungeon.player.hasRelic(TinyChest.ID)) {
            AbstractDungeon.player.getRelic(TinyChest.ID).description = DESCRIPTIONS[2];
        }
        /* TODO: 把上面那段变成获取时触发，并更新它们的 Tips */
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
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, amount)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, amount)));
        }
    }

    @Override
    public void onUnequip() {
        RefreshPatch.roomWeight.put("Monster", 4);
        RefreshPatch.roomWeight.put("Elite", 1);
        RefreshPatch.roomWeight.put("Event", 2);
        RefreshPatch.totalWeight = 12;
        if (AbstractDungeon.player.hasRelic(JuzuBracelet.ID)) {
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).description = CardCrawlGame.languagePack.getRelicStrings(JuzuBracelet.ID).DESCRIPTIONS[0];
            AbstractDungeon.player.getRelic(JuzuBracelet.ID).counter = -1;
        }
        if (AbstractDungeon.player.hasRelic(TinyChest.ID)) {
            AbstractDungeon.player.getRelic(TinyChest.ID).description = CardCrawlGame.languagePack.getRelicStrings(TinyChest.ID).DESCRIPTIONS[0];
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
}
