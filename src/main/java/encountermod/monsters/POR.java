package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAndDeckAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import encountermod.powers.PORPower;
import encountermod.powers.QuiLonPower;
import encountermod.powers.RecordPower;

public class POR extends AbstractMonster {
    public static final String ID = "encountermod:PadmāsanaofRebirth";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public static final String IMAGE = null; //"resources/encountermod/images/monsters/POR.png";
    public POR(float x, float y) {
        super(NAME, ID, 100, 20.0F, 0, 160.0F, 300.0F, IMAGE, x, y);
        type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new PORPower(this)));
    }

    @Override
    public void takeTurn() {
        hideHealthBar();
        escaped = true;
        // TODO: 退场动画
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m instanceof QuiLon) {
                ((RecordPower)m.getPower(RecordPower.POWER_ID)).list.addAll(((PORPower)(getPower(PORPower.POWER_ID))).list);
                m.getPower(QuiLonPower.POWER_ID).onSpecificTrigger();
            }
        }
    }

    @Override
    protected void getMove(int i) {
        setMove((byte)99, Intent.UNKNOWN);
    }
}