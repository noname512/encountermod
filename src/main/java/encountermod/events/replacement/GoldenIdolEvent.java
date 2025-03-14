package encountermod.events.replacement;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.GoldenIdol;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class GoldenIdolEvent extends AbstractImageEvent {
    public static final String ID = "encountermod:Golden Idol";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String DIALOG_START;
    private static final String DIALOG_BOULDER;
    private static final String DIALOG_CHOSE_RUN;
    private static final String DIALOG_CHOSE_FIGHT;
    private static final String DIALOG_CHOSE_FLAT;
    private static final String DIALOG_IGNORE;
    private int screenNum = 0;
    private static final float HP_LOSS_PERCENT = 0.25F;
    private static final float MAX_HP_LOSS_PERCENT = 0.08F;
    private static final float A_2_HP_LOSS_PERCENT = 0.35F;
    private static final float A_2_MAX_HP_LOSS_PERCENT = 0.1F;
    private int damage;
    private int maxHpLoss;
    private int gold;
    private AbstractRelic relicMetric = null;

    public GoldenIdolEvent() {
        super(NAME, DIALOG_START, "images/events/goldenIdol.jpg");
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.damage = (int)((float)AbstractDungeon.player.maxHealth * 0.35F);
            this.maxHpLoss = (int)((float)AbstractDungeon.player.maxHealth * 0.1F);
            gold = 100;
        } else {
            this.damage = (int)((float)AbstractDungeon.player.maxHealth * 0.25F);
            this.maxHpLoss = (int)((float)AbstractDungeon.player.maxHealth * 0.08F);
            gold = 125;
        }
        this.imageEventText.setDialogOption(OPTIONS[0], new GoldenIdol());
        imageEventText.setDialogOption(modStrings.OPTIONS[0] + gold + modStrings.OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[1]);

        if (this.maxHpLoss < 1) {
            this.maxHpLoss = 1;
        }

    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_GOLDEN");
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_BOULDER);
                        if (AbstractDungeon.player.hasRelic("Golden Idol")) {
                            this.relicMetric = RelicLibrary.getRelic("Circlet").makeCopy();
                        } else {
                            this.relicMetric = RelicLibrary.getRelic("Golden Idol").makeCopy();
                        }

                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.relicMetric);
                        CardCrawlGame.screenShake.mildRumble(5.0F);
                        CardCrawlGame.sound.play("BLUNT_HEAVY");
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2], CardLibrary.getCopy("Injury"));
                        this.imageEventText.updateDialogOption(1, OPTIONS[3] + this.damage + OPTIONS[4]);
                        this.imageEventText.updateDialogOption(2, OPTIONS[5] + this.maxHpLoss + OPTIONS[6]);
                        return;
                    case 1:
                        imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                        AbstractDungeon.player.gainGold(gold);
                        screenNum = 2;
                        imageEventText.updateDialogOption(0, OPTIONS[1]);
                        imageEventText.clearRemainingOptions();
                        AbstractEvent.logMetricGainGold("Golden Idol", "Pray", gold);
                        return;
                    default:
                        this.imageEventText.updateBodyText(DIALOG_IGNORE);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        AbstractEvent.logMetricIgnored("Golden Idol");
                        return;
                }
            case 1:
                switch (buttonPressed) {
                    case 0:
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        this.imageEventText.updateBodyText(DIALOG_CHOSE_RUN);
                        AbstractCard curse = new Injury();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        AbstractEvent.logMetricObtainCardAndRelic("Golden Idol", "Take Wound", curse, this.relicMetric);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    case 1:
                        this.imageEventText.updateBodyText(DIALOG_CHOSE_FIGHT);
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.damage));
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        AbstractEvent.logMetricObtainRelicAndDamage("Golden Idol", "Take Damage", this.relicMetric, this.damage);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    case 2:
                        this.imageEventText.updateBodyText(DIALOG_CHOSE_FLAT);
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHpLoss);
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        AbstractEvent.logMetricObtainRelicAndLoseMaxHP("Golden Idol", "Lose Max HP", this.relicMetric, this.maxHpLoss);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    default:
                        this.openMap();
                        return;
                }
            case 2:
                this.openMap();
                break;
            default:
                this.openMap();
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Golden Idol");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_START = DESCRIPTIONS[0];
        DIALOG_BOULDER = DESCRIPTIONS[1];
        DIALOG_CHOSE_RUN = DESCRIPTIONS[2];
        DIALOG_CHOSE_FIGHT = DESCRIPTIONS[3];
        DIALOG_CHOSE_FLAT = DESCRIPTIONS[4];
        DIALOG_IGNORE = DESCRIPTIONS[5];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }
}
