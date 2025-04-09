package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAndDeckAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Envy extends AbstractMonster {
    public static final String ID = "encountermod:Envy";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String IMAGE = null;
    public Envy(float x, float y) {
        super(NAME, ID, 35, 20.0F, 0, 160.0F, 300.0F, IMAGE, x, y);
        type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7)
            setHp(40);
        if (AbstractDungeon.ascensionLevel >= 17) {
            this.damage.add(new DamageInfo(this, 18));
        } else if (AbstractDungeon.ascensionLevel >= 2) {
            this.damage.add(new DamageInfo(this, 16));
        } else {
            this.damage.add(new DamageInfo(this, 14));
        }
        loadAnimation("resources/encountermod/images/monsters/enemy_1128_spmage_2/enemy_1128_spmage_233.atlas", "resources/encountermod/images/monsters/enemy_1128_spmage_2/enemy_1128_spmage_233.json", 2.0F);
        state.setAnimation(0, "Idle", true);
        flipHorizontal = true;
    }

    @Override
    public void takeTurn() {
        state.setAnimation(0, "Attack", false);
        state.addAnimation(0, "Idle", true, 0);
        addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
        addToBot(new MakeTempCardInDiscardAndDeckAction(new Dazed()));
        getMove(0);
    }

    @Override
    protected void getMove(int i) {
        setMove((byte)1, Intent.ATTACK_DEBUFF, damage.get(0).base);
    }

    @Override
    public void die(boolean triggerRelics) {
        state.setAnimation(0, "Die", false);
        super.die(triggerRelics);
    }
}