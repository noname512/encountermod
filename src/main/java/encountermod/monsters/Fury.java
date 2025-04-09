package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Fury extends AbstractMonster {
    public static final String ID = "encountermod:Fury";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String IMAGE = null;
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
        loadAnimation("resources/encountermod/images/monsters/enemy_1129_spklr_2/enemy_1129_spklr_233.atlas", "resources/encountermod/images/monsters/enemy_1129_spklr_2/enemy_1129_spklr_233.json", 2.0F);
        state.setAnimation(0, "Idle", true);
        flipHorizontal = true;
    }

    @Override
    public void takeTurn() {
        state.setAnimation(0, "Attack", false);
        state.addAnimation(0, "Idle", true, 0);
        addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
        addToBot(new MakeTempCardInDiscardAction(new Wound(), 1));
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