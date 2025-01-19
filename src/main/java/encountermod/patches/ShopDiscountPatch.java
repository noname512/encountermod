package encountermod.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import encountermod.relics.VisionsOfTheEraOfProsperity;

import java.util.ArrayList;

public class ShopDiscountPatch {
    @SpirePatch(clz = ShopScreen.class, method = "init")
    public static class InitPatch {
        @SpirePostfixPatch
        public static void Postfix(ShopScreen _inst, ArrayList<AbstractCard> coloredCards, ArrayList<AbstractCard> colorlessCards) {
            if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
                _inst.applyDiscount(0.7F, true);
            }
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "purgeCard")
    public static class PurgeCardPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID) && !AbstractDungeon.player.hasRelic("Smiling Mask")) {
                ShopScreen.actualPurgeCost = MathUtils.round(ShopScreen.actualPurgeCost * 0.7F);
            }
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "getNewPrice", paramtypez = {StoreRelic.class})
    public static class GetNewPriceRelicPatch {
        @SpireInsertPatch(rloc = 17, localvars = {"retVal"})
        public static void Insert(ShopScreen _inst, StoreRelic r, @ByRef int[] retVal) {
            if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
                retVal[0] = MathUtils.round(0.7F * retVal[0]);
            }
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "getNewPrice", paramtypez = {StorePotion.class})
    public static class GetNewPricePotionPatch {
        @SpireInsertPatch(rloc = 17, localvars = {"retVal"})
        public static void Insert(ShopScreen _inst, StorePotion r, @ByRef int[] retVal) {
            if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
                retVal[0] = MathUtils.round(0.7F * retVal[0]);
            }
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "setPrice", paramtypez = {AbstractCard.class})
    public static class SetPricePatch {
        @SpireInsertPatch(rloc = 21, localvars = {"tmpPrice"})
        public static void Insert(ShopScreen _inst, AbstractCard card, @ByRef float[] tmpPrice) {
            if (AbstractDungeon.player.hasRelic(VisionsOfTheEraOfProsperity.ID)) {
                tmpPrice[0] *= 0.7F;
            }
        }
    }
}