package encountermod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import encountermod.monsters.AgginiOfNila;

public class NilaPower extends AbstractPower implements OnReceivePowerPower {
    public static final String POWER_ID = "encountermod:NilaPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public NilaPower(AbstractCreature owner, int amount) {
        this.ID = POWER_ID;
        this.name = NAME;
        this.type = PowerType.BUFF;
        this.owner = owner;
        this.amount = amount;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/NilaPower 84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/NilaPower 32.png"), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + ((AgginiOfNila)owner).damage + DESCRIPTIONS[2];
    }

    @Override
    public int onPlayerGainedBlock(float blockAmount) {
        if (blockAmount > 1) {
            flash();
            addToBot(new HealAction(owner, owner, MathUtils.floor(blockAmount / 2)));
            blockAmount = blockAmount - MathUtils.floor(blockAmount / 2);
        }
        return MathUtils.floor(blockAmount);
    }

    @Override
    public void onSpecificTrigger() {
        amount --;
        if (amount == 0) {
            AbstractDungeon.effectList.add(new ExplosionSmallEffect(owner.hb.cX, owner.hb.cY));
            addToBot(new DamageAction(AbstractDungeon.player, new DamageInfo(owner, ((AgginiOfNila)owner).damage, DamageInfo.DamageType.THORNS)));
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new SuperWeakPower(AbstractDungeon.player, 5)));
            addToBot(new SuicideAction((AbstractMonster)owner));
        }
        updateDescription();
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        return 0;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        return 0;
    }

    @Override
    public boolean onReceivePower(AbstractPower var1, AbstractCreature var2, AbstractCreature var3)
    {
        return false;
    }
}