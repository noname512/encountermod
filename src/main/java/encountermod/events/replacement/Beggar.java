package encountermod.events.replacement;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import encountermod.EncounterMod;
import encountermod.patches.IdeaPatch;
import encountermod.vfx.IdeaFlashEffect;

public class Beggar extends AbstractImageEvent {
    public static final String ID = "encountermod:Beggar";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private CurScreen screen;
    public static final int GOLD_COST = 75;
    private static final String DIALOG_1;
    private static final String CANCEL_DIALOG;
    private static final String PURGE_DIALOG;
    private static final String POST_PURGE_DIALOG;
    private static int gold;

    public Beggar() {
        super(NAME, DIALOG_1, "images/events/beggar.jpg");
        if (AbstractDungeon.player.gold >= 75) {
            this.imageEventText.setDialogOption(OPTIONS[0] + 75 + OPTIONS[1], AbstractDungeon.player.gold < 75);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[2] + 75 + OPTIONS[3], AbstractDungeon.player.gold < 75);
        }
        if (AbstractDungeon.ascensionLevel >= 15) {
            gold = 50;
        }
        else {
            gold = 35;
        }
        if (AbstractDungeon.player.gold >= gold) {
            imageEventText.setDialogOption(modStrings.OPTIONS[0] + gold + modStrings.OPTIONS[1]);
        }
        else {
            imageEventText.setDialogOption(modStrings.OPTIONS[2] + gold + modStrings.OPTIONS[3]);
        }

        this.imageEventText.setDialogOption(OPTIONS[5]);
        this.hasDialog = true;
        this.hasFocus = true;
        this.screen = CurScreen.INTRO;
    }

    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardCrawlGame.sound.play("CARD_EXHAUST");
            logMetricCardRemovalAtCost("Beggar", "Gave Gold", (AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0), 75);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            AbstractDungeon.player.masterDeck.removeCard((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.openMap();
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                if (buttonPressed == 0) {
                    this.imageEventText.loadImage("images/events/cleric.jpg");
                    this.imageEventText.updateBodyText(PURGE_DIALOG);
                    AbstractDungeon.player.loseGold(75);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[4]);
                    this.screen = CurScreen.GAVE_MONEY;
                } else if (buttonPressed == 2) {
                    this.imageEventText.updateBodyText(CANCEL_DIALOG);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[5]);
                    this.screen = CurScreen.LEAVE;
                    logMetricIgnored("Beggar");
                }
                else {
                    this.imageEventText.loadImage("images/events/cleric.jpg");
                    this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                    AbstractDungeon.player.loseGold(gold);
                    EncounterMod.ideaCount += 3;
                    IdeaPatch.topEffect.add(new IdeaFlashEffect());
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[5]);
                    this.screen = CurScreen.LEAVE;
                }
                break;
            case GAVE_MONEY:
                AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[6], false, false, false, true);
                this.imageEventText.updateBodyText(POST_PURGE_DIALOG);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[5]);
                this.screen = CurScreen.LEAVE;
                break;
            case LEAVE:
                this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                this.imageEventText.clearRemainingOptions();
                this.openMap();
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Beggar");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        CANCEL_DIALOG = DESCRIPTIONS[1];
        PURGE_DIALOG = DESCRIPTIONS[2];
        POST_PURGE_DIALOG = DESCRIPTIONS[3];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }

    public static enum CurScreen {
        INTRO,
        LEAVE,
        GAVE_MONEY;

        private CurScreen() {
        }
    }
}
