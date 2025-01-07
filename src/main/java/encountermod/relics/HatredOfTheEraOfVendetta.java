package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class HatredOfTheEraOfVendetta extends CustomRelic {

    public static final String ID = "encountermod:HatredOfTheEraOfVendetta";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/HatredOfTheEraOfVendetta.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/HatredOfTheEraOfVendetta_p.png");
    public HatredOfTheEraOfVendetta() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        return MathUtils.floor(damageAmount * 1.2F);
    }

    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        return MathUtils.floor(damageAmount * 1.2F);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new HatredOfTheEraOfVendetta();
    }
}
