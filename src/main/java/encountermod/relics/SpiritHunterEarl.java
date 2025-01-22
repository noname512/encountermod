package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
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
    public void onEnterRoom(AbstractRoom r) {
        if (!(r instanceof MonsterRoom)) {
            if (AbstractDungeon.miscRng.random(9) < 2) {
                EncounterMod.ideaCount++;
                // TODO: vfx
            }
        }
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
