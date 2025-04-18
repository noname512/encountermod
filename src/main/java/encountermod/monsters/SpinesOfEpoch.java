package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.purple.Foresight;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.cards.tempCards.Insight;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import encountermod.powers.PORPower;
import encountermod.powers.QuiLonPower;
import encountermod.powers.RecordPower;
import encountermod.reward.IdeaReward;

public class SpinesOfEpoch extends AbstractMonster {
    public static final String ID = "encountermod:SpinesOfEpoch";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String IMAGE = null;
    public SpinesOfEpoch(float x, float y) {
        super(NAME, ID, 1, 20.0F, 0, 160.0F, 140.0F, IMAGE, x, y);
        maxHealth = AbstractDungeon.floorNum * 2 + AbstractDungeon.actNum * 40;
        if (AbstractDungeon.ascensionLevel >= 17) {
            maxHealth -= 20;
        }
        else if (AbstractDungeon.ascensionLevel >= 7) {
            maxHealth -= 30;
        }
        else if (AbstractDungeon.ascensionLevel >= 2) {
            maxHealth -= 20;
            maxHealth -= AbstractDungeon.actNum * 10;
        }
        else {
            maxHealth -= 30;
            maxHealth -= AbstractDungeon.floorNum;
        }
        currentHealth = maxHealth;
        type = EnemyType.NORMAL;
        /*
        loadAnimation("resources/encountermod/images/monsters/enemy_2090_skzjbc/enemy_2090_skzjbc33.atlas", "resources/encountermod/images/monsters/enemy_2090_skzjbc/enemy_2090_skzjbc33.json", 1.5F);
        state.setAnimation(0, "Start", false);
        state.addAnimation(0, "Idle", true, 0);
        */
        flipHorizontal = true;
    }

    @Override
    public void takeTurn() {
        if (nextMove == 1) {
            addToBot(new MakeTempCardInDrawPileAction(new VoidCard(), 1, true, false));
        }
        else {
            addToBot(new MakeTempCardInDrawPileAction(new Insight(), 1, true, false));
        }
        getMove(0);
    }

    @Override
    public void die() {
        super.die();
        AbstractDungeon.getCurrRoom().rewards.add(new IdeaReward());
    }

    @Override
    protected void getMove(int i) {
        if (true) {     //TODO: 如果是畅玩版执行 false 部分
            setMove((byte)1, Intent.STRONG_DEBUFF);
        }
        else {
            setMove((byte)2, Intent.UNKNOWN);
        }
    }
}