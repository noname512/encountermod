package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.EncounterMod;
import encountermod.patches.IdeaPatch;
import encountermod.vfx.IdeaFlashEffect;

public class BagOfIdeas extends CustomRelic {

    public static final String ID = "encountermod:BagOfIdeas";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/BagOfIdeas.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/BagOfIdeas_p.png");
    public BagOfIdeas() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        EncounterMod.ideaCount += 3;
        IdeaPatch.topEffect.add(new IdeaFlashEffect());
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BagOfIdeas();
    }
}
