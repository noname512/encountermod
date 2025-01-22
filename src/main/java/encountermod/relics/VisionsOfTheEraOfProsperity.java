package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;

public class VisionsOfTheEraOfProsperity extends CustomRelic {

    public static final String ID = "encountermod:VisionsOfTheEraOfProsperity";
    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
    public static final String NAME = relicStrings.NAME;
    public static final String[] DESCRIPTIONS = relicStrings.DESCRIPTIONS;
    public static final Texture IMG = new Texture("resources/encountermod/images/relics/VisionsOfTheEraOfProsperity.png");
    public static final Texture IMG_OUTLINE = new Texture("resources/encountermod/images/relics/VisionsOfTheEraOfProsperity_p.png");
    public VisionsOfTheEraOfProsperity() {
        super(ID, IMG, IMG_OUTLINE, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.gainGold(180);
        counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (room instanceof TreasureRoomBoss) {
            counter = 0;
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new VisionsOfTheEraOfProsperity();
    }
}
