package encountermod.monsters;

import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;
import encountermod.actions.EndBattleAction;
import encountermod.actions.SummonEnemyAction;
import encountermod.powers.CounterPower;

public class Catastrophe extends AbstractMonster {
    public static final String ID = "encountermod:Catastrophe";
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String IMAGE = "resources/encountermod/images/monsters/Catastrophe.png";
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public SpinesOfEpoch[] spines = new SpinesOfEpoch[5];
    public float[] posx = {-150.0F, -150.0F, -300.0F, -450.0F, -450.0F};
    public float[] posy = {300.0F, 0.0F, 150.0F, 300.0F, 0.0F};
    int turn = 1;
    public Catastrophe(float x, float y) {
        super(NAME, ID, 5, 0, 0, 140.0F, 140.0F, IMAGE, x, y);
        currentHealth = 1;
        type = EnemyType.NORMAL;
        flipHorizontal = true;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new SummonEnemyAction(spines, new SpinesOfEpoch(posx[0], posy[0]), 0));
        addToBot(new ApplyPowerAction(this, this, new CounterPower(this, 5)));
    }

    @Override
    public void takeTurn() {
        getMove(0);
        turn ++;
        addToBot(new TalkAction(true, DIALOG[turn - 2], 2.0F, 2.0F));
        if (turn == 6)
        {
            AbstractDungeon.player.hideHealthBar();
            AbstractDungeon.player.isEscaping = true;
            AbstractDungeon.player.flipHorizontal = !AbstractDungeon.player.flipHorizontal;
            AbstractDungeon.overlayMenu.endTurnButton.disable();
            AbstractDungeon.player.escapeTimer = 2.5F;
            this.hideHealthBar();
            AbstractDungeon.actionManager.cleanCardQueue();
            AbstractDungeon.effectList.add(new DeckPoofEffect(64.0F * Settings.scale, 64.0F * Settings.scale, true));
            AbstractDungeon.effectList.add(new DeckPoofEffect((float)Settings.WIDTH - 64.0F * Settings.scale, 64.0F * Settings.scale, false));
            AbstractDungeon.overlayMenu.hideCombatPanels();
            addToBot(new EndBattleAction(2.5F));
            return;
        }
        for (int i = 0; i < turn; i++) {
            if ((spines[i] == null) || (spines[i].isDeadOrEscaped())) {
                addToBot(new SummonEnemyAction(spines, new SpinesOfEpoch(posx[i], posy[i]), i));
            }
        }
        addToBot(new HealAction(this, this, 1));
    }

    @Override
    protected void getMove(int i) {
        setMove((byte)1, Intent.UNKNOWN);
    }
}