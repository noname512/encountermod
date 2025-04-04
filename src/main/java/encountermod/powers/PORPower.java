package encountermod.powers;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import encountermod.cards.Empty;

import java.util.ArrayList;

public class PORPower extends AbstractPower {
    public static final String POWER_ID = "encountermod:PORPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public ArrayList<AbstractCard> list = new ArrayList<>();
    public PORPower(AbstractCreature owner) {
        this.ID = POWER_ID;
        this.name = NAME;
        this.type = PowerType.BUFF;
        this.owner = owner;
        loadRegion("curiosity");
        list.add(new Empty());
        list.add(new Empty());
        list.add(new Empty());
        // region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/HatredPower 84.png"), 0, 0, 84, 84);
        // region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/HatredPower 32.png"), 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + list.get(0).name + DESCRIPTIONS[1] + list.get(1).name + DESCRIPTIONS[1] + list.get(2).name + DESCRIPTIONS[2];
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        if ((!card.purgeOnUse) && (m == owner)) {
            for (int i = 0; i < 3; i++) {
                AbstractCard c = list.get(i);
                if (c instanceof Empty) {
                    addToBot(new MakeTempCardInDiscardAction(c, 1));
                    card.purgeOnUse = true;
                    list.remove(c);
                    list.add(card.makeStatEquivalentCopy());
                    break;
                }
            }
        }
    }

    @Override
    public void onDeath() {
        addToBot(new MakeTempCardInDiscardAction(list.get(0), 1));
        addToBot(new MakeTempCardInDiscardAction(list.get(1), 1));
        addToBot(new MakeTempCardInDiscardAction(list.get(2), 1));
    }
}