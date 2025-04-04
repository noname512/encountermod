package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAndDeckAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Fury extends AbstractMonster {
    public static final String ID = "encountermod:Fury";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public static final String IMAGE = null; //"resources/encountermod/images/monsters/fury.png";
    public Fury(float x, float y) {
        super(NAME, ID, 70, 20.0F, 0, 160.0F, 300.0F, IMAGE, x, y);
        type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7)
            setHp(80);
        if (AbstractDungeon.ascensionLevel >= 17) {
            this.damage.add(new DamageInfo(this, 20));
        } else if (AbstractDungeon.ascensionLevel >= 2) {
            this.damage.add(new DamageInfo(this, 18));
        } else {
            this.damage.add(new DamageInfo(this, 16));
        }
    }

    @Override
    public void takeTurn() {
        addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
        addToBot(new MakeTempCardInDiscardAction(new Wound(), 1));
        getMove(0);
    }
    @Override
    protected void getMove(int i) {
        setMove((byte)1, Intent.ATTACK_DEBUFF, damage.get(0).base);
    }
}