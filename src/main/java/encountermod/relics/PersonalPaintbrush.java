package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.EncounterMod;
import encountermod.patches.IdeaPatch;
import encountermod.vfx.IdeaFlashEffect;

public class PersonalPaintbrush extends CustomRelic {

    public static final String ID = "encountermod:PersonalPaintbrush";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/PersonalPaintbrush.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/PersonalPaintbrush_p.png");
    public PersonalPaintbrush() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onRest() {
        EncounterMod.ideaCount++;
        IdeaPatch.topEffect.add(new IdeaFlashEffect());
    }

    @Override
    public AbstractRelic makeCopy() {
        return new PersonalPaintbrush();
    }
}
