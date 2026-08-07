package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.value.collection.IStringArrayValue;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Implemented by fields that want to be editable in game through
 * {@link fathertoast.crust.api.config.client.gui.screen.EditStringListScreen} and
 * {@link fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider}
 */
@Deprecated( forRemoval = true )
public interface IStringListScreenEditable<T> {
    
    /** Converts the unprocessed value into its displayable string list. */
    default List<String> getAsStringList( T value ) {
        try {
            //noinspection unchecked
            return (List<String>) value;
        }
        catch( ClassCastException ex ) {
            // Not directly assignable, try parsing
        }
        if( value instanceof IStringArrayValue strArrayVal ) {
            return strArrayVal.toStringList();
        }
        return new ArrayList<>();
    }
    
    /** @return This field's line validator, or null if any string is allowed. */
    @Nullable
    default Predicate<String> getLineValidator() { return null; }
}