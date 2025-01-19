package encountermod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import encountermod.EncounterMod;
import encountermod.patches.RefreshPatch;
import encountermod.powers.HatredPower;

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
    public void onEquip() {
        RefreshPatch.roomWeight.put("Elite", 12);
        RefreshPatch.totalWeight = 23;
        EncounterMod.prob += 2;
    }

    @Override
    public void onUnequip() {
        RefreshPatch.roomWeight.put("Elite", 1);
        RefreshPatch.totalWeight = 12;
        EncounterMod.prob -= 2;
    }

    @Override
    public void atBattleStart() {
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new HatredPower(AbstractDungeon.player)));
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            addToBot(new ApplyPowerAction(m, m, new HatredPower(m)));
        }
    }

    @Override
    public void onSpawnMonster(AbstractMonster m) {
        addToBot(new ApplyPowerAction(m, m, new HatredPower(m)));
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
