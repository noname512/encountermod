package encountermod.events.replacement;


import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import encountermod.EncounterMod;

public class TheJoust extends AbstractImageEvent {
    public static final String ID = "encountermod:The Joust";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String HALT_MSG;
    private static final String EXPL_MSG;
    private static final String BET_AGAINST;
    private static final String BET_FOR;
    private static final String COMBAT_MSG;
    private static final String NOODLES_WIN;
    private static final String NOODLES_LOSE;
    private static final String BET_WON_MSG;
    private static final String BET_LOSE_MSG;
    private boolean betFor;
    private boolean ownerWins;
    private static final int WIN_OWNER = 250;
    private static final int WIN_MURDERER = 100;
    private static final int BET_AMT = 50;
    private boolean isCheat = false;
    private CUR_SCREEN screen;
    private float joustTimer;
    private int clangCount;

    public TheJoust() {
        super(NAME, HALT_MSG, "images/events/joust.jpg");
        this.screen = CUR_SCREEN.HALT;
        this.joustTimer = 0.0F;
        this.clangCount = 0;
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    public void update() {
        super.update();
        if (this.joustTimer != 0.0F) {
            this.joustTimer -= Gdx.graphics.getDeltaTime();
            if (this.joustTimer < 0.0F) {
                ++this.clangCount;
                if (this.clangCount == 1) {
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, false);
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    this.joustTimer = 1.0F;
                } else if (this.clangCount == 2) {
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    this.joustTimer = 0.25F;
                } else if (this.clangCount == 3) {
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.LONG, false);
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    this.joustTimer = 0.0F;
                }
            }
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case HALT:
                this.imageEventText.updateBodyText(EXPL_MSG);
                this.imageEventText.updateDialogOption(0, OPTIONS[1] + 50 + OPTIONS[2] + 100 + OPTIONS[3]);
                this.imageEventText.setDialogOption(OPTIONS[4] + 50 + OPTIONS[5] + 250 + OPTIONS[3]);
                if (EncounterMod.ideaCount >= 1) {
                    imageEventText.setDialogOption(modStrings.OPTIONS[0]);
                }
                else {
                    imageEventText.setDialogOption(modStrings.OPTIONS[1], true);
                }
                this.screen = CUR_SCREEN.EXPLANATION;
                break;
            case EXPLANATION:
                if (buttonPressed == 0) {
                    this.betFor = false;
                    this.imageEventText.updateBodyText(BET_AGAINST);
                } else if (buttonPressed == 1) {
                    this.betFor = true;
                    this.imageEventText.updateBodyText(BET_FOR);
                }
                else {
                    this.betFor = true;
                    this.imageEventText.updateBodyText(BET_FOR);
                    EncounterMod.ideaCount --;
                    isCheat = true;
                }

                AbstractDungeon.player.loseGold(50);
                this.imageEventText.updateDialogOption(0, OPTIONS[6]);
                this.imageEventText.clearRemainingOptions();
                this.screen = CUR_SCREEN.PRE_JOUST;
                break;
            case PRE_JOUST:
                this.imageEventText.updateBodyText(COMBAT_MSG);
                this.imageEventText.updateDialogOption(0, OPTIONS[6]);
                if (isCheat) {
                    ownerWins = true;
                }
                else {
                    this.ownerWins = AbstractDungeon.miscRng.randomBoolean(0.3F);
                }
                this.screen = CUR_SCREEN.JOUST;
                this.joustTimer = 0.01F;
                break;
            case JOUST:
                this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                String tmp;
                if (this.ownerWins) {
                    tmp = NOODLES_WIN;
                    if (this.betFor) {
                        tmp = tmp + BET_WON_MSG;
                        AbstractDungeon.player.gainGold(250);
                        CardCrawlGame.sound.play("GOLD_GAIN");
                        if (isCheat) {
                            tmp = modStrings.DESCRIPTIONS[0];
                        }
                        logMetricGainAndLoseGold("The Joust", "Bet on Owner", 250, 50);
                    } else {
                        tmp = tmp + BET_LOSE_MSG;
                        logMetricLoseGold("The Joust", "Bet on Owner", 50);
                    }
                } else {
                    tmp = NOODLES_LOSE;
                    if (this.betFor) {
                        tmp = tmp + BET_LOSE_MSG;
                        logMetricLoseGold("The Joust", "Bet on Murderer", 50);
                    } else {
                        tmp = tmp + BET_WON_MSG;
                        AbstractDungeon.player.gainGold(100);
                        CardCrawlGame.sound.play("GOLD_GAIN");
                        logMetricGainAndLoseGold("The Joust", "Bet on Murderer", 100, 50);
                    }
                }

                this.imageEventText.updateBodyText(tmp);
                this.screen = CUR_SCREEN.COMPLETE;
                break;
            case COMPLETE:
                this.openMap();
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("The Joust");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        HALT_MSG = DESCRIPTIONS[0];
        EXPL_MSG = DESCRIPTIONS[1];
        BET_AGAINST = DESCRIPTIONS[2];
        BET_FOR = DESCRIPTIONS[3];
        COMBAT_MSG = DESCRIPTIONS[4];
        NOODLES_WIN = DESCRIPTIONS[5];
        NOODLES_LOSE = DESCRIPTIONS[6];
        BET_WON_MSG = DESCRIPTIONS[7];
        BET_LOSE_MSG = DESCRIPTIONS[8];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }

    private static enum CUR_SCREEN {
        HALT,
        EXPLANATION,
        PRE_JOUST,
        JOUST,
        COMPLETE;

        private CUR_SCREEN() {
        }
    }
}
