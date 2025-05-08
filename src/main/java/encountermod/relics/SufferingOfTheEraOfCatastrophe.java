package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.patches.HorizonEdgePatch;

public class SufferingOfTheEraOfCatastrophe extends CustomRelic {
    public static final String ID = "encountermod:SufferingOfTheEraOfCatastrophe";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/SufferingOfTheEraOfCatastrophe.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/SufferingOfTheEraOfCatastrophe_p.png");
    public static final float CHANCE = 1.0F;
    public SufferingOfTheEraOfCatastrophe() {
        super(ID, IMG, IMG_OUTLINE, AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        if (AbstractDungeon.id.equals("Exordium") || AbstractDungeon.id.equals("TheCity") || AbstractDungeon.id.equals("TheBeyond")) {
            HorizonEdgePatch.generateHorizontalEdge(CHANCE);
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SufferingOfTheEraOfCatastrophe();
    }
}
