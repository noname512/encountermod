package encountermod.events.replacement;


import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.EncounterMod;
import encountermod.monsters.QuiLon;
import encountermod.reward.IdeaReward;

public class Colosseum extends AbstractImageEvent {
    public static final String ID = "encountermod:Colosseum";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private CurScreen screen;

    public Colosseum() {
        super(NAME, DESCRIPTIONS[0], "images/events/colosseum.jpg");
        this.screen = CurScreen.INTRO;
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1] + DESCRIPTIONS[2] + 4200 + DESCRIPTIONS[3]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        if (EncounterMod.ideaCount >= 1) {
                            this.imageEventText.setDialogOption(modStrings.OPTIONS[0]);
                        }
                        this.screen = CurScreen.FIGHT;
                        return;
                    default:
                        return;
                }
            case FIGHT:
                switch (buttonPressed) {
                    case 0:
                        this.screen = CurScreen.POST_COMBAT;
                        this.logMetric("Fight");
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter("Colosseum Slavers");
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().rewardAllowed = false;
                        this.enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = "Colosseum Slavers";
                        this.imageEventText.clearRemainingOptions();
                        return;
                    case 1:
                        EncounterMod.ideaCount --;
                        this.screen = CurScreen.LEAVE;
                        this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                        this.imageEventText.updateDialogOption(0, modStrings.OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        return;
                    default:
                        return;
                }
            case POST_COMBAT:
                AbstractDungeon.getCurrRoom().rewardAllowed = true;
                switch (buttonPressed) {
                    case 1:
                        this.screen = CurScreen.LEAVE;
                        this.logMetric("Fought Nobs");
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter("Colosseum Nobs");
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);
                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.UNCOMMON);
                        AbstractDungeon.getCurrRoom().addGoldToRewards(100);
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        this.enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = "Colosseum Nobs";
                        return;
                    case 2:
                        this.screen = CurScreen.LEAVE;
                        this.logMetric("Fought Champion");
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter("Champ");
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);
                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.UNCOMMON);
                        AbstractDungeon.getCurrRoom().addGoldToRewards(999);
                        AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        this.enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = "Champ";
                        return;
                    default:
                        this.logMetric("Fled From Nobs");
                        this.openMap();
                        return;
                }
            case LEAVE:
                this.openMap();
                break;
            default:
                this.openMap();
        }

    }

    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric("Colosseum", actionTaken);
    }

    public void reopen() {
        if (this.screen != CurScreen.LEAVE) {
            AbstractDungeon.resetPlayer();
            AbstractDungeon.player.drawX = (float) Settings.WIDTH * 0.25F;
            AbstractDungeon.player.preBattlePrep();
            this.enterImageFromCombat();
            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
            this.imageEventText.updateDialogOption(0, OPTIONS[2]);
            this.imageEventText.setDialogOption(OPTIONS[3]);
            this.imageEventText.setDialogOption(modStrings.OPTIONS[2]);
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Colosseum");
        modStrings = CardCrawlGame.languagePack.getEventString(ID);
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
    }

    private static enum CurScreen {
        INTRO,
        FIGHT,
        LEAVE,
        POST_COMBAT;

        private CurScreen() {
        }
    }
}
