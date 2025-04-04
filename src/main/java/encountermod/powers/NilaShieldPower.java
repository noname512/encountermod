package encountermod.powers;

import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import encountermod.monsters.AgginiofNila;
import encountermod.monsters.QuiLon;

public class NilaShieldPower extends AbstractPower {
    public static final String POWER_ID = "encountermod:NilaShieldPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public int nilaDamage = 0;
    public NilaShieldPower(AbstractCreature owner, int amount) {
        this(owner, amount, 0);
    }
    public NilaShieldPower(AbstractCreature owner, int amount, int damage) {
        this.ID = POWER_ID;
        this.name = NAME;
        this.type = PowerType.BUFF;
        this.owner = owner;
        this.amount = amount;
        nilaDamage = damage;
        loadRegion("curiosity");
        // region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/HatredPower 84.png"), 0, 0, 84, 84);
        // region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/HatredPower 32.png"), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + nilaDamage + DESCRIPTIONS[2];
    }

    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if ((info.type == DamageInfo.DamageType.NORMAL) && (target == AbstractDungeon.player)) {
            for (AgginiofNila nila : ((QuiLon)owner).nila) {
                if ((nila == null) || (nila.isDeadOrEscaped())) {
                    continue;
                }
                addToBot(new DamageAction(AbstractDungeon.player, new DamageInfo(nila, nila.damage, DamageInfo.DamageType.THORNS)));
            }
        }
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType damageType) {
        if (damageType == DamageInfo.DamageType.NORMAL) {
            for (int i = 0; i < amount; i++) {
                damage *= 0.8F;
            }
        }
        return damage;
    }
}