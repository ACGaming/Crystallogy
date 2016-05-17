package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConditionOr implements ICondition
{
    final Iterable<ICondition> conditions;

    public ConditionOr(Iterable<ICondition> p_i46563_1_)
    {
        this.conditions = p_i46563_1_;
    }

    public Predicate<IBlockState> getPredicate(final BlockStateContainer blockState)
    {
        return Predicates.or(Iterables.transform(this.conditions, new Function<ICondition, Predicate<IBlockState>>()
        {
            public Predicate<IBlockState> apply(ICondition p_apply_1_)
            {
                return p_apply_1_ == null ? null : p_apply_1_.getPredicate(blockState);
            }
        }));
    }
}