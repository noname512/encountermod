package encountermod.events.replacement;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import encountermod.EncounterMod;
import encountermod.patches.IdeaPatch;
import encountermod.vfx.IdeaFlashEffect;

import java.util.ArrayList;
import java.util.List;

public class FountainOfCurseRemoval extends AbstractImageEvent {
    public static final String ID = "encountermod:Fountain of Cleansing";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String DIALOG_1;
    private static final String DIALOG_2;
    private static final String DIALOG_3;
    private int screenNum = 0;

    public FountainOfCurseRemoval() {
        super(NAME, DIALOG_1, "images/events/fountain.jpg");
        this.imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption(modStrings.OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }

    public void onEnterRoom() {
        CardCrawlGame.music.playTempBGM("SHRINE");
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_FOUNTAIN");
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_2);
                        List<String> curses = new ArrayList();
                        this.screenNum = 1;

                        for(int i = AbstractDungeon.player.masterDeck.group.size() - 1; i >= 0; --i) {
                            if (((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).type == AbstractCard.CardType.CURSE && !((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).inBottleFlame && !((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).inBottleLightning && ((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).cardID != "AscendersBane" && ((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).cardID != "CurseOfTheBell" && ((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).cardID != "Necronomicurse") {
                                AbstractDungeon.effectList.add(new PurgeCardEffect((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)));
                                curses.add(((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i)).cardID);
                                AbstractDungeon.player.masterDeck.removeCard((AbstractCard)AbstractDungeon.player.masterDeck.group.get(i));
                            }
                        }

                        logMetricRemoveCards("Fountain of Cleansing", "Removed Curses", curses);
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    case 1:
                        this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                        EncounterMod.ideaCount += 2;
                        IdeaPatch.topEffect.add(new IdeaFlashEffect());
                        CardGroup curse = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                            if (card.type == AbstractCard.CardType.CURSE) {
                                if (card.rawDescription.contains("逃脱") || card.rawDescription.contains("牌组中移除")) {
                                    //TODO: 支持下别的语言吧，总之别把我源石删了！
                                    continue;
                                }
                                curse.group.add(card);
                            }
                        }
                        AbstractDungeon.gridSelectScreen.open(curse, 1, modStrings.DESCRIPTIONS[1], false, false, false, true);
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        return;
                    case 2:
                        logMetricIgnored("Fountain of Cleansing");
                        this.imageEventText.updateBodyText(DIALOG_3);
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        this.screenNum = 1;
                        return;
                }
            case 1:
                this.openMap();
                break;
            default:
                this.openMap();
        }

    }

    public void update() {
        super.update();
        this.purgeLogic();
        if (this.waitForInput) {
            this.buttonEffect(GenericEventDialog.getSelectedOption());
        }

    }
    private void purgeLogic() {
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = (AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            logMetricCardRemoval("Wheel of Change", "Card Removal", c);
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.effectList.add(new PurgeCardEffect(c));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.hasFocus = false;
        }

    }
    public void logMetric(String cardGiven) {
        AbstractEvent.logMetric("Fountain of Cleansing", cardGiven);
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Fountain of Cleansing");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }
}
