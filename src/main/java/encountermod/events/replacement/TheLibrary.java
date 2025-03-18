package encountermod.events.replacement;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import encountermod.EncounterMod;

import java.util.ArrayList;
import java.util.Iterator;

public class TheLibrary extends AbstractImageEvent {
    public static final String ID = "encountermod:The Library";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String DIALOG_1;
    private static final String SLEEP_RESULT;
    private int screenNum = 0;
    private boolean pickCard = false;
    private static final float HP_HEAL_PERCENT = 0.33F;
    private static final float A_2_HP_HEAL_PERCENT = 0.2F;
    private int healAmt;

    public TheLibrary() {
        super(NAME, DIALOG_1, "images/events/library.jpg");
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.healAmt = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.2F);
        } else {
            this.healAmt = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.33F);
        }

        this.imageEventText.setDialogOption(OPTIONS[0]);
        if (EncounterMod.ideaCount >= 2) {
            imageEventText.setDialogOption(modStrings.OPTIONS[0]);
        }
        else {
            imageEventText.setDialogOption(modStrings.OPTIONS[1], true);
        }
        this.imageEventText.setDialogOption(OPTIONS[1] + this.healAmt + OPTIONS[2]);
    }

    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeCopy();
            logMetricObtainCard("The Library", "Read", c);
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(this.getBook());
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        this.pickCard = true;
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

                        for(int i = 0; i < 20; ++i) {
                            AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                            boolean containsDupe = true;

                            while(true) {
                                Iterator var6;
                                while(containsDupe) {
                                    containsDupe = false;
                                    var6 = group.group.iterator();

                                    while(var6.hasNext()) {
                                        AbstractCard c = (AbstractCard)var6.next();
                                        if (c.cardID.equals(card.cardID)) {
                                            containsDupe = true;
                                            card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                                            break;
                                        }
                                    }
                                }

                                if (group.contains(card)) {
                                    --i;
                                } else {
                                    var6 = AbstractDungeon.player.relics.iterator();

                                    while(var6.hasNext()) {
                                        AbstractRelic r = (AbstractRelic)var6.next();
                                        r.onPreviewObtainCard(card);
                                    }

                                    group.addToBottom(card);
                                }
                                break;
                            }
                        }

                        Iterator var8 = group.group.iterator();

                        while(var8.hasNext()) {
                            AbstractCard c = (AbstractCard)var8.next();
                            UnlockTracker.markCardAsSeen(c.cardID);
                        }

                        AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[4], false);
                        return;
                    case 1:
                        EncounterMod.ideaCount -= 2;
                        this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0] + this.getBook());
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        this.pickCard = true;
                        CardGroup group2 = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard card : CardLibrary.getAllCards()) {
                            if ((card.color == AbstractDungeon.player.getCardColor()) && (card.rarity == AbstractCard.CardRarity.BASIC)) {
                                group2.group.add(card);
                            }
                        }
                        group2.group.addAll(AbstractDungeon.commonCardPool.group);
                        group2.group.addAll(AbstractDungeon.uncommonCardPool.group);
                        group2.group.addAll(AbstractDungeon.rareCardPool.group);
                        AbstractDungeon.gridSelectScreen.open(group2, 1, OPTIONS[4], false);
                        return;
                    case 2:
                        this.imageEventText.updateBodyText(SLEEP_RESULT);
                        AbstractDungeon.player.heal(this.healAmt, true);
                        logMetricHeal("The Library", "Heal", this.healAmt);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                }
            default:
                this.openMap();
        }
    }

    private String getBook() {
        ArrayList<String> list = new ArrayList();
        list.add(DESCRIPTIONS[2]);
        list.add(DESCRIPTIONS[3]);
        list.add(DESCRIPTIONS[4]);
        return (String)list.get(MathUtils.random(2));
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("The Library");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        SLEEP_RESULT = DESCRIPTIONS[1];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }
}
