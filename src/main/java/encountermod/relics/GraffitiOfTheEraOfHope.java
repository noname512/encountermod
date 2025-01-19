package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.EncounterMod;
import encountermod.patches.RefreshPatch;

public class GraffitiOfTheEraOfHope extends CustomRelic {

    public static final String ID = "encountermod:GraffitiOfTheEraOfHope";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/GraffitiOfTheEraOfHope.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/GraffitiOfTheEraOfHope_p.png");
    public GraffitiOfTheEraOfHope() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        EncounterMod.ideaCount += 2;
        RefreshPatch.roomWeight.put("Monster", 2);
        RefreshPatch.roomWeight.put("Elite", 0);
        RefreshPatch.totalWeight = 9;
    }

    @Override
    public void onUnequip() {
        RefreshPatch.roomWeight.put("Monster", 4);
        RefreshPatch.roomWeight.put("Elite", 1);
        RefreshPatch.totalWeight = 12;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new GraffitiOfTheEraOfHope();
    }
}
