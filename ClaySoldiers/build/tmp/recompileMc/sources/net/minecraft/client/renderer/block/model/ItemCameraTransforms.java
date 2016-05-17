package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Quaternion;

/*
 * @deprecated use {@link net.minecraftforge.client.model.IPerspectiveAwareModel} instead
 */
@SideOnly(Side.CLIENT)
public class ItemCameraTransforms
{
    public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
    public static float offsetTranslateX = 0.0F;
    public static float offsetTranslateY = 0.0F;
    public static float offsetTranslateZ = 0.0F;
    public static float offsetRotationX = 0.0F;
    public static float offsetRotationY = 0.0F;
    public static float offsetRotationZ = 0.0F;
    public static float offsetScaleX = 0.0F;
    public static float offsetScaleY = 0.0F;
    public static float offsetScaleZ = 0.0F;
    public final ItemTransformVec3f thirdperson_left;
    public final ItemTransformVec3f thirdperson_right;
    public final ItemTransformVec3f firstperson_left;
    public final ItemTransformVec3f firstperson_right;
    public final ItemTransformVec3f head;
    public final ItemTransformVec3f gui;
    public final ItemTransformVec3f ground;
    public final ItemTransformVec3f fixed;

    private ItemCameraTransforms()
    {
        this(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    @Deprecated
    public ItemCameraTransforms(ItemCameraTransforms transforms)
    {
        this.thirdperson_left = transforms.thirdperson_left;
        this.thirdperson_right = transforms.thirdperson_right;
        this.firstperson_left = transforms.firstperson_left;
        this.firstperson_right = transforms.firstperson_right;
        this.head = transforms.head;
        this.gui = transforms.gui;
        this.ground = transforms.ground;
        this.fixed = transforms.fixed;
    }

    @Deprecated
    public ItemCameraTransforms(ItemTransformVec3f p_i46569_1_, ItemTransformVec3f p_i46569_2_, ItemTransformVec3f p_i46569_3_, ItemTransformVec3f p_i46569_4_, ItemTransformVec3f p_i46569_5_, ItemTransformVec3f p_i46569_6_, ItemTransformVec3f p_i46569_7_, ItemTransformVec3f p_i46569_8_)
    {
        this.thirdperson_left = p_i46569_1_;
        this.thirdperson_right = p_i46569_2_;
        this.firstperson_left = p_i46569_3_;
        this.firstperson_right = p_i46569_4_;
        this.head = p_i46569_5_;
        this.gui = p_i46569_6_;
        this.ground = p_i46569_7_;
        this.fixed = p_i46569_8_;
    }

    public void applyTransform(ItemCameraTransforms.TransformType type)
    {
        applyTransformSide(this.getTransform(type), false);
    }

    public static void applyTransformSide(ItemTransformVec3f vec, boolean leftHand)
    {
        if (vec != ItemTransformVec3f.DEFAULT)
        {
            int i = leftHand ? -1 : 1;
            GlStateManager.translate((float)i * (offsetTranslateX + vec.translation.x), offsetTranslateY + vec.translation.y, offsetTranslateZ + vec.translation.z);
            float f = offsetRotationX + vec.rotation.x;
            float f1 = offsetRotationY + vec.rotation.y;
            float f2 = offsetRotationZ + vec.rotation.z;

            if (leftHand)
            {
                f1 = -f1;
                f2 = -f2;
            }

            GlStateManager.rotate(makeQuaternion(f, f1, f2));
            GlStateManager.scale(offsetScaleX + vec.scale.x, offsetScaleY + vec.scale.y, offsetScaleZ + vec.scale.z);
        }
    }

    private static Quaternion makeQuaternion(float p_188035_0_, float p_188035_1_, float p_188035_2_)
    {
        float f = p_188035_0_ * 0.017453292F;
        float f1 = p_188035_1_ * 0.017453292F;
        float f2 = p_188035_2_ * 0.017453292F;
        float f3 = MathHelper.sin(0.5F * f);
        float f4 = MathHelper.cos(0.5F * f);
        float f5 = MathHelper.sin(0.5F * f1);
        float f6 = MathHelper.cos(0.5F * f1);
        float f7 = MathHelper.sin(0.5F * f2);
        float f8 = MathHelper.cos(0.5F * f2);
        return new Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7, f4 * f6 * f8 - f3 * f5 * f7);
    }

    @Deprecated
    public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
    {
        switch (type)
        {
            case THIRD_PERSON_LEFT_HAND:
                return this.thirdperson_left;
            case THIRD_PERSON_RIGHT_HAND:
                return this.thirdperson_right;
            case FIRST_PERSON_LEFT_HAND:
                return this.firstperson_left;
            case FIRST_PERSON_RIGHT_HAND:
                return this.firstperson_right;
            case HEAD:
                return this.head;
            case GUI:
                return this.gui;
            case GROUND:
                return this.ground;
            case FIXED:
                return this.fixed;
            default:
                return ItemTransformVec3f.DEFAULT;
        }
    }

    public boolean hasCustomTransform(ItemCameraTransforms.TransformType type)
    {
        return this.getTransform(type) != ItemTransformVec3f.DEFAULT;
    }

    @SideOnly(Side.CLIENT)
    static class Deserializer implements JsonDeserializer<ItemCameraTransforms>
        {
            public ItemCameraTransforms deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                ItemTransformVec3f itemtransformvec3f = this.func_181683_a(p_deserialize_3_, jsonobject, "thirdperson_righthand");
                ItemTransformVec3f itemtransformvec3f1 = this.func_181683_a(p_deserialize_3_, jsonobject, "thirdperson_lefthand");

                if (itemtransformvec3f1 == ItemTransformVec3f.DEFAULT)
                {
                    itemtransformvec3f1 = itemtransformvec3f;
                }

                ItemTransformVec3f itemtransformvec3f2 = this.func_181683_a(p_deserialize_3_, jsonobject, "firstperson_righthand");
                ItemTransformVec3f itemtransformvec3f3 = this.func_181683_a(p_deserialize_3_, jsonobject, "firstperson_lefthand");

                if (itemtransformvec3f3 == ItemTransformVec3f.DEFAULT)
                {
                    itemtransformvec3f3 = itemtransformvec3f2;
                }

                ItemTransformVec3f itemtransformvec3f4 = this.func_181683_a(p_deserialize_3_, jsonobject, "head");
                ItemTransformVec3f itemtransformvec3f5 = this.func_181683_a(p_deserialize_3_, jsonobject, "gui");
                ItemTransformVec3f itemtransformvec3f6 = this.func_181683_a(p_deserialize_3_, jsonobject, "ground");
                ItemTransformVec3f itemtransformvec3f7 = this.func_181683_a(p_deserialize_3_, jsonobject, "fixed");
                return new ItemCameraTransforms(itemtransformvec3f1, itemtransformvec3f, itemtransformvec3f3, itemtransformvec3f2, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
            }

            private ItemTransformVec3f func_181683_a(JsonDeserializationContext p_181683_1_, JsonObject p_181683_2_, String p_181683_3_)
            {
                return p_181683_2_.has(p_181683_3_) ? (ItemTransformVec3f)p_181683_1_.deserialize(p_181683_2_.get(p_181683_3_), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
            }
        }

    @SideOnly(Side.CLIENT)
    public static enum TransformType implements net.minecraftforge.common.model.IModelPart
    {
        NONE,
        THIRD_PERSON_LEFT_HAND,
        THIRD_PERSON_RIGHT_HAND,
        FIRST_PERSON_LEFT_HAND,
        FIRST_PERSON_RIGHT_HAND,
        HEAD,
        GUI,
        GROUND,
        FIXED;
    }
}