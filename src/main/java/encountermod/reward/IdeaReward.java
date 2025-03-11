package encountermod.reward;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import encountermod.EncounterMod;

import static encountermod.patches.IdeaRewardPatch.ENCOUNTERMOD_IDEAREWARD;

public class IdeaReward extends CustomReward {
    public static String ID = "encountermod:IdeaReward";
    private static final Texture ICON = ImageMaster.loadImage("resources/encountermod/images/ui/Idea.png");
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public IdeaReward() {
        super(ICON, TEXT[0] ,ENCOUNTERMOD_IDEAREWARD);
    }

    @Override
    public boolean claimReward() {
        EncounterMod.ideaCount ++;
        //TODO : vfx, 最好让构闪一下
        return true;
    }
}
