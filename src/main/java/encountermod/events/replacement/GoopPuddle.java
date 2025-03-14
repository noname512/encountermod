package encountermod.events.replacement;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.ObtainPotionEffect;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.ArrayList;

public class GoopPuddle extends AbstractImageEvent {
    public static final String ID = "encountermod:World of Goop";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String DIALOG_1;
    private static final String GOLD_DIALOG;
    private static final String LEAVE_DIALOG;
    private CurScreen screen;
    private int damage;
    private int gold;
    private int goldLoss;
    private int bonusGold;
    private AbstractCard card;

    public GoopPuddle() {
        super(NAME, DIALOG_1, "images/events/goopPuddle.jpg");
        this.screen = CurScreen.INTRO;
        this.damage = 11;
        this.gold = 75;
        bonusGold = 175;
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.goldLoss = AbstractDungeon.miscRng.random(35, 75);
        } else {
            this.goldLoss = AbstractDungeon.miscRng.random(20, 50);
        }

        if (this.goldLoss > AbstractDungeon.player.gold) {
            this.goldLoss = AbstractDungeon.player.gold;
        }

        ArrayList<AbstractCard> cards = new ArrayList<>();
        AbstractCard.CardRarity currentMax = AbstractCard.CardRarity.CURSE;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.RARE) {
                if (currentMax != c.rarity) {
                    cards.clear();
                    currentMax = c.rarity;
                }
                cards.add(c);
            }
            else if (c.rarity == AbstractCard.CardRarity.UNCOMMON) {
                if (currentMax == AbstractCard.CardRarity.RARE) {
                    continue;
                }
                if (currentMax != c.rarity) {
                    cards.clear();
                    currentMax = c.rarity;
                }
                cards.add(c);
            }
            else if (c.rarity == AbstractCard.CardRarity.COMMON) {
                if ((currentMax == AbstractCard.CardRarity.RARE) || (currentMax == AbstractCard.CardRarity.UNCOMMON)) {
                    continue;
                }
                if (currentMax != c.rarity) {
                    cards.clear();
                    currentMax = c.rarity;
                }
                cards.add(c);
            }
        }
        if (!cards.isEmpty()) {
            card = cards.get(AbstractDungeon.eventRng.random(cards.size()-1));
        }
        boolean isDisabled = false;
        if ((currentMax == AbstractCard.CardRarity.CURSE) || ((AbstractDungeon.ascensionLevel >= 15) && (currentMax == AbstractCard.CardRarity.COMMON))) {
            isDisabled = true;
        }

        this.imageEventText.setDialogOption(OPTIONS[0] + this.gold + OPTIONS[1] + this.damage + OPTIONS[2]);
        this.imageEventText.setDialogOption(OPTIONS[3] + this.goldLoss + OPTIONS[4]);
        if (!isDisabled) {
            imageEventText.setDialogOption(modStrings.OPTIONS[0] + card.name + modStrings.OPTIONS[1]);
        }
        else if (AbstractDungeon.ascensionLevel >= 15) {
            imageEventText.setDialogOption(modStrings.OPTIONS[2], true);
        }
        else {
            imageEventText.setDialogOption(modStrings.OPTIONS[3], true);
        }
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SPIRITS");
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(GOLD_DIALOG);
                        this.imageEventText.clearAllDialogs();
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.damage));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.gold));
                        AbstractDungeon.player.gainGold(this.gold);
                        this.imageEventText.setDialogOption(OPTIONS[5]);
                        this.screen = CurScreen.RESULT;
                        AbstractEvent.logMetricGainGoldAndDamage("World of Goop", "Gather Gold", this.gold, this.damage);
                        return;
                    case 1:
                        this.imageEventText.updateBodyText(LEAVE_DIALOG);
                        AbstractDungeon.player.loseGold(this.goldLoss);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[5]);
                        this.screen = CurScreen.RESULT;
                        logMetricLoseGold("World of Goop", "Left Gold", this.goldLoss);
                        return;
                    case 2:
                        imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                        AbstractDungeon.player.gainGold(bonusGold);
                        logMetricGainGold("World of Goop", "Trade", bonusGold);
                        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        AbstractDungeon.player.masterDeck.removeCard(card);
                        imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[5]);
                        return;
                    default:
                        return;
                }
            default:
                this.openMap();
        }
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("World of Goop");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        GOLD_DIALOG = DESCRIPTIONS[1];
        LEAVE_DIALOG = DESCRIPTIONS[2];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }

    private static enum CurScreen {
        INTRO,
        RESULT;

        private CurScreen() {
        }
    }
}
