package net.qbar.common.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;

import javax.annotation.Nullable;

public class ItemPunchedCard extends ItemBase
{
    public ItemPunchedCard()
    {
        super("punched_card");
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = PunchedCardDataManager.getInstance().readFromNBT(stack.getTagCompound());
            if (card.isValid(stack.getTagCompound()))
                card.addInformation(stack, tooltip, flagIn);
            else
                tooltip.add("Card Invalid");
        }
        else
            tooltip.add("Card Empty");
    }
}