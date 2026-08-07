package fathertoast.crust.api.config.common.file.action;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.file.CrustConfigFormat;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;

import java.util.List;

/** Represents a write-only spec action. */
public abstract class FormatAction implements ISpecAction {
    
    /** Called when the config is loaded. */
    @Override // Formatting actions do not affect file reading
    public final boolean onLoad() { return false; }
    
    
    // ---- Format Action Implementations ---- //
    
    /** Represents a variable number of new lines. */
    public static class NewLines extends FormatAction {
        
        /** The number of new lines to write. */
        protected final int COUNT;
        
        /** Create a new comment action that will insert a number of new lines. */
        public NewLines( int count ) { COUNT = count; }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            for( int i = 0; i < COUNT; i++ ) writer.writeNewLine( output );
        }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {
            guiSpec.newLine( COUNT );
        }
    }
    
    /** Represents a variable number of new lines. Ignored in the editor. */
    public static class FileOnlyNewLines extends NewLines {
        
        /** Create a new comment action that will insert a number of new lines. */
        public FileOnlyNewLines( int count ) { super( count ); }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {}
    }
    
    /** Represents a variable number of indent increases or decreases. */
    public static class Indent extends FormatAction {
        
        /** The amount to change the indent by. */
        protected final int AMOUNT;
        
        /** Create a new indent action that will modify the current indent level. */
        public Indent( int amount ) { AMOUNT = amount; }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) { writer.changeIndentLevel( AMOUNT ); }
        
        /** Called when the config edit screen is opened. */
        @Override // We don't indent the GUI; maybe eventually it would be nice for this to create "foldable" sections
        public void initGui( ICrustConfigGuiSpec guiSpec ) {}
    }
    
    /** Represents a comment. Fully printed in both files and the editor. */
    public static class Comment extends FormatAction {
        
        /** The comment. */
        protected final List<String> COMMENT;
        
        /** Create a new comment action that will insert a comment. */
        public Comment( List<String> comment ) { COMMENT = comment; }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) { writer.writeComment( COMMENT, output ); }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {
            guiSpec.comment( COMMENT );
        }
    }
    
    /** Represents a comment. Fully printed in files. Only the title prints in the editor, with the rest as a tooltip. */
    public static class TitledComment extends Comment {
        
        /** The comment title. */
        protected final String TITLE;
        
        /** Create a new comment action that will insert a comment. */
        public TitledComment( String title, List<String> comment ) {
            super( comment );
            TITLE = title;
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            writer.writeComment( TITLE + ":", output );
            super.write( writer, output );
        }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {
            guiSpec.titledComment( TITLE, COMMENT );
        }
    }
    
    /** Represents a comment. Fully printed in files, but NOT printed in the editor. */
    public static class FileOnlyComment extends Comment {
        
        /** Create a new comment action that will insert a comment. */
        public FileOnlyComment( List<String> comment ) { super( comment ); }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {}
    }
    
    /** Represents a file header comment. */
    public static class FileHeader extends FormatAction {
        
        /** The spec this action belongs to. */
        protected final CrustConfigSpec PARENT;
        /** The file comment. */
        protected final List<String> COMMENT;
        
        /** Create a new header action that will insert the opening file comment. */
        public FileHeader( CrustConfigSpec parent, List<String> comment ) {
            PARENT = parent;
            COMMENT = comment;
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            writer.writeComment( PARENT.MANAGER.MOD_ID + ":" + PARENT.NAME + CrustConfigFormat.FILE_EXT,
                    output );
            writer.writeComment( COMMENT, output );
            
            writer.increaseIndentLevel();
        }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {
            // File name, etc. is displayed in the screen header
            guiSpec.comment( COMMENT, 0xFFFFFF );
        }
    }
    
    /** Represents an appendix header comment. */
    public static class AppendixHeader extends FormatAction {
        
        /** The appendix comment. */
        protected final List<String> COMMENT;
        
        /** Create a new appendix header action that will insert a closing file comment. */
        public AppendixHeader( List<String> comment ) { COMMENT = comment; }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            writer.decreaseIndentLevel();
            
            writer.writeNewLine( output );
            writer.writeNewLine( output );
            writer.writeComment( "Appendix:", output );
            writer.writeComment( COMMENT, output );
            writer.writeNewLine( output );
            
            writer.increaseIndentLevel();
        }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {
            guiSpec.newLine( 2 )
                    .header( "Appendix", null )
                    .comment( COMMENT )
                    .newLine();
        }
    }
    
    /** Represents a category comment. */
    public static class Category extends FormatAction {
        
        /** The spec this action belongs to. */
        protected final CrustConfigSpec PARENT;
        /** The category name. */
        protected final String CATEGORY;
        /** The category comment. */
        protected final List<String> COMMENT;
        
        /** Create a new category action that will insert the category comment. */
        public Category( CrustConfigSpec parent, String categoryName, List<String> comment ) {
            PARENT = parent;
            CATEGORY = categoryName;
            COMMENT = comment;
            PARENT.loadingCategory = categoryName + ".";
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            PARENT.loadingCategory = CATEGORY + ".";
            writer.decreaseIndentLevel();
            
            writer.writeNewLine( output );
            writer.writeNewLine( output );
            writer.writeComment( "Category: " + CATEGORY, output );
            writer.writeComment( COMMENT, output );
            
            writer.increaseIndentLevel();
            writer.writeNewLine( output );
        }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {
            PARENT.loadingCategory = CATEGORY + ".";
            guiSpec.newLine( 2 )
                    .header( CATEGORY, COMMENT )
                    .newLine();
        }
    }
    
    /** Represents a category comment. */
    public static class SimpleCategory extends FormatAction {
        
        /** The spec this action belongs to. */
        protected final CrustConfigSpec PARENT;
        /** The category name. */
        protected final String CATEGORY;
        /** The category comment. */
        protected final List<String> COMMENT;
        
        /** Create a new category action that will insert the category comment. */
        public SimpleCategory( CrustConfigSpec parent, String categoryName, List<String> comment ) {
            PARENT = parent;
            CATEGORY = categoryName;
            COMMENT = comment;
            PARENT.loadingCategory = categoryName + ".";
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            PARENT.loadingCategory = CATEGORY + ".";
            
            writer.writeNewLine( output );
            writer.writeNewLine( output );
            writer.writeComment( "Category: " + CATEGORY, output );
            writer.writeComment( COMMENT, output );
            writer.writeNewLine( output );
        }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {
            PARENT.loadingCategory = CATEGORY + ".";
            guiSpec.newLine( 2 )
                    .header( CATEGORY, COMMENT )
                    .newLine();
        }
    }
    
    /** Represents a subcategory comment. */
    public static class Subcategory extends FormatAction {
        
        /** The spec this action belongs to. */
        protected final CrustConfigSpec PARENT;
        /** The subcategory name. */
        protected final String SUBCATEGORY;
        /** The subcategory comment. */
        protected final List<String> COMMENT;
        
        /** Create a new subcategory action that will insert the subcategory comment. */
        public Subcategory( CrustConfigSpec parent, String subcategoryName, List<String> comment ) {
            PARENT = parent;
            SUBCATEGORY = subcategoryName;
            COMMENT = comment;
        }
        
        /** Called when the config is saved. */
        @Override
        public void write( CrustTomlWriter writer, CharacterOutput output ) {
            writer.decreaseIndentLevel();
            
            writer.writeNewLine( output );
            writer.writeComment( "Subcategory: " + SUBCATEGORY, output );
            writer.writeComment( COMMENT, output );
            
            writer.increaseIndentLevel();
            writer.writeNewLine( output );
        }
        
        /** Called when the config edit screen is opened. */
        @Override
        public void initGui( ICrustConfigGuiSpec guiSpec ) {
            guiSpec.newLine()
                    .header( PARENT.loadingCategory + SUBCATEGORY, COMMENT, 0xFFFFFF )
                    .newLine();
        }
    }
}