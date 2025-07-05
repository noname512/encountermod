package encountermod.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import encountermod.EncounterMod;
import encountermod.relics.*;
import encountermod.reward.IdeaReward;

import java.util.ArrayList;
import java.util.List;


public class InaugurationCeremony extends AbstractImageEvent {
    public static final String ID = "encountermod:Inauguration Ceremony";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private CurScreen screen = CurScreen.INTRO;
    public int ideaGet = 3;
    public int goldAmount = 150;
    public int options;
    private enum CurScreen {
        INTRO, FIGHT, LEAVE
    }
    public InaugurationCeremony() {
        super(NAME, DESCRIPTIONS[0], "resources/encountermod/images/event/InaugurationCeremony.png");
        if (AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)) {
            ideaGet = 7;
            imageEventText.setDialogOption(OPTIONS[0] + ideaGet + OPTIONS[1], false);
            options = 0;
        }
        else if (AbstractDungeon.player.hasRelic(HatredOfTheEraOfVendetta.ID)) {
            imageEventText.setDialogOption(OPTIONS[2], false);
            imageEventText.setDialogOption(OPTIONS[3], false);
            options = 1;
        }
        else if (AbstractDungeon.player.hasRelic(LongingOfTheEraOfDreams.ID)) {
            ideaGet = 4;
            imageEventText.setDialogOption(OPTIONS[4] + ideaGet + OPTIONS[5], false);
            options = 2;
        }
        else if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
            ideaGet = 4;
            imageEventText.setDialogOption(OPTIONS[6] + ideaGet + OPTIONS[7] + goldAmount + OPTIONS[8], false);
            options = 3;
        }
        else if (AbstractDungeon.player.hasRelic(SufferingOfTheEraOfCatastrophe.ID)) {
            noCardsInRewards = true;
            imageEventText.setDialogOption(OPTIONS[9], false);
            options = 4;
        }
        else {
            ideaGet = 5;
            imageEventText.setDialogOption(OPTIONS[0] + ideaGet + OPTIONS[1], false);
            options = 0;
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (screen == CurScreen.INTRO) {
            if (buttonPressed == 0) {
                imageEventText.updateBodyText(DESCRIPTIONS[options + 1]);
                EncounterMod.ideaCount += ideaGet;
                screen = CurScreen.LEAVE;
                if (AbstractDungeon.player.hasRelic(LongingOfTheEraOfDreams.ID)) {
                    AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, relic);
                }
                if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
                    AbstractDungeon.effectList.add(new RainingGoldEffect(goldAmount));
                    AbstractDungeon.player.gainGold(goldAmount);
                }
                if (AbstractDungeon.player.hasRelic(HatredOfTheEraOfVendetta.ID) || AbstractDungeon.player.hasRelic(SufferingOfTheEraOfCatastrophe.ID)) {
                    String[] monsters = {"Hatred 1", "Hatred 2", "Hatred 3", "Catastrophe Fight"};
                    int fightNum = 3;
                    if (AbstractDungeon.player.hasRelic(HatredOfTheEraOfVendetta.ID)) {
                        int actNum = AbstractDungeon.actNum - 1;
                        if (actNum >= 2) {
                            actNum = 2;
                        }
                        fightNum = actNum;
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
                        AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
                        AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.COMMON);
                    }
                    AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(monsters[fightNum]);
                    this.enterCombatFromImage();
                    AbstractDungeon.lastCombatMetricKey = "Inauguration Battle";
                    screen = CurScreen.FIGHT;
                }
            }
            else if (buttonPressed == 1) {
                imageEventText.updateBodyText(DESCRIPTIONS[6]);
                screen = CurScreen.LEAVE;
            }
            if (screen == CurScreen.LEAVE) {
                imageEventText.updateDialogOption(0, OPTIONS[3]);
                imageEventText.clearRemainingOptions();
            }
        }
        else {
            openMap();
        }
    }
}
