package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.file.TomlHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Similar to {@link StringListField}, except this field type requires a String predicate
 * to do custom validation of each line in the String list.<br><br>
 * Useful when you want to parse each line in the String list with restrictions without going too crazy.
 */
public class PredicateStringListField extends StringListField {

    /**
     * A predicate for validating each line in the String list.<br>
     * Should return true if the tested String is considered valid.
     */
    private final Predicate<String> lineValidator;


    public PredicateStringListField( String key, String typeName, List<String> defaultValue,
                                     Predicate<String> lineValidator, @Nullable String... description ) {
        super( key, typeName, defaultValue, description );
        Objects.requireNonNull( lineValidator );
        this.lineValidator = lineValidator;
    }

    public PredicateStringListField( String key, List<String> defaultValue, Predicate<String> lineInvalidator, @Nullable String... description ) {
        this( key, "String", defaultValue, lineInvalidator, description );
    }

    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        List<String> rawParsed = TomlHelper.parseStringList( raw );
        value = loadValidated( rawParsed, lineValidator );
    }

    /**
     * Called when this field's String list value is being loaded.<br><br>
     * Uses this field's line validator predicate to determine if the
     * tested line is valid or not. Invalid lines are discarded from the field's
     * String list value.
     *
     * @param strings The List of Strings that was just parsed by {@link TomlHelper#parseStringList(Object)}
     *                in {@link PredicateStringListField#load(Object)}.
     * @param lineValidator This field's line validator predicate.
     */
    protected List<String> loadValidated( List<String> strings, Predicate<String> lineValidator ) {
        strings.removeIf( (line) -> !lineValidator.test( line ) );
        return strings;
    }
}
