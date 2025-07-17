package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import encountermod.powers.PORPower;
import encountermod.powers.QuiLonPower;
import encountermod.powers.RecordPower;

public class POR extends AbstractMonster {
    public static final String ID = "encountermod:PadmāsanaOfRebirth";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String IMAGE = null;
    public POR(float x, float y) {
        super(NAME, ID, 150, 20.0F, 0, 160.0F, 140.0F, IMAGE, x, y);
        type = EnemyType.ELITE;
        loadAnimation("resources/encountermod/images/monsters/enemy_2090_skzjbc/enemy_2090_skzjbc33.atlas", "resources/encountermod/images/monsters/enemy_2090_skzjbc/enemy_2090_skzjbc33.json", 1.5F);
        state.setAnimation(0, "Start", false);
        state.addAnimation(0, "Idle", true, 0);
        flipHorizontal = true;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new PORPower(this)));
    }

    @Override
    public void takeTurn() {
        hideHealthBar();
        escaped = true;
        isEscaping = true;
        state.setAnimation(0, "Die", false);
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m instanceof QuiLon) {
                ((RecordPower)m.getPower(RecordPower.POWER_ID)).list.addAll(((PORPower)(getPower(PORPower.POWER_ID))).list);
                m.getPower(RecordPower.POWER_ID).updateDescription();
                m.getPower(QuiLonPower.POWER_ID).onSpecificTrigger();
            }
        }
        AbstractDungeon.getCurrRoom().monsters.monsters.remove(this);
    }

    @Override
    public void die() {
        state.setAnimation(0, "Die", false);
        super.die();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m instanceof QuiLon) {
                m.getPower(QuiLonPower.POWER_ID).onSpecificTrigger();
            }
        }
    }

    @Override
    protected void getMove(int i) {
        setMove((byte)99, Intent.UNKNOWN);
    }
}