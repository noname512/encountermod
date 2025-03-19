package encountermod.events.replacement;


import com.megacrit.cardcrawl.actions.utility.ShowCardAction;
import com.megacrit.cardcrawl.actions.utility.ShowCardAndPoofAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import encountermod.relics.GraffitiOfTheEraOfHope;
import encountermod.relics.HatredOfTheEraOfVendetta;

public class Nest extends AbstractImageEvent {
    public static final String ID = "encountermod:Nest";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String INTRO_BODY_M;
    private static final String INTRO_BODY_M_2;
    private static final String ACCEPT_BODY;
    private static final String EXIT_BODY;
    private static final int HP_LOSS = 6;
    private int goldGain;
    private int screenNum = 0;
    AbstractCard ritualDagger = null;

    public Nest() {
        super(NAME, INTRO_BODY_M, "images/events/theNest.jpg");
        this.imageEventText.setDialogOption(OPTIONS[5]);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.goldGain = 50;
        } else {
            this.goldGain = 99;
        }
        for (AbstractCard card: AbstractDungeon.player.masterDeck.group) {
            if (card instanceof RitualDagger) {
                ritualDagger = card;
                break;
            }
        }
        if (ritualDagger == null) {
            imageEventText.setDialogOption(modStrings.OPTIONS[1], true);
        }
        else {
            imageEventText.setDialogOption(modStrings.OPTIONS[0]);
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                if (buttonPressed == 0) {
                    this.imageEventText.updateBodyText(INTRO_BODY_M_2);
                    this.imageEventText.setDialogOption(OPTIONS[0] + 6 + OPTIONS[1], new RitualDagger());
                    UnlockTracker.markCardAsSeen("RitualDagger");
                    this.imageEventText.updateDialogOption(0, OPTIONS[2] + this.goldGain + OPTIONS[3]);
                    this.imageEventText.clearRemainingOptions();
                    this.screenNum = 1;
                    break;
                }
                else {
                    ritualDagger.misc += ritualDagger.magicNumber * 4;
                    ritualDagger.baseDamage = ritualDagger.misc;
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(ritualDagger.makeStatEquivalentCopy()));
                    this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                    this.imageEventText.updateDialogOption(0, modStrings.OPTIONS[2]);
                    this.imageEventText.clearRemainingOptions();
                    screenNum = 3;
                    return;
                }
            case 1:
                switch (buttonPressed) {
                    case 0:
                        logMetricGainGold("Nest", "Stole From Cult", this.goldGain);
                        this.imageEventText.updateBodyText(EXIT_BODY);
                        this.screenNum = 2;
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldGain));
                        AbstractDungeon.player.gainGold(this.goldGain);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    case 1:
                        AbstractCard c = new RitualDagger();
                        logMetricObtainCardAndDamage("Nest", "Joined the Cult", c, 6);
                        this.imageEventText.updateBodyText(ACCEPT_BODY);
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, 6));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    default:
                        return;
                }
            case 2:
                this.openMap();
                break;
            case 3:
                AbstractDungeon.player.loseRelic(GraffitiOfTheEraOfHope.ID);
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new HatredOfTheEraOfVendetta());
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[1]);
                screenNum = 2;
                break;
            default:
                this.openMap();
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Nest");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        INTRO_BODY_M = DESCRIPTIONS[0];
        INTRO_BODY_M_2 = DESCRIPTIONS[1];
        ACCEPT_BODY = DESCRIPTIONS[2];
        EXIT_BODY = DESCRIPTIONS[3];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }
}
