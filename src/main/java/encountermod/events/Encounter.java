package encountermod.events;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.EncounterMod;
import encountermod.relics.*;

public class Encounter extends AbstractImageEvent {
    public static final String ID = "encountermod:Encounter";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private CurScreen screen = CurScreen.INTRO;
    private enum CurScreen {
        INTRO, LEAVE
    }
    public Encounter() {
        super(NAME, DESCRIPTIONS[0], "resources/encountermod/images/event/Encounter.png");
        imageEventText.setDialogOption(OPTIONS[0], false, new GraffitiOfTheEraOfHope());
        imageEventText.setDialogOption(OPTIONS[1], false, new HatredOfTheEraOfVendetta());
        imageEventText.setDialogOption(OPTIONS[2], false, new LongingOfTheEraOfDreams());
        imageEventText.setDialogOption(OPTIONS[3], false, new VisionsOfTheEraOfProsperity());
        imageEventText.setDialogOption(OPTIONS[4], false, new SufferingOfTheEraOfCatastrophe());
//        imageEventText.setDialogOption(OPTIONS[5]);
    }

    @Override
    public void onEnterRoom() {
        EncounterMod.firstEvent = false;
        AbstractDungeon.eventList.remove(ID);
        AbstractDungeon.specialOneTimeEventList.remove(ID);
        AbstractDungeon.shrineList.remove(ID);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (screen == CurScreen.INTRO) {
            AbstractRelic relic;
            imageEventText.updateBodyText(DESCRIPTIONS[buttonPressed + 1]);
            screen = CurScreen.LEAVE;
            switch (buttonPressed) {
                case 0:
                    relic = new GraffitiOfTheEraOfHope();
                    logMetricObtainRelic(NAME, "Graffiti of the Era of Hope", relic);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, relic);
                    break;
                case 1:
                    relic = new HatredOfTheEraOfVendetta();
                    logMetricObtainRelic(NAME, "Hatred of the Era of Vendetta", relic);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, relic);
                    break;
                case 2:
                    relic = new LongingOfTheEraOfDreams();
                    logMetricObtainRelic(NAME, "Longing of the Era of Dreams", relic);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, relic);
                    break;
                case 3:
                    relic = new VisionsOfTheEraOfProsperity();
                    logMetricObtainRelic(NAME, "Visions of the Era of Prosperity", relic);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, relic);
                    break;
                case 4:
                    relic = new SufferingOfTheEraOfCatastrophe();
                    logMetricObtainRelic(NAME, "Suffering of the Era of Catastrophe", relic);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, relic);
                    break;
            }
            imageEventText.updateDialogOption(0, OPTIONS[5]);
            imageEventText.clearRemainingOptions();
        }
        openMap();
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "generateEvent")
    public static class GenerateEventPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(Random rng) {
            if (EncounterMod.firstEvent) {
                return SpireReturn.Return(new Encounter());
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EventHelper.class, method = "roll", paramtypez = {Random.class})
    public static class RollEventPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> Prefix(Random eventRng) {
            if (EncounterMod.firstEvent) {
                return SpireReturn.Return(EventHelper.RoomResult.EVENT);
            } else {
                return SpireReturn.Continue();
            }
        }
    }
}
