package encountermod.powers;

import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;
import encountermod.monsters.QuiLon;
import encountermod.reward.IdeaReward;

public class QuiLonPower extends AbstractPower{
    public static final String POWER_ID = "encountermod:EmbodimentOfTrikāya";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    boolean canDamage = true;
    public int percent = 75;
    public int stage = 0;
    public QuiLonPower(AbstractCreature owner) {
        this.ID = POWER_ID;
        this.name = NAME;
        this.type = AbstractPower.PowerType.BUFF;
        this.owner = owner;
        loadRegion("curiosity");
        // region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/HatredPower 84.png"), 0, 0, 84, 84);
        // region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/HatredPower 32.png"), 0, 0, 32, 32);
        updateDescription();
    }

    public void changeStage() {
        flash();
        stage ++;
        if (percent > 0) {
            percent -= 25;
        }
        if (stage <= 3) {
            canDamage = false;
            if (((QuiLon)owner).nextMove != 20) {
                ((QuiLon)owner).currentMove = ((QuiLon)owner).nextMove;
            }
            ((QuiLon)owner).setMove((byte)20, AbstractMonster.Intent.UNKNOWN);
            ((QuiLon)owner).createIntent();
        }
        else if (stage == 4) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (m != owner) {
                    m.powers.clear();
                    addToBot(new SuicideAction(m));
                }
            }
            ((QuiLon)owner).setMove((byte)50, AbstractMonster.Intent.BUFF);
            ((QuiLon)owner).createIntent();
        }
        if (stage == 1) {
            AbstractDungeon.getCurrRoom().addGoldToRewards(50);
        }
        else if (stage == 2) {
            AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
        }
        else if (stage == 3) {
            AbstractDungeon.getCurrRoom().addCardToRewards();
        }
        else if (stage == 4) {
            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));;
            AbstractDungeon.getCurrRoom().addGoldToRewards(50);
        }
        else if (stage == 5) {
            AbstractDungeon.getCurrRoom().addGoldToRewards(150);
            AbstractDungeon.getCurrRoom().addCardToRewards();
            AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.UNCOMMON);
            AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);
            AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
            AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
            AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
            AbstractDungeon.actionManager.cleanCardQueue();
            AbstractDungeon.effectList.add(new DeckPoofEffect(64.0F * Settings.scale, 64.0F * Settings.scale, true));
            AbstractDungeon.effectList.add(new DeckPoofEffect((float)Settings.WIDTH - 64.0F * Settings.scale, 64.0F * Settings.scale, false));
            AbstractDungeon.overlayMenu.hideCombatPanels();
            AbstractDungeon.getCurrRoom().endBattle();
        }
    }

    @Override
    public void updateDescription() {
        if (stage <= 3) {
            description = DESCRIPTIONS[0] + percent + DESCRIPTIONS[1] + (int)(percent * 0.01F * owner.maxHealth) + DESCRIPTIONS[2];
            description += DESCRIPTIONS[3 + stage];
        }
        else {
            description = DESCRIPTIONS[3 + stage];
        }
    }

    @Override
    public void onSpecificTrigger() {
        canDamage = true;
        updateDescription();
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        if (canDamage) {
            return damage;
        }
        else {
            return 0;
        }
    }
}
