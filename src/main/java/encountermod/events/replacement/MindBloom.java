package encountermod.events.replacement;


import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Doubt;
import com.megacrit.cardcrawl.cards.curses.Normality;
import com.megacrit.cardcrawl.cards.curses.Pain;
import com.megacrit.cardcrawl.cards.curses.Writhe;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import encountermod.EncounterMod;
import encountermod.patches.IdeaPatch;
import encountermod.vfx.IdeaFlashEffect;

import java.util.*;

public class MindBloom extends AbstractImageEvent {
    public static final String ID = "encountermod:MindBloom";
    private static final EventStrings eventStrings;
    private static final EventStrings modStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String DIALOG_1;
    private static final String DIALOG_2;
    private static final String DIALOG_3;
    private CurScreen screen;

    public MindBloom() {
        super(NAME, DIALOG_1, "images/events/mindBloom.jpg");
        this.screen = CurScreen.INTRO;
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.imageEventText.setDialogOption(OPTIONS[3]);
        if (AbstractDungeon.floorNum % 50 <= 40) {
            this.imageEventText.setDialogOption(OPTIONS[1], CardLibrary.getCopy("Normality"));
        } else {
            this.imageEventText.setDialogOption(OPTIONS[2], CardLibrary.getCopy("Doubt"));
        }
        imageEventText.setDialogOption(modStrings.OPTIONS[0], CardLibrary.getCopy("Writhe"));

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DIALOG_2);
                        this.screen = CurScreen.FIGHT;
                        logMetric("MindBloom", "Fight");
                        CardCrawlGame.music.playTempBgmInstantly("MINDBLOOM", true);
                        ArrayList<String> list = new ArrayList();
                        list.add("The Guardian");
                        list.add("Hexaghost");
                        list.add("Slime Boss");
                        Collections.shuffle(list, new Random(AbstractDungeon.miscRng.randomLong()));
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter((String)list.get(0));
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        if (AbstractDungeon.ascensionLevel >= 13) {
                            AbstractDungeon.getCurrRoom().addGoldToRewards(25);
                        } else {
                            AbstractDungeon.getCurrRoom().addGoldToRewards(50);
                        }

                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);
                        this.enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = "Mind Bloom Boss Battle";
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DIALOG_3);
                        this.screen = CurScreen.LEAVE;
                        int effectCount = 0;
                        List<String> upgradedCards = new ArrayList();
                        List<String> obtainedRelic = new ArrayList();
                        Iterator var11 = AbstractDungeon.player.masterDeck.group.iterator();

                        while(var11.hasNext()) {
                            AbstractCard c = (AbstractCard)var11.next();
                            if (c.canUpgrade()) {
                                ++effectCount;
                                if (effectCount <= 20) {
                                    float x = MathUtils.random(0.1F, 0.9F) * (float) Settings.WIDTH;
                                    float y = MathUtils.random(0.2F, 0.8F) * (float)Settings.HEIGHT;
                                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), x, y));
                                    AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(x, y));
                                }

                                upgradedCards.add(c.cardID);
                                c.upgrade();
                                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                            }
                        }

                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, RelicLibrary.getRelic("Mark of the Bloom").makeCopy());
                        obtainedRelic.add("Mark of the Bloom");
                        logMetric("MindBloom", "Upgrade", (List)null, (List)null, (List)null, upgradedCards, obtainedRelic, (List)null, (List)null, 0, 0, 0, 0, 0, 0);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        break;
                    case 2:
                        if (AbstractDungeon.floorNum % 50 <= 40) {
                            this.imageEventText.updateBodyText(DIALOG_2);
                            this.screen = CurScreen.LEAVE;
                            List<String> cardsAdded = new ArrayList();
                            cardsAdded.add("Normality");
                            cardsAdded.add("Normality");
                            logMetric("MindBloom", "Gold", cardsAdded, (List)null, (List)null, (List)null, (List)null, (List)null, (List)null, 0, 0, 0, 0, 999, 0);
                            AbstractDungeon.effectList.add(new RainingGoldEffect(999));
                            AbstractDungeon.player.gainGold(999);
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Normality(), (float)Settings.WIDTH * 0.6F, (float)Settings.HEIGHT / 2.0F));
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Normality(), (float)Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
                            this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        } else {
                            this.imageEventText.updateBodyText(DIALOG_2);
                            this.screen = CurScreen.LEAVE;
                            AbstractCard curse = new Doubt();
                            logMetricObtainCardAndHeal("MindBloom", "Heal", curse, AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth);
                            AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                            this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        }
                        break;
                    case 3:
                        this.imageEventText.updateBodyText(modStrings.DESCRIPTIONS[0]);
                        this.screen = CurScreen.LEAVE;
                        AbstractCard curse = new Writhe();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        EncounterMod.ideaCount += 99;
                        IdeaPatch.topEffect.add(new IdeaFlashEffect());

                }

                this.imageEventText.clearRemainingOptions();
                break;
            case LEAVE:
                this.openMap();
                break;
            default:
                this.openMap();
        }

    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("MindBloom");
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
        FIGHT,
        LEAVE;

        private CurScreen() {
        }
    }
}
