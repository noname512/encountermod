package encountermod.actions;

import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndBattleAction extends AbstractGameAction {
    private static final Logger logger = LogManager.getLogger(EndBattleAction.class.getName());
    public EndBattleAction(float time) {
        this.duration = time;
    }
    public void update() {
        this.tickDuration();
        if (this.isDone) {
            AbstractDungeon.getCurrRoom().endBattle();
        }
    }
}
