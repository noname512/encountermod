package encountermod.reward;

import basemod.abstracts.CustomReward;
import basemod.devcommands.relic.Relic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import encountermod.EncounterMod;

import java.util.Iterator;

import static encountermod.patches.IdeaRewardPatch.ENCOUNTERMOD_IDEAREWARD;

public class ExtraRelicReward extends CustomReward {
    public static String ID = "encountermod:ExtraRelicReward";
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public ExtraRelicReward(AbstractRelic relic) {
        super(relic.img, TEXT[0] + relic.name ,ENCOUNTERMOD_IDEAREWARD);
        this.relic = relic;
    }

    @Override
    public boolean claimReward() {
        if (EncounterMod.ideaCount == 0) {
            return false;
        }
        EncounterMod.ideaCount --;
        relic.instantObtain();
        CardCrawlGame.metricData.addRelicObtainData(relic);
        return true;
    }

    @Override
    public void render(SpriteBatch sb) {
        //Copied from BaseMod render
        if (this.hb.hovered) {
            sb.setColor(new Color(0.4F, 0.6F, 0.6F, 1.0F));
        } else {
            sb.setColor(new Color(0.5F, 0.6F, 0.6F, 0.8F));
        }

        if (this.hb.clickStarted) {
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, (float) Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.xScale * 0.98F, Settings.scale * 0.98F, 0.0F, 0, 0, 464, 98, false, false);
        } else {
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, (float)Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 464, 98, false, false);
        }

        if (this.flashTimer != 0.0F) {
            sb.setColor(0.6F, 1.0F, 1.0F, this.flashTimer * 1.5F);
            sb.setBlendFunction(770, 1);
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, (float)Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.scale * 1.03F, Settings.scale * 1.15F, 0.0F, 0, 0, 464, 98, false, false);
            sb.setBlendFunction(770, 771);
        }

        relic.renderWithoutAmount(sb, new Color(0.0F, 0.0F, 0.0F, 0.25F));

        Color c = Settings.CREAM_COLOR.cpy();
        if (hb.hovered) {
            c = Settings.GOLD_COLOR.cpy();
        }

        FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, this.text, (float)Settings.WIDTH * 0.434F, this.y + 5.0F * Settings.scale, 1000.0F * Settings.scale, 0.0F, c);
        if (!hb.hovered) {
            for (AbstractGameEffect e : this.effects) {
                e.render(sb);
            }
        }

        hb.render(sb);
    }
}
