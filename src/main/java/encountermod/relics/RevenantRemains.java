package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.EncounterMod;
import encountermod.patches.RefreshPatch;

public class RevenantRemains extends CustomRelic {

    public static final String ID = "encountermod:RevenantRemains";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/RevenantRemains.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/RevenantRemains_p.png");
    public RevenantRemains() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        EncounterMod.ideaCount++;
        RefreshPatch.maxRefreshNum++;
    }

    @Override
    public void onUnequip() {
        RefreshPatch.maxRefreshNum--;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new RevenantRemains();
    }
}
