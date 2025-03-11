package encountermod.reward;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import encountermod.EncounterMod;
import encountermod.patches.IdeaPatch;
import encountermod.vfx.IdeaFlashEffect;

import static encountermod.patches.IdeaRewardPatch.IDEA_REWARD;

public class IdeaReward extends CustomReward {
    public static String ID = "encountermod:IdeaReward";
    private static final Texture ICON = ImageMaster.loadImage("resources/encountermod/images/ui/IdeaL.png");
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public IdeaReward() {
        super(ICON, TEXT[0], IDEA_REWARD);
    }

    @Override
    public boolean claimReward() {
        EncounterMod.ideaCount++;
        IdeaPatch.topEffect.add(new IdeaFlashEffect());
        return true;
    }
}
