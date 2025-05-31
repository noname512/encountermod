package encountermod.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.TinyHouse;
import encountermod.EncounterMod;
import encountermod.relics.*;

import java.util.ArrayList;
import java.util.List;


public class LostAndFound extends AbstractImageEvent {
    public static final String ID = "encountermod:Lost And Found";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private CurScreen screen = CurScreen.INTRO;
    AbstractRelic owned = null;
    ArrayList<String> rest;
    private enum CurScreen {
        INTRO, CHOICE, LEAVE
    }
    public LostAndFound() {
        super(NAME, DESCRIPTIONS[0], "resources/encountermod/images/event/LostAndFound.png");
        if (EncounterMod.ideaCount >= 1) {
            if (AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)) {
                imageEventText.setDialogOption(OPTIONS[1], false);
            }
            else {
                imageEventText.setDialogOption(OPTIONS[0], false);
            }
        }
        else {
            imageEventText.setDialogOption(OPTIONS[2], true);
        }
        if (AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)) {
            owned = AbstractDungeon.player.getRelic(GraffitiOfTheEraOfHope.ID);
        }
        if (AbstractDungeon.player.hasRelic(HatredOfTheEraOfVendetta.ID)) {
            owned = AbstractDungeon.player.getRelic(HatredOfTheEraOfVendetta.ID);
        }
        if (AbstractDungeon.player.hasRelic(LongingOfTheEraOfDreams.ID)) {
            owned = AbstractDungeon.player.getRelic(LongingOfTheEraOfDreams.ID);
        }
        if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
            owned = AbstractDungeon.player.getRelic(VisionsOfTheEraOfProsperity.ID);
        }
        if (AbstractDungeon.player.hasRelic(SufferingOfTheEraOfCatastrophe.ID)) {
            owned = AbstractDungeon.player.getRelic(SufferingOfTheEraOfCatastrophe.ID);
        }
        rest = new ArrayList<>();
        rest.add(GraffitiOfTheEraOfHope.ID);
        rest.add(HatredOfTheEraOfVendetta.ID);
        rest.add(LongingOfTheEraOfDreams.ID);
        rest.add(VisionsOfTheEraOfProsperity.ID);
        rest.add(SufferingOfTheEraOfCatastrophe.ID);
        if (owned == null) {
            imageEventText.setDialogOption(OPTIONS[3], true);
            imageEventText.setDialogOption(OPTIONS[3], true);
        }
        else {
            rest.remove(owned.relicId);
            imageEventText.setDialogOption(OPTIONS[5] + owned.name + OPTIONS[6], false);
            if (EncounterMod.ideaCount >= 1)
            {
                imageEventText.setDialogOption(OPTIONS[7] + owned.name + OPTIONS[8], false);
            }
            else {
                imageEventText.setDialogOption(OPTIONS[2], true);
            }
        }
        imageEventText.setDialogOption(OPTIONS[9]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (screen == CurScreen.INTRO) {
            imageEventText.updateBodyText(DESCRIPTIONS[buttonPressed + 1]);
            screen = CurScreen.LEAVE;
            if (buttonPressed == 0) {
                EncounterMod.ideaCount --;
                AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, relic);
                if (AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)) {
                    AbstractRelic nextRelic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, nextRelic);
                    List<String> tempList = new ArrayList();
                    tempList.add(relic.relicId);
                    tempList.add(nextRelic.relicId);
                    logMetric(ID, "Get Two Relics", (List)null, (List)null, (List)null, (List)null, tempList, (List)null, (List)null, 0, 0, 0, 0, 0, 0);
                }
                else {
                    logMetricObtainRelic(ID, "Get Relic", relic);
                }
            }
            else if (buttonPressed == 1) {
                AbstractDungeon.player.loseRelic(owned.relicId);
                AbstractRelic get = RelicLibrary.getRelic(rest.get(AbstractDungeon.eventRng.random(rest.size() - 1))).makeCopy();
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, get);
                logMetricRelicSwap(ID, "Swap Randomly", owned, get);
            }
            else if (buttonPressed == 2) {
                EncounterMod.ideaCount --;
                AbstractDungeon.player.loseRelic(owned.relicId);
                screen = CurScreen.CHOICE;
                for (int i = 0; i < 4; i++) {
                    imageEventText.updateDialogOption(i, OPTIONS[10] + RelicLibrary.getRelic(rest.get(i)).name + OPTIONS[11]);
                }
            }
            else {
                logMetricIgnored(ID);
            }
            if (screen == CurScreen.LEAVE) {
                imageEventText.updateDialogOption(0, OPTIONS[4]);
                imageEventText.clearRemainingOptions();
            }
        }
        else if (screen == CurScreen.CHOICE) {
            AbstractRelic get = RelicLibrary.getRelic(rest.get(buttonPressed)).makeCopy();
            logMetricRelicSwap(ID, "Swap Manually", owned, get);
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, get);
            screen = CurScreen.LEAVE;
            imageEventText.updateBodyText(DESCRIPTIONS[5]);
            imageEventText.updateDialogOption(0, OPTIONS[4]);
            imageEventText.clearRemainingOptions();
        }
        else {
            openMap();
        }
    }
}
