package encountermod.events.replacement;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class Cleric extends AbstractImageEvent {
    public static final String ID = "encountermod:The Cleric";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    public static final int HEAL_COST = 35;
    private static final int PURIFY_COST = 50;
    private static final int A_2_PURIFY_COST = 75;
    private static final int BOTH_COST = 75;
    private static final int A_15_BOTH_COST = 100;
    private int purifyCost = 0;
    private int bothCost;
    private static final float HEAL_AMT = 0.25F;
    private static final String DIALOG_1;
    private static final String DIALOG_2;
    private static final String DIALOG_3;
    private static final String DIALOG_4;
    private int healAmt;

    public Cleric() {
        super(NAME, DIALOG_1, "images/events/cleric.jpg");
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.purifyCost = A_2_PURIFY_COST;
            bothCost = A_15_BOTH_COST;
        } else {
            this.purifyCost = PURIFY_COST;
            bothCost = BOTH_COST;
        }

        int gold = AbstractDungeon.player.gold;
        if (gold >= 35) {
            this.healAmt = (int)((float)AbstractDungeon.player.maxHealth * 0.25F);
            this.imageEventText.setDialogOption(OPTIONS[0] + this.healAmt + OPTIONS[8], false);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1] + 35 + OPTIONS[2], true);
        }

        if (gold >= purifyCost) {
            this.imageEventText.setDialogOption(OPTIONS[3] + this.purifyCost + OPTIONS[4], false);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1] + purifyCost + OPTIONS[2], true);
        }

        if (gold >= bothCost) {
            this.imageEventText.setDialogOption(modStrings.OPTIONS[0] + bothCost + modStrings.OPTIONS[1] + healAmt + modStrings.OPTIONS[2]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1] + bothCost + OPTIONS[2], true);
        }

        this.imageEventText.setDialogOption(OPTIONS[6]);
    }

    public void update() {
        super.update();
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = (AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            AbstractEvent.logMetricCardRemovalAtCost("The Cleric", "Card Removal", c, this.purifyCost);
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.player.loseGold(35);
                        AbstractDungeon.player.heal(this.healAmt);
                        this.showProceedScreen(DIALOG_2);
                        AbstractEvent.logMetricHealAtCost("The Cleric", "Healed", 35, this.healAmt);
                        return;
                    case 1:
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            AbstractDungeon.player.loseGold(this.purifyCost);
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[7], false, false, false, true);
                        }

                        this.showProceedScreen(DIALOG_3);
                        return;
                    case 2:
                        AbstractDungeon.player.loseGold(this.bothCost);
                        AbstractDungeon.player.heal(this.healAmt);
                        if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()).size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[7], false, false, false, true);
                        }
                        this.showProceedScreen(modStrings.DESCRIPTIONS[0]);
                        return;

                    default:
                        this.showProceedScreen(DIALOG_4);
                        AbstractEvent.logMetric("The Cleric", "Leave");
                        return;
                }
            default:
                this.openMap();
        }
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("The Cleric");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        DIALOG_4 = DESCRIPTIONS[3];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }
}
