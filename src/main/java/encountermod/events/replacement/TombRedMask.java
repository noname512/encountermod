package encountermod.events.replacement;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.RedMask;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import encountermod.EncounterMod;
import encountermod.patches.IdeaPatch;
import encountermod.vfx.IdeaFlashEffect;

public class TombRedMask extends AbstractImageEvent {
    public static final String ID = "encountermod:Tomb of Lord Red Mask";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final int GOLD_AMT = 222;
    private static final String DIALOG_1;
    private static final String MASK_RESULT;
    private static final String RELIC_RESULT;
    private CurScreen screen;

    public TombRedMask() {
        super(NAME, DIALOG_1, "images/events/redMaskTomb.jpg");
        this.screen = CurScreen.INTRO;
        if (AbstractDungeon.player.hasRelic("Red Mask")) {
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true);
            this.imageEventText.setDialogOption(OPTIONS[2] + AbstractDungeon.player.gold + OPTIONS[3], new RedMask());
        }
        boolean hasAttack = false;
        for (AbstractCard card: AbstractDungeon.player.masterDeck.group) {
            if ((card.type == AbstractCard.CardType.ATTACK) && (card.damage >= 15)) {
                hasAttack = true;
            }
        }
        if (!hasAttack)
        {
            imageEventText.setDialogOption(modStrings.OPTIONS[1], true);
        }
        else {
            imageEventText.setDialogOption(modStrings.OPTIONS[0]);
        }

        this.imageEventText.setDialogOption(OPTIONS[4]);
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                if (buttonPressed == 0) {
                    AbstractDungeon.effectList.add(new RainingGoldEffect(222));
                    AbstractDungeon.player.gainGold(222);
                    this.imageEventText.updateBodyText(MASK_RESULT);
                    logMetricGainGold("Tomb of Lord Red Mask", "Wore Mask", 222);
                } else if (buttonPressed == 1 && !AbstractDungeon.player.hasRelic("Red Mask")) {
                    AbstractRelic r = new RedMask();
                    logMetricObtainRelicAtCost("Tomb of Lord Red Mask", "Paid", r, AbstractDungeon.player.gold);
                    AbstractDungeon.player.loseGold(AbstractDungeon.player.gold);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), r);
                    this.imageEventText.updateBodyText(RELIC_RESULT);
                } else if ((buttonPressed == 3) || (AbstractDungeon.player.hasRelic("Red Mask") && (buttonPressed == 2))) {
                    this.openMap();
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[4]);
                    logMetricIgnored("Tomb of Lord Red Mask");
                }
                else {
                    EncounterMod.ideaCount += 2;
                    IdeaPatch.topEffect.add(new IdeaFlashEffect());
                    this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                }

                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4]);
                this.screen = CurScreen.RESULT;
                break;
            default:
                this.openMap();
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Tomb of Lord Red Mask");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        MASK_RESULT = DESCRIPTIONS[1];
        RELIC_RESULT = DESCRIPTIONS[2];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }

    private static enum CurScreen {
        INTRO,
        RESULT;

        private CurScreen() {
        }
    }
}
