package encountermod.events.replacement;


import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.FadeWipeParticle;
import encountermod.EncounterMod;

public class SecretPortal extends AbstractImageEvent {
    public static final String ID = "encountermod:SecretPortal";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    public static final String EVENT_CHOICE_TOOK_PORTAL = "Took Portal";
    private static final String DIALOG_1;
    private static final String DIALOG_2;
    private static final String DIALOG_3;
    private CurScreen screen;

    public SecretPortal() {
        super(NAME, DIALOG_1, "images/events/secretPortal.jpg");
        this.screen = CurScreen.INTRO;
        this.imageEventText.setDialogOption(OPTIONS[0]);
        if (EncounterMod.ideaCount >= 1)
        {
            imageEventText.setDialogOption(modStrings.OPTIONS[0]);
        }
        else {
            imageEventText.setDialogOption(modStrings.OPTIONS[1], true);
        }
        if (EncounterMod.ideaCount >= 2)
        {
            imageEventText.setDialogOption(modStrings.OPTIONS[2]);
        }
        else
        {
            imageEventText.setDialogOption(modStrings.OPTIONS[3], true);
        }
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_PORTAL");
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_2);
                        this.screen = CurScreen.ACCEPT;
                        logMetric("SecretPortal", "Took Portal");
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        CardCrawlGame.screenShake.mildRumble(5.0F);
                        CardCrawlGame.sound.play("ATTACK_MAGIC_SLOW_2");
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                        EncounterMod.ideaCount --;
                        this.screen = CurScreen.LEAVE;
                        AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.RARE);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, relic);
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[1]);
                        EncounterMod.ideaCount -= 2;
                        this.screen = CurScreen.KUILON;
                        this.imageEventText.updateDialogOption(0, modStrings.OPTIONS[4]);
                        break;
                    case 3:
                        this.imageEventText.updateBodyText(DIALOG_3);
                        this.screen = CurScreen.LEAVE;
                        logMetricIgnored("SecretPortal");
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        break;
                }

                this.imageEventText.clearRemainingOptions();
                break;
            case ACCEPT:
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                MapRoomNode node = new MapRoomNode(-1, 15);
                node.room = new MonsterRoomBoss();
                AbstractDungeon.nextRoom = node;
                CardCrawlGame.music.fadeOutTempBGM();
                AbstractDungeon.pathX.add(1);
                AbstractDungeon.pathY.add(15);
                AbstractDungeon.topLevelEffects.add(new FadeWipeParticle());
                AbstractDungeon.nextRoomTransitionStart();
                break;
            case KUILON:
                AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter("Qui'Lon");
                AbstractDungeon.getCurrRoom().rewards.clear();
                this.enterCombatFromImage();
                break;
            default:
                this.openMap();
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("SecretPortal");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
    }

    private static enum CurScreen {
        INTRO,
        ACCEPT,
        LEAVE,
        KUILON;

        private CurScreen() {
        }
    }
}
