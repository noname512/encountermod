package encountermod.monsters;

import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.actions.SummonEnemyAction;
import encountermod.powers.FriendlyChatPower;
import encountermod.powers.QuiLonPower;
import encountermod.powers.RecordPower;
import encountermod.relics.GraffitiOfTheEraOfHope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QuiLon extends AbstractMonster {
    public static final String ID = "encountermod:Qui'lon, Avatāra of Mahāsattva";
    public static final Logger logger = LogManager.getLogger(QuiLon.class.getName());
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String IMAGE = null;

    public AgginiOfNila[] nila = new AgginiOfNila[2];
    public POR[] boat = new POR[1];
    public Envy[] envy = new Envy[2];
    public Smarty[] smarty = new Smarty[4];
    public Fury[] fury = new Fury[1];
    public float[][] posx = {{0.0F, 0.0F}, {-600.0F}, {-200.0F, -200.0F}, {-400.0F, -400.0F, -800.0F, -800.0F}, {-600.0F}};
    public float[][] posy = {{300.0F, 0.0F}, {450.0F}, {300.0F, -100.0F}, {200.0F, -100.0F, 200.0F, -100.0F}, {0.0F}};
    public int currentMove = -1;
    public QuiLon(float x, float y) {
        super(NAME, ID, 300, 20.0F, 0, 200.0F, 420.0F, IMAGE, x, y);
        type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9)
            setHp(320);
        if (AbstractDungeon.ascensionLevel >= 19) {
            this.damage.add(new DamageInfo(this, 6));
        } else if (AbstractDungeon.ascensionLevel >= 4) {
            this.damage.add(new DamageInfo(this, 5));
        } else {
            this.damage.add(new DamageInfo(this, 4));
        }
        loadAnimation("resources/encountermod/images/monsters/enemy_2089_skzjkl/enemy_2089_skzjkl33.atlas", "resources/encountermod/images/monsters/enemy_2089_skzjkl/enemy_2089_skzjkl33.json", 1.5F);
        state.setAnimation(0, "A_Skill", false);
        state.addAnimation(0, "A_Idle", true, 0);
        flipHorizontal = true;
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new InvinciblePower(this, 0)));
        addToBot(new CannotLoseAction());
        if (AbstractDungeon.player.hasRelic(GraffitiOfTheEraOfHope.ID)) {   // 以后可以考虑复用
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new FriendlyChatPower(AbstractDungeon.player)));
        }
    }

    @Override
    public void takeTurn() {
        int slot;
        logger.info(nextMove);
        logger.info(currentMove);
        if (nextMove == 1) {
            // Summon Envy + Fury
            slot = getEmptySlot(envy);
            addToBot(new SummonEnemyAction(envy, new Envy(posx[2][slot], posy[2][slot]), slot));
            slot = getEmptySlot(fury);
            addToBot(new SummonEnemyAction(fury, new Fury(posx[4][slot], posy[4][slot]), slot));
        }
        else if (nextMove == 2) {
            // Summon Nila
            slot = getEmptySlot(nila);
            addToBot(new SummonEnemyAction(nila, new AgginiOfNila(posx[0][slot], posy[0][slot]), slot));
        }
        else if (nextMove == 3) {
            // Attack
            state.setAnimation(0, "B_Attack", false);
            state.addAnimation(0, "B_Idle", true, 0);
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
        }
        else if (nextMove == 4) {
            // Attack 5 times
            state.setAnimation(0, "B_Skill_2", false);
            state.addAnimation(0, "B_Idle", true, 0);
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
        }
        else if (nextMove == 20) {
            // Summon Smarty * 2 + Padmāsana Of Rebirth
            if (AbstractDungeon.ascensionLevel >= 19) {
                fillSlot(smarty);
            }
            else {
                slot = getEmptySlot(smarty);
                addToBot(new SummonEnemyAction(smarty, new Smarty(posx[3][slot], posy[3][slot]), slot));
                slot = getEmptySlot(smarty, slot);
                addToBot(new SummonEnemyAction(smarty, new Smarty(posx[3][slot], posy[3][slot]), slot));
            }
            slot = getEmptySlot(boat);
            addToBot(new SummonEnemyAction(boat, new POR(posx[1][slot], posy[1][slot]), slot));
        }
        else if (nextMove == 30) {
            // Summon Check Failed, Attack & Defend 40
            state.setAnimation(0, "B_Attack", false);
            state.addAnimation(0, "B_Idle", true, 0);
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 3, true)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 3, true)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 3, true)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new StrengthPower(AbstractDungeon.player, -20)));
            if (!AbstractDungeon.player.hasPower(ArtifactPower.POWER_ID)) {
                addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new GainStrengthPower(AbstractDungeon.player, 20)));
            }
        }
        else if (nextMove == 50) {
            // Turn into Stage 2
            state.setAnimation(0, "B_Revive_End", false);
            state.addAnimation(0, "C_Revive", false, 0);
            state.addAnimation(0, "C_Revive_2", false, 0);
            state.addAnimation(0, "C_Idle", true, 0);
            AbstractPlayer p = AbstractDungeon.player;
            p.hand.clear();
            p.discardPile.clear();
            p.drawPile.clear();
            p.exhaustPile.clear();
            for (AbstractCard c : ((RecordPower)getPower(RecordPower.POWER_ID)).list) {
                p.drawPile.addToRandomSpot(c);
            }
            clearSlot(nila);
            clearSlot(boat);
            clearSlot(envy);
            clearSlot(smarty);
            clearSlot(fury);
            halfDead = false;
            addToBot(new HealAction(this, this, maxHealth));
            getPower(QuiLonPower.POWER_ID).onSpecificTrigger();
            powers.removeIf(pow -> pow.type == AbstractPower.PowerType.DEBUFF);
        }
        else if (nextMove == 61) {
            // Summon Nila
            slot = getEmptySlot(nila);
            addToBot(new SummonEnemyAction(nila, new AgginiOfNila(posx[0][slot], posy[0][slot]), slot));
        }
        else if (nextMove == 62) {
            // Summon Envy + Smarty * 2
            slot = getEmptySlot(envy);
            addToBot(new SummonEnemyAction(envy, new Envy(posx[2][slot], posy[2][slot]), slot));
            if (AbstractDungeon.ascensionLevel >= 19) {
                fillSlot(smarty);
            }
            else {
                slot = getEmptySlot(smarty);
                addToBot(new SummonEnemyAction(smarty, new Smarty(posx[3][slot], posy[3][slot]), slot));
                slot = getEmptySlot(smarty, slot);
                addToBot(new SummonEnemyAction(smarty, new Smarty(posx[3][slot], posy[3][slot]), slot));
            }
        }
        else if (nextMove == 63) {
            // Summon Nila And Attack 2 times
            state.setAnimation(0, "C_Attack", false);
            state.addAnimation(0, "C_Idle", true, 0);
            slot = getEmptySlot(nila);
            addToBot(new SummonEnemyAction(nila, new AgginiOfNila(posx[0][slot], posy[0][slot]), slot));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
        }
        else if (nextMove == 64) {
            // Attack 5 times
            state.setAnimation(0, "C_Skill_1", false);
            state.addAnimation(0, "C_Idle", true, 0);
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
        }
        else if (nextMove == 90) {
            // Summon Check Failed, Attack * 2 & Defend 40
            state.setAnimation(0, "C_Attack", false);
            state.addAnimation(0, "C_Idle", true, 0);
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new DamageAction(AbstractDungeon.player, damage.get(0)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 6, true)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 6, true)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 6, true)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new StrengthPower(AbstractDungeon.player, -40)));
            if (!AbstractDungeon.player.hasPower(ArtifactPower.POWER_ID)) {
                addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new GainStrengthPower(AbstractDungeon.player, 40)));
            }
        }
        else if (nextMove == 0) {
            state.setAnimation(0, "A_Revive", false);
            state.addAnimation(0, "B_Idle", true, 0);
            addToBot(new ApplyPowerAction(this, this, new RecordPower(this)));
            addToBot(new ApplyPowerAction(this, this, new QuiLonPower(this)));
            addToBot(new RemoveSpecificPowerAction(this, this, InvinciblePower.POWER_ID));
        }
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m instanceof Smarty) {
                ((Smarty) m).getMove(0);
            }
        }
        getMove(0);
    }

    @Override
    protected void getMove(int i) {
        if (nextMove == 50) {
            currentMove = 61;
        }
        else if (nextMove != 20) {
            currentMove ++;
            if (currentMove == 5) {
                currentMove = 1;
            }
            if (currentMove == 65) {
                currentMove = 61;
            }
        }
        if (currentMove == 0) {
            setMove(MOVES[2], (byte)0, Intent.UNKNOWN);
        }
        else if (currentMove == 1) {
            if ((getSpace(envy) < 1) || (getSpace(fury) < 1)) {
                checkFailed(1, 1);
            }
            else {
                setMove((byte)1, Intent.UNKNOWN);
            }
        }
        else if (currentMove == 2) {
            if (getSpace(nila) < 1) {
                checkFailed(1, 2);
            }
            else {
                setMove(MOVES[3], (byte)2, Intent.UNKNOWN);
            }
        }
        else if (currentMove == 3) {
            setMove((byte)3, Intent.ATTACK, damage.get(0).base);
        }
        else if (currentMove == 4) {
            setMove(MOVES[0], (byte)4, Intent.ATTACK, damage.get(0).base, 5, true);
        }
        else if (currentMove == 61) {
            if (getSpace(nila) < 1) {
                checkFailed(2, 61);
            }
            else {
                setMove(MOVES[3], (byte)61, Intent.UNKNOWN);
            }
        }
        else if (currentMove == 62) {
            if ((getSpace(smarty) < 2) || (getSpace(envy) < 1)) {
                checkFailed(2, 62);
            }
            else {
                setMove((byte)62, Intent.UNKNOWN);
            }
        }
        else if (currentMove == 63) {
            if (getSpace(nila) < 1) {
                checkFailed(2, 63);
            }
            else {
                setMove(MOVES[3], (byte)63, Intent.ATTACK_BUFF, damage.get(0).base, 2, true);
            }
        }
        else if (currentMove == 64) {
            setMove(MOVES[0], (byte)64, Intent.ATTACK, damage.get(0).base, 5, true);
        }
    }

    void checkFailed(int stage, int nextMove) {
        currentMove = nextMove;
        if (stage == 1) {
            setMove(MOVES[1], (byte)30, Intent.ATTACK_DEBUFF, damage.get(0).base);
        }
        else {
            setMove(MOVES[1], (byte)90, Intent.ATTACK_DEBUFF, damage.get(0).base, 2, true);
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (currentMove <= 0) {
            return;
        }
        while (currentHealth <= maxHealth * 0.01F * ((QuiLonPower)getPower(QuiLonPower.POWER_ID)).percent) {
            ((QuiLonPower)getPower(QuiLonPower.POWER_ID)).changeStage();
            if (((QuiLonPower)getPower(QuiLonPower.POWER_ID)).stage >= 4) {
                break;
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!halfDead) {
            halfDead = true;
            for (AbstractPower p : powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
        }
    }

    int getSpace(AbstractMonster[] m) {
        int cnt = 0;
        for (AbstractMonster mo : m) {
            if ((mo == null) || (mo.isDeadOrEscaped())) {
                cnt ++;
            }
        }
        return cnt;
    }

    void clearSlot(AbstractMonster[] m) {
        for (int i = 0; i < m.length; i++) {
            m[i] = null;
        }
    }

    private int getEmptySlot(AbstractMonster[] m) {
        int i;
        for (i = 0; i < m.length; i++) {
            if ((m[i] == null) || (m[i].isDeadOrEscaped())) {
                return i;
            }
        }
        return -1;
    }
    private int getEmptySlot(AbstractMonster[] m, int notThis) {
        int i;
        for (i = 0; i < m.length; i++) {
            if (i == notThis) {
                continue;
            }
            if ((m[i] == null) || (m[i].isDeadOrEscaped())) {
                return i;
            }
        }
        return -1;
    }

    void fillSlot(AbstractMonster[] m) {
        int i;
        for (i = 0; i < m.length; i++) {
            if ((m[i] == null) || (m[i].isDeadOrEscaped())) {
                addToBot(new SummonEnemyAction(m, new Smarty(posx[3][i], posy[3][i]), i));
            }
        }
    }
}
