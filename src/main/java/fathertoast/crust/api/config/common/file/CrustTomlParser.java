package fathertoast.crust.api.config.common.file;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.toml.TomlParser;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.common.core.Crust;

import java.io.Reader;

/**
 * A simple toml parser implementation that allows the config spec to perform some additional actions on load.
 */
public class CrustTomlParser implements ConfigParser<CommentedConfig> {
    
    /** The actual parser. */
    private final TomlParser WRAPPED_PARSER = new TomlParser();
    
    /** The config spec that drives this parser. */
    private final CrustConfigSpec CONFIG_SPEC;
    
    CrustTomlParser( CrustConfigSpec spec ) { CONFIG_SPEC = spec; }
    
    /** @return The format supported by this parser. */
    @Override
    public ConfigFormat<CommentedConfig> getFormat() { return WRAPPED_PARSER.getFormat(); }
    
    /**
     * Parses a configuration.
     *
     * @param reader The reader to parse
     * @return A new Config
     * @throws ParsingException If an error occurs.
     */
    @Override
    public CommentedConfig parse( Reader reader ) {
        Crust.LOG.error( "Attempting to parse NEW config file! ({})", ConfigUtil.toRelativePath( CONFIG_SPEC.getFile() ) );
        throw new ParsingException( "Attempted to generate new config! This is not supported." );
    }
    
    /**
     * Parses a configuration.
     *
     * @param reader      The reader to parse.
     * @param destination The config where to put the data.
     * @param parsingMode The set parsing mode.
     */
    @Override
    public void parse( Reader reader, Config destination, ParsingMode parsingMode ) {
        Crust.LOG.debug( "Parsing config file! ({}{})", CONFIG_SPEC.NAME, CrustConfigFormat.FILE_EXT );
        WRAPPED_PARSER.parse( reader, destination, parsingMode );
        CONFIG_SPEC.onLoad();
    }
}