package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.cards.tempCards.Insight;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import encountermod.powers.NotImportantPower;
import encountermod.reward.IdeaReward;

public class SpinesOfEpoch extends AbstractMonster {
    public static final String ID = "encountermod:SpinesOfEpoch";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String IMAGE = "resources/encountermod/images/monsters/SpinesOfEpoch_128.png";
    public SpinesOfEpoch(float x, float y) {
        super(NAME, ID, 1, 0, 0, 140.0F, 140.0F, IMAGE, x, y);
        maxHealth = AbstractDungeon.floorNum * 2 + AbstractDungeon.actNum * 40;
        if (AbstractDungeon.ascensionLevel >= 17) {
            maxHealth -= 20;
        }
        else if (AbstractDungeon.ascensionLevel >= 7) {
            maxHealth -= 20;
            maxHealth -= AbstractDungeon.actNum * 15;
        }
        else if (AbstractDungeon.ascensionLevel >= 2) {
            maxHealth -= 20;
            maxHealth -= AbstractDungeon.floorNum;
            maxHealth -= AbstractDungeon.actNum * 15;
        }
        else {
            maxHealth -= 20;
            maxHealth -= AbstractDungeon.floorNum;
            maxHealth -= AbstractDungeon.actNum * 20;
        }
        currentHealth = maxHealth;
        type = EnemyType.NORMAL;
        flipHorizontal = true;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new NotImportantPower(this)));
    }

    @Override
    public void takeTurn() {
        if (nextMove == 1) {
            if (AbstractDungeon.ascensionLevel >= 2) {
                addToBot(new MakeTempCardInDrawPileAction(new VoidCard(), 1, true, false));
            }
            else {
                addToBot(new MakeTempCardInDiscardAction(new VoidCard(), 1));
            }
        }
        else {
            addToBot(new MakeTempCardInDrawPileAction(new Insight(), 1, true, false));
        }
        getMove(0);
    }

    @Override
    protected void getMove(int i) {
        if (true) {     //TODO: 如果是畅玩版执行 false 部分
            setMove((byte)1, Intent.STRONG_DEBUFF);
        } else {
            setMove((byte)2, Intent.UNKNOWN);
        }
    }
}