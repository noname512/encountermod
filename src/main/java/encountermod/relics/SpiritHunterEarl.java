package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.EncounterMod;

public class SpiritHunterEarl extends CustomRelic {

    public static final String ID = "encountermod:SpiritHunterEarl";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/SpiritHunterEarl.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/SpiritHunterEarl_p.png");
    public SpiritHunterEarl() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.RARE, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        EncounterMod.prob += 3;
    }

    @Override
    public void onUnequip() {
        EncounterMod.prob -= 3;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SpiritHunterEarl();
    }
}
