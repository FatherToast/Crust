package fathertoast.crust.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public class CommandUtil {
    
    public static boolean canUseCommand( CommandDispatcher<CommandSource> dispatcher, Entity user, String command ) {
        StringReader reader = new StringReader( org.apache.commons.lang3.StringUtils.normalizeSpace( command ) );
        if( reader.canRead() && reader.peek() == '/' ) reader.skip();
        CommandSource source = user.createCommandSourceStack();
        
        ParseResults<CommandSource> parse = dispatcher.parse( reader, source );
        return false;//TODO
    }
}