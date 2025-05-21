package encountermod.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;

import java.util.ArrayList;

public class FriendlyChatPower extends AbstractPower {
    public static final String POWER_ID = "encountermod:FriendlyChatPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public ArrayList<AbstractCard> list = new ArrayList<>();
    boolean triggered = false;
    public FriendlyChatPower(AbstractCreature owner) {
        this.ID = POWER_ID;
        this.name = NAME;
        this.type = PowerType.BUFF;
        this.owner = owner;
        this.amount = owner.currentHealth;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/FriendlyChatPower 84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("resources/encountermod/images/powers/FriendlyChatPower 32.png"), 0, 0, 32, 32);
        updateDescription();
        priority = 1000;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public int onLoseHp(int damageAmount) {
        if (damageAmount >= owner.currentHealth) {
            AbstractDungeon.actionManager.cleanCardQueue();
            AbstractDungeon.effectList.add(new DeckPoofEffect(64.0F * Settings.scale, 64.0F * Settings.scale, true));
            AbstractDungeon.effectList.add(new DeckPoofEffect((float)Settings.WIDTH - 64.0F * Settings.scale, 64.0F * Settings.scale, false));
            AbstractDungeon.overlayMenu.hideCombatPanels();
            AbstractDungeon.getCurrRoom().endBattle();
            triggered = true;
        }
        if (triggered) {
            return 0;
        }
        return damageAmount;
    }

    @Override
    public void onVictory() {
        flash();
        owner.currentHealth = amount;
    }
}