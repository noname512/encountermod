package encountermod.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.relics.GraffitiOfTheEraOfHope;
import encountermod.relics.HatredOfTheEraOfVendetta;
import encountermod.relics.LongingOfTheEraOfDreams;

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
        super(NAME, DESCRIPTIONS[0], "resources/rhinemod/images/event/Encounter.png");
        this.imageEventText.setDialogOption(OPTIONS[0], false, new GraffitiOfTheEraOfHope());
        this.imageEventText.setDialogOption(OPTIONS[1], false, new HatredOfTheEraOfVendetta());
        this.imageEventText.setDialogOption(OPTIONS[2], false, new LongingOfTheEraOfDreams());
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (this.screen == CurScreen.INTRO) {
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
            }
            this.imageEventText.updateDialogOption(0, OPTIONS[3]);
            this.imageEventText.clearRemainingOptions();
        }
        openMap();
    }
}
