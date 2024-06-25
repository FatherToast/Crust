package fathertoast.crust.api.config.common;

import com.electronwill.nightconfig.core.file.FileConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A static helper class that contains some helper utilities for making pretty configs.
 */
@SuppressWarnings( "unused" )
public final class ConfigUtil {
    
    /** Logger instance for the Crust Config API. */
    public static final Logger LOG = LogManager.getLogger( "crust/configs" );
    
    /** The plus or minus symbol (+/-). */
    public static final String PLUS_OR_MINUS = "\u00b1";
    /** The less than or equal to symbol (<=). */
    public static final String LESS_OR_EQUAL = "\u2264";
    /** The greater than or equal to symbol (>=). */
    public static final String GREATER_OR_EQUAL = "\u2265";
    
    /** @return The string with all spaces replaced by underscores. Useful for file names. */
    public static String toLowerCaseNoSpaces( String str ) { return noSpaces( str.toLowerCase( Locale.ROOT ) ); }
    
    /** @return The string with all spaces replaced by underscores. Useful for file names. */
    public static String noSpaces( String str ) { return str.replace( ' ', '_' ); }
    
    /** @return The string converted from camel case to lower space case; e.g., "UpperCamelCase" returns "upper camel case". */
    public static String camelCaseToLowerSpace( String str ) {
        final StringBuilder spacedStr = new StringBuilder();
        for( int i = 0; i < str.length(); i++ ) {
            final char c = str.charAt( i );
            if( Character.isUpperCase( c ) ) {
                if( i > 0 ) spacedStr.append( ' ' );
                spacedStr.append( Character.toLowerCase( c ) );
            }
            else {
                spacedStr.append( c );
            }
        }
        return spacedStr.toString();
    }
    
    /** @return The string converted from camel case to lower underscore case; e.g., "UpperCamelCase" returns "upper_camel_case". */
    public static String camelCaseToLowerUnderscore( String str ) { return noSpaces( camelCaseToLowerSpace( str ) ); }
    
    /** @return The string converted from space case to lower camel case; e.g., "lower space case" returns "lowerSpaceCase". */
    public static String spaceCaseToLowerCamel( String str ) { return underscoreCaseToLowerCamel( noSpaces( str ) ); }
    
    /** @return The string converted from underscore case to lower camel case; e.g., "lower_underscore_case" returns "lowerUnderscoreCase". */
    public static String underscoreCaseToLowerCamel( String str ) {
        final StringBuilder camelStr = new StringBuilder();
        boolean upper = false;
        for( int i = 0; i < str.length(); i++ ) {
            final char c = str.charAt( i );
            if( c == '_' ) {
                upper = true;
            }
            else if( upper ) {
                upper = false;
                camelStr.append( Character.toUpperCase( c ) );
            }
            else {
                camelStr.append( Character.toLowerCase( c ) );
            }
        }
        return camelStr.toString();
    }
    
    /** @return The string, but with the first character changed to upper case. */
    public static String properCase( String str ) { return str.substring( 0, 1 ).toUpperCase() + str.substring( 1 ); }
    
    /** @return A string representation of the config file from the game directory. */
    public static String toRelativePath( FileConfig configFile ) { return toRelativePath( configFile.getFile() ); }
    
    /** @return A string representation of the file from the game directory. */
    public static String toRelativePath( File gameFile ) { return FMLPaths.GAMEDIR.get().relativize( gameFile.toPath() ).toString(); }
    
    /** @return Returns a dynamic registry entry as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceKey<?> regKey ) { return regKey == null ? "null" : toString( regKey.location() ); }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }

    /** @return Returns the tag key as a string, or "null" if it is null. */
    public static String toString( @Nullable TagKey<?> tagKey ) { return tagKey == null ? "null" : ("#" + tagKey.location() ); }
    
    /**
     * @param str    The string we wish to wrap.
     * @param length Maximum characters to fit in each line.
     * @return A new list containing the lines of the wrapped string.
     */
    public static List<String> wrap( String str, int length ) {
        ArrayList<String> wrappedLines = new ArrayList<>();
        wrap( wrappedLines, str, length );
        return wrappedLines;
    }
    
    /**
     * @param wrappedLines The list to append parsed lines to.
     * @param str          The string to wrap.
     * @param length       Number of characters to fit in.
     */
    public static void wrap( List<String> wrappedLines, String str, int length ) {
        wrap( wrappedLines, str, length, "  ", false );
    }
    
    /**
     * @param wrappedLines  The list to append parsed lines to.
     * @param str           The string to wrap.
     * @param length        Number of characters to fit in.
     * @param hangingIndent The hanging indent to apply. Null disables all indentation.
     * @param hardWrap      If true, this will forcibly wrap "words" that are longer than the wrap length.
     */
    public static void wrap( List<String> wrappedLines, String str, int length, @Nullable String hangingIndent, boolean hardWrap ) {
        int originalLength = str.length();
        
        if( originalLength <= length ) {
            wrappedLines.add( str );
            return;
        }
        
        int index = 0;
        
        // Determine hanging indent
        if( hangingIndent == null ) hangingIndent = "";
        else {
            int indentLength = 0;
            do {
                if( Character.isWhitespace( str.charAt( index ) ) ) indentLength++;
                else break;
                index++;
            }
            while( index < originalLength );
            if( indentLength > 0 ) hangingIndent = str.substring( 0, indentLength ) + hangingIndent;
            length -= hangingIndent.length();
        }
        if( length < 2 ) length = 2;
        
        // Perform the actual wrapping
        boolean hasWrapped = false;
        int lastValidWrapIndex = -1;
        int indexOfLastWrap = index - 1;
        while( index < originalLength ) {
            char c = str.charAt( index );
            boolean whitespace = Character.isWhitespace( c );
            
            // Time to wrap
            if( !whitespace && index - indexOfLastWrap > length ) {
                if( lastValidWrapIndex >= 0 ) {
                    wrappedLines.add( extractLine( hasWrapped, str, hangingIndent, indexOfLastWrap, lastValidWrapIndex + 1 ) );
                    indexOfLastWrap = lastValidWrapIndex;
                    hasWrapped = true;
                }
                else if( hardWrap ) {
                    wrappedLines.add( extractLine( hasWrapped, str, hangingIndent, indexOfLastWrap, index - 1 ) + "-" );
                    indexOfLastWrap = index - 2;
                    hasWrapped = true;
                }
            }
            
            // Identify preferred wrap point
            if( whitespace || c == '-' ) lastValidWrapIndex = index;
            
            index++;
        }
        wrappedLines.add( extractLine( hasWrapped, str, hangingIndent, indexOfLastWrap, originalLength ) );
    }
    
    /** @return A complete line, given the current state of the wrap method. */
    private static String extractLine( boolean hasWrapped, String str, String hangingIndent, int indexOfLastWrap, int wrapToIndex ) {
        return hasWrapped ? hangingIndent + str.substring( indexOfLastWrap + 1, wrapToIndex ).trim() :
                str.substring( 0, wrapToIndex ).replaceFirst( "\\s++$", "" );
    }
}