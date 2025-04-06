package encountermod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Empty extends CustomCard {
    public static final String ID = "encountermod:Empty";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG = null; //"encountermod/images/cards/Empty.png";
    public static final int COST = -2;
    public Empty() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.STATUS, CardColor.COLORLESS,CardRarity.BASIC, CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    public void triggerWhenDrawn() {
        this.addToBot(new LoseEnergyAction(1));
    }

    @Override
    public void upgrade() {
    }
}
