package encountermod.events.replacement;


import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BloodyIdol;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.GoldenIdol;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import encountermod.relics.GraffitiOfTheEraOfHope;

public class ForgottenAltar extends AbstractImageEvent {
    public static final String ID = "encountermod:Forgotten Altar";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String DIALOG_1;
    private static final String DIALOG_2;
    private static final String DIALOG_3;
    private static final String DIALOG_4;
    private static final float HP_LOSS_PERCENT = 0.25F;
    private static final float A_2_HP_LOSS_PERCENT = 0.35F;
    private int hpLoss;
    private int hpGain;
    private static final int MAX_HP_GAIN = 5;

    public ForgottenAltar() {
        super(NAME, DIALOG_1, "images/events/forgottenAltar.jpg");
        if (AbstractDungeon.player.hasRelic("Golden Idol")) {
            this.imageEventText.setDialogOption(OPTIONS[0], !AbstractDungeon.player.hasRelic("Golden Idol"), new BloodyIdol());
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], !AbstractDungeon.player.hasRelic("Golden Idol"), new BloodyIdol());
        }

        if (AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)) {
            hpGain = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.35F);
            if (hpGain < 5) {
                hpGain = 5;
            }
        }
        else {
            hpGain = 5;
        }

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.hpLoss = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.35F);
        } else {
            this.hpLoss = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.25F);
        }

        this.imageEventText.setDialogOption(OPTIONS[2] + hpGain + OPTIONS[3] + this.hpLoss + OPTIONS[4]);
        this.imageEventText.setDialogOption(OPTIONS[6], CardLibrary.getCopy("Decay"));
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_FORGOTTEN");
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.gainChalice();
                        this.showProceedScreen(DIALOG_2);
                        CardCrawlGame.sound.play("HEAL_1");
                        return;
                    case 1:
                        AbstractDungeon.player.increaseMaxHp(hpGain, false);
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.hpLoss));
                        CardCrawlGame.sound.play("HEAL_3");
                        this.showProceedScreen(DIALOG_3);
                        logMetricDamageAndMaxHPGain("Forgotten Altar", "Shed Blood", this.hpLoss, hpGain);
                        return;
                    case 2:
                        CardCrawlGame.sound.play("BLUNT_HEAVY");
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, true);
                        AbstractCard curse = new Decay();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        this.showProceedScreen(DIALOG_4);
                        logMetricObtainCard("Forgotten Altar", "Smashed Altar", curse);
                        return;
                    default:
                        return;
                }
            default:
                this.openMap();
        }
    }

    public void gainChalice() {
        int relicAtIndex = 0;

        for(int i = 0; i < AbstractDungeon.player.relics.size(); ++i) {
            if (((AbstractRelic)AbstractDungeon.player.relics.get(i)).relicId.equals("Golden Idol")) {
                relicAtIndex = i;
                break;
            }
        }

        if (AbstractDungeon.player.hasRelic("Bloody Idol")) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), RelicLibrary.getRelic("Circlet").makeCopy());
            logMetricRelicSwap("Forgotten Altar", "Gave Idol", new Circlet(), new GoldenIdol());
        } else {
            ((AbstractRelic)AbstractDungeon.player.relics.get(relicAtIndex)).onUnequip();
            AbstractRelic bloodyIdol = RelicLibrary.getRelic("Bloody Idol").makeCopy();
            bloodyIdol.instantObtain(AbstractDungeon.player, relicAtIndex, false);
            logMetricRelicSwap("Forgotten Altar", "Gave Idol", new BloodyIdol(), new GoldenIdol());
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Forgotten Altar");
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        DIALOG_4 = DESCRIPTIONS[3];
    }
}
