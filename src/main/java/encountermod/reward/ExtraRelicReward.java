package encountermod.reward;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import encountermod.EncounterMod;

public class ExtraRelicReward extends CustomReward {
    public static String ID = "encountermod:ExtraRelicReward";
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public ExtraRelicReward(AbstractRelic relic) {
        super(relic.img, TEXT[0] + relic.name, RewardType.RELIC);
        this.relic = relic;
    }

    @Override
    public boolean claimReward() {
        if (EncounterMod.ideaCount == 0) {
            return false;
        }
        EncounterMod.ideaCount--;
        relic.instantObtain();
        CardCrawlGame.metricData.addRelicObtainData(relic);
        if (relic.tier == AbstractRelic.RelicTier.COMMON) {
            AbstractDungeon.commonRelicPool.remove(relic.relicId);
        }
        if (relic.tier == AbstractRelic.RelicTier.UNCOMMON) {
            AbstractDungeon.uncommonRelicPool.remove(relic.relicId);
        }
        if (relic.tier == AbstractRelic.RelicTier.RARE) {
            AbstractDungeon.rareRelicPool.remove(relic.relicId);
        }
        return true;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (EncounterMod.ideaCount == 0) {
            sb.setColor(new Color(0.3F, 0.4F, 0.4F, 0.8F));
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, (float)Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 464, 98, false, false);

            sb.setColor(Color.WHITE);
            this.relic.renderWithoutAmount(sb, new Color(0.0F, 0.0F, 0.0F, 0.25F));

            if (this.hb.hovered) {
                relic.renderTip(sb);
            }

            Color color = Settings.CREAM_COLOR;
            FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, this.text, Settings.WIDTH * 0.434F, this.y + 5.0F * Settings.scale, 1000.0F * Settings.scale, 0.0F, color);
        } else {
            if (this.hb.hovered) {
                sb.setColor(new Color(0.4F, 0.6F, 0.6F, 1.0F));
            } else {
                sb.setColor(new Color(0.5F, 0.6F, 0.6F, 0.8F));
            }

            if (this.hb.clickStarted) {
                sb.draw(ImageMaster.REWARD_SCREEN_ITEM, (float) Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.xScale * 0.98F, Settings.scale * 0.98F, 0.0F, 0, 0, 464, 98, false, false);
            } else {
                sb.draw(ImageMaster.REWARD_SCREEN_ITEM, (float) Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 464, 98, false, false);
            }

            if (this.flashTimer != 0.0F) {
                sb.setColor(0.6F, 1.0F, 1.0F, this.flashTimer * 1.5F);
                sb.setBlendFunction(770, 1);
                sb.draw(ImageMaster.REWARD_SCREEN_ITEM, (float) Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.xScale * 1.03F, Settings.scale * 1.15F, 0.0F, 0, 0, 464, 98, false, false);
                sb.setBlendFunction(770, 771);
            }

            sb.setColor(Color.WHITE);
            this.relic.renderWithoutAmount(sb, new Color(0.0F, 0.0F, 0.0F, 0.25F));
            if (this.hb.hovered) {
                relic.renderTip(sb);
            }

            Color color;
            if (this.hb.hovered) {
                color = Settings.GOLD_COLOR;
            } else {
                color = Settings.CREAM_COLOR;
            }

            if (this.redText) {
                color = Settings.RED_TEXT_COLOR;
            }

            FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, this.text, Settings.WIDTH * 0.434F, this.y + 5.0F * Settings.scale, 1000.0F * Settings.scale, 0.0F, color);
            if (!this.hb.hovered) {
                for (AbstractGameEffect e : this.effects) {
                    e.render(sb);
                }
            }
        }

        if (Settings.isControllerMode) {
            this.renderReticle(sb, this.hb);
        }

        this.hb.render(sb);
    }
}
