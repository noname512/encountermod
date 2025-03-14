package encountermod.events.replacement;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;
import com.megacrit.cardcrawl.relics.CultistMask;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import encountermod.EncounterMod;

public class GoldenWing extends AbstractImageEvent {
    public static final String ID = "encountermod:Golden Wing";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private int damage = 7;
    private static final String INTRO;
    private static final String AGREE_DIALOG;
    private static final String SPECIAL_OPTION;
    private static final String DISAGREE_DIALOG;
    private boolean canAttack;
    private boolean purgeResult = false;
    private static final int MIN_GOLD = 50;
    private static final int MAX_GOLD = 80;
    private static final int REQUIRED_DAMAGE = 10;
    private int goldAmount;
    private int idea;
    private CUR_SCREEN screen;

    public GoldenWing() {
        super(NAME, INTRO, "images/events/goldenWing.jpg");
        this.screen = CUR_SCREEN.INTRO;
        this.canAttack = CardHelper.hasCardWithXDamage(10);
        this.imageEventText.setDialogOption(OPTIONS[0] + this.damage + OPTIONS[1]);
        if (this.canAttack) {
            this.imageEventText.setDialogOption(OPTIONS[2] + 50 + OPTIONS[3] + 80 + OPTIONS[4]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5] + 10 + OPTIONS[6], !this.canAttack);
        }
        if (AbstractDungeon.ascensionLevel >= 15) {
            idea = 2;
        }
        else {
            idea = 1;
        }
        if (EncounterMod.ideaCount >= idea) {
            imageEventText.setDialogOption(modStrings.OPTIONS[0] + idea + modStrings.OPTIONS[1], CardLibrary.getCard(RitualDagger.ID));
        }
        else {
            imageEventText.setDialogOption(modStrings.OPTIONS[2] + idea + modStrings.OPTIONS[3], true);
        }

        this.imageEventText.setDialogOption(OPTIONS[7]);
    }

    public void update() {
        super.update();
        this.purgeLogic();
        if (this.waitForInput) {
            this.buttonEffect(GenericEventDialog.getSelectedOption());
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(AGREE_DIALOG);
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, this.damage));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                        this.screen = CUR_SCREEN.PURGE;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        imageEventText.clearRemainingOptions();
                        return;
                    case 1:
                        if (this.canAttack) {
                            this.goldAmount = AbstractDungeon.miscRng.random(50, 80);
                            AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldAmount));
                            AbstractDungeon.player.gainGold(this.goldAmount);
                            AbstractEvent.logMetricGainGold("Golden Wing", "Gained Gold", this.goldAmount);
                            this.imageEventText.updateBodyText(SPECIAL_OPTION);
                            this.screen = CUR_SCREEN.MAP;
                            this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                            imageEventText.clearRemainingOptions();
                        }

                        return;
                    case 2:
                        if (EncounterMod.ideaCount >= idea) {
                            EncounterMod.ideaCount -= idea;
                        }
                        AbstractCard c = new RitualDagger();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractEvent.logMetricObtainCard("Golden Wing", "Wish", c);
                        int roll = MathUtils.random(2);
                        CardCrawlGame.sound.play("VO_CULTIST_1" + (char)((int)'A' + roll));
                        imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                        screen = CUR_SCREEN.MAP;
                        imageEventText.updateDialogOption(0, OPTIONS[7]);
                        imageEventText.clearRemainingOptions();
                        return;
                    default:
                        this.imageEventText.updateBodyText(DISAGREE_DIALOG);
                        AbstractEvent.logMetricIgnored("Golden Wing");
                        this.screen = CUR_SCREEN.MAP;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        imageEventText.clearRemainingOptions();
                        return;
                }
            case PURGE:
                AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, OPTIONS[9], false, false, false, true);
                this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                this.purgeResult = true;
                this.screen = CUR_SCREEN.MAP;
                break;
            case MAP:
                this.openMap();
                break;
            default:
                this.openMap();
        }

    }

    private void purgeLogic() {
        if (this.purgeResult && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = (AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            AbstractEvent.logMetricCardRemovalAndDamage("Golden Wing", "Card Removal", c, this.damage);
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.purgeResult = false;
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Golden Wing");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        INTRO = DESCRIPTIONS[0];
        AGREE_DIALOG = DESCRIPTIONS[1];
        SPECIAL_OPTION = DESCRIPTIONS[2];
        DISAGREE_DIALOG = DESCRIPTIONS[3];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }

    private static enum CUR_SCREEN {
        INTRO,
        PURGE,
        MAP;

        private CUR_SCREEN() {
        }
    }
}
