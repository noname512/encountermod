package encountermod.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.TinyHouse;
import encountermod.EncounterMod;


public class EpochalRevision extends AbstractImageEvent {
    public static final String ID = "encountermod:EpochalRevision";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    public int ideaRequire;
    private CurScreen screen = CurScreen.INTRO;
    private enum CurScreen {
        INTRO, LEAVE
    }
    public EpochalRevision() {
        super(NAME, DESCRIPTIONS[0], "resources/encountermod/images/event/EpochalRevision.png");
        if (EncounterMod.ideaCount >= 1) {
            imageEventText.setDialogOption(OPTIONS[0], false);
            imageEventText.setDialogOption(OPTIONS[1], false);
        }
        else {
            imageEventText.setDialogOption(OPTIONS[5] + 1 + OPTIONS[6], true);
            imageEventText.setDialogOption(OPTIONS[5] + 1 + OPTIONS[6], true);
        }
        if (AbstractDungeon.ascensionLevel >= 15) {
            ideaRequire = 4;
        }
        else {
            ideaRequire = 3;
        }
        if (EncounterMod.ideaCount >= ideaRequire) {
            imageEventText.setDialogOption(OPTIONS[2] + ideaRequire + OPTIONS[3], false);
        }
        else {
            imageEventText.setDialogOption(OPTIONS[5] + ideaRequire + OPTIONS[6], true);
        }
        imageEventText.setDialogOption(OPTIONS[4]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (screen == CurScreen.INTRO) {
            imageEventText.updateBodyText(DESCRIPTIONS[buttonPressed + 1]);
            screen = CurScreen.LEAVE;
            if (buttonPressed == 0) {
                EncounterMod.ideaCount --;
                AbstractDungeon.player.increaseMaxHp(10, true);
                CardCrawlGame.sound.play("HEAL_3");
                logMetricMaxHPGain("Fixed Era", "Better", 10);
            }
            else if (buttonPressed == 1) {
                EncounterMod.ideaCount --;
                AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.RARE);
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, relic);
                logMetricObtainRelic("Fixed Era", "Richer", relic);
            }
            else if (buttonPressed == 2) {
                EncounterMod.ideaCount -= ideaRequire;
                AbstractRelic relic = new TinyHouse();
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, relic);
                logMetricObtainRelic("Fixed Era", "Tiny House", relic);
            }
            else {
                logMetricIgnored("Fixed Era");
            }
            imageEventText.updateDialogOption(0, OPTIONS[4]);
            imageEventText.clearRemainingOptions();
        }
        else {
            openMap();
        }
    }
}
