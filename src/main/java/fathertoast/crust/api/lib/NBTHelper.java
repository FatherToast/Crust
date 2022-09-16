package fathertoast.crust.api.lib;

import net.minecraft.nbt.CompoundNBT;

@SuppressWarnings( "unused" )
public class NBTHelper {
    
    /** The ID used to match String tags. Equal to {@link net.minecraft.nbt.ListNBT#getId()}. */
    public static final int ID_LIST = 9;
    /** The ID used to match String tags. Equal to {@link net.minecraft.nbt.CompoundNBT#getId()}. */
    public static final int ID_COMPOUND = 10;
    
    /** The ID used to match String tags. Equal to {@link net.minecraft.nbt.StringNBT#getId()}. */
    public static final int ID_STRING = 8;
    
    /**
     * The ID used to match any numerical-type tags (including Boolean, which is a Byte in NBT format).
     * Primarily used when testing tag existence within a Compound.
     *
     * @see CompoundNBT#contains(String, int)
     */
    public static final int ID_NUMERICAL = 99;
    /** The ID used to match Byte (and Boolean) tags. Equal to {@link net.minecraft.nbt.ByteNBT#getId()}. */
    public static final int ID_BYTE = 1;
    /** The ID used to match Short tags. Equal to {@link net.minecraft.nbt.ShortNBT#getId()}. */
    public static final int ID_SHORT = 2;
    /** The ID used to match Int tags. Equal to {@link net.minecraft.nbt.IntNBT#getId()}. */
    public static final int ID_INT = 3;
    /** The ID used to match Long tags. Equal to {@link net.minecraft.nbt.LongNBT#getId()}. */
    public static final int ID_LONG = 4;
    /** The ID used to match Float tags. Equal to {@link net.minecraft.nbt.FloatNBT#getId()}. */
    public static final int ID_FLOAT = 5;
    /** The ID used to match Double tags. Equal to {@link net.minecraft.nbt.DoubleNBT#getId()}. */
    public static final int ID_DOUBLE = 6;
    
    /** The ID used to match Byte Array tags. Equal to {@link net.minecraft.nbt.ByteArrayNBT#getId()}. */
    public static final int ID_BYTE_ARRAY = 7;
    /** The ID used to match Int Array tags. Equal to {@link net.minecraft.nbt.IntArrayNBT#getId()}. */
    public static final int ID_INT_ARRAY = 11;
    /** The ID used to match Long Array tags. Equal to {@link net.minecraft.nbt.LongArrayNBT#getId()}. */
    public static final int ID_LONG_ARRAY = 12;
}