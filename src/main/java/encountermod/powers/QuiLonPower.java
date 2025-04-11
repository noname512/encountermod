package encountermod.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
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
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;
import encountermod.monsters.QuiLon;
import encountermod.reward.IdeaReward;

public class QuiLonPower extends AbstractPower implements OnReceivePowerPower {
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
        if (stage <= 3) {
            owner.state.setAnimation(0, "B_Skill_Begin", false);
            owner.state.addAnimation(0, "B_Skill_Loop", true, 0);
        } else if (stage == 4) {
            owner.state.setAnimation(0, "B_Revive_Begin", false);
            owner.state.addAnimation(0, "B_Revive_Loop", true, 0);
        } else if (stage == 5) {
            owner.state.setAnimation(0, "C_Die", false);
        }

        if (percent > 0) {
            percent -= 25;
        }
        if (stage <= 3) {
            canDamage = false;
            ((QuiLon)owner).setMove((byte)20, AbstractMonster.Intent.UNKNOWN);
            ((QuiLon)owner).createIntent();
        }
        else if (stage == 4) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (m != owner) {
                    QuiLon.logger.info("name" + m.name);
                    m.powers.clear();
                    m.currentHealth = 0;
                    m.isDead = true;
                    m.isDying = true;
                }
            }
            AbstractDungeon.getCurrRoom().monsters.monsters.removeIf(m -> m != owner);
            owner.powers.removeIf(p -> p.ID.equals(NilaShieldPower.POWER_ID));
            QuiLon.logger.info("PowerID" + NilaShieldPower.POWER_ID);
            ((QuiLon)owner).setMove(QuiLon.MOVES[4], (byte)50, AbstractMonster.Intent.BUFF);
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
        if (stage < 3) {
            description = DESCRIPTIONS[0] + percent + DESCRIPTIONS[1] + (int)(percent * 0.01F * owner.maxHealth) + DESCRIPTIONS[2];
            description += DESCRIPTIONS[3 + stage];
        }
        else {
            description = DESCRIPTIONS[3 + stage];
        }
    }

    @Override
    public void onSpecificTrigger() {
        owner.state.setAnimation(0, "B_Skill_End", false);
        owner.state.addAnimation(0, "B_Idle", true, 0);
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

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (canDamage) {
            return damageAmount;
        }
        else {
            return 0;
        }
    }
    @Override
    public boolean onReceivePower(AbstractPower var1, AbstractCreature var2, AbstractCreature var3)
    {
        if (!canDamage) {
            return false;
        }
        return true;
    }
}
