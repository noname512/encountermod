package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import encountermod.powers.NilaPower;
import encountermod.powers.NilaShieldPower;

public class AgginiOfNila extends AbstractMonster {
    public static final String ID = "encountermod:AgginiOfNila";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public static final String IMAGE = null;
    public int damage;
    AbstractMonster quiLon;
    public AgginiOfNila(float x, float y) {
        super(NAME, ID, 28, 20.0F, 0, 160.0F, 300.0F, IMAGE, x, y);
        type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7)
            setHp(30);
        if (AbstractDungeon.ascensionLevel >= 17) {
            damage = 6;
        }
        else if (AbstractDungeon.ascensionLevel >= 2) {
            damage = 5;
        }
        else {
            damage = 4;
        }
        currentHealth = 1;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new NilaPower(this, 4)));
        for (AbstractMonster m:AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m instanceof QuiLon) {
                quiLon = m;
                break;
            }
        }
        addToBot(new ApplyPowerAction(quiLon, this, new NilaShieldPower(quiLon, 1, damage)));
    }

    @Override
    public void heal(int healAmount) {
        super.heal(healAmount);
        if (currentHealth == maxHealth) {
            hideHealthBar();
            escaped = true;
            // TODO: 退场动画
            addToBot(new ReducePowerAction(quiLon, quiLon, NilaShieldPower.POWER_ID, 1));
        }
    }

    @Override
    public void die() {
        super.die();
        addToBot(new ReducePowerAction(quiLon, quiLon, NilaShieldPower.POWER_ID, 1));
    }

    @Override
    public void takeTurn() {
        getPower(NilaPower.POWER_ID).onSpecificTrigger();
        getMove(0);
    }
    @Override
    protected void getMove(int i) {
        setMove((byte) 1, Intent.UNKNOWN);
    }
}