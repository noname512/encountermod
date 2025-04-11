package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import encountermod.powers.SmartyPower;

public class Smarty extends AbstractMonster {
    public static final String ID = "encountermod:Smarty";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String IMAGE = null;
    public Smarty(float x, float y) {
        super(NAME, ID, 26, 20.0F, 0, 120.0F, 200.0F, IMAGE, x, y);
        type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7)
            setHp(30);
        if (AbstractDungeon.ascensionLevel >= 17) {
            this.damage.add(new DamageInfo(this, 6));
            this.damage.add(new DamageInfo(this, 40));
        } else if (AbstractDungeon.ascensionLevel >= 2) {
            this.damage.add(new DamageInfo(this, 5));
            this.damage.add(new DamageInfo(this, 35));
        } else {
            this.damage.add(new DamageInfo(this, 5));
            this.damage.add(new DamageInfo(this, 28));
        }
        loadAnimation("resources/encountermod/images/monsters/enemy_1126_spslme_2/enemy_1126_spslme_233.atlas", "resources/encountermod/images/monsters/enemy_1126_spslme_2/enemy_1126_spslme_233.json", 2.0F);
        state.setAnimation(0, "Idle", true);
        flipHorizontal = true;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new SmartyPower(this, damage.get(0).base)));
    }

    @Override
    public void takeTurn() {
        if (nextMove == 1) {
            state.setAnimation(0, "Attack", false);
            state.addAnimation(0, "Idle", true, 0);
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
        }
        else {
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(1)));
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped() && (m instanceof Smarty)) {
                    AbstractDungeon.effectList.add(new ExplosionSmallEffect(m.hb.cX, m.hb.cY));
                    addToBot(new SuicideAction(m));
                }
            }
        }
        getMove(0);
    }

    @Override
    public void rollMove() {
        setMove((byte)2, Intent.ATTACK, damage.get(1).base);
    }

    @Override
    protected void getMove(int i) {
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && (m instanceof Smarty) && m != this) {
                setMove((byte)2, Intent.ATTACK, damage.get(1).base);
                return;
            }
        }
        setMove((byte)1, Intent.ATTACK, damage.get(0).base);
    }

    @Override
    public void die() {
        state.setAnimation(0, "Die", false);
        super.die();
        int cnt = 0;
        AbstractMonster mo = null;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && (m instanceof Smarty) && m != this) {
                cnt ++;
                mo = m;
            }
        }
        if (cnt == 1) {
            mo.setMove((byte)1, Intent.ATTACK, damage.get(0).base);
            mo.createIntent();
            addToBot(new TextAboveCreatureAction(mo, TextAboveCreatureAction.TextType.INTERRUPTED));
        }
    }
}