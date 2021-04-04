package de.craftedcrime.bungee.servermanager.utils;
/*
 * Created by ian on 03.03.21
 * Location: de.craftedcrime.bungee.servermanager.utils
 * Created for the project servermanager with the name GeneralUtils
 */

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.regex.Pattern;

public class GeneralUtils {

    public GeneralUtils() {
    }

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public TextComponent getCommandSuggestion(String baseText, String commandToBeSuggested, String hovertext) {
        TextComponent ret = new TextComponent(baseText);
        ret.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandToBeSuggested));
        ret.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hovertext)));
        return ret;
    }

    public TextComponent getCommandExecute(String baseText, String commandToBeExecuted, String hovertext) {
        TextComponent ret = new TextComponent(baseText);
        ret.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandToBeExecuted));
        ret.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hovertext)));
        return ret;
    }

    public TextComponent openURLThing(String baseText, String url, String hovertext) {
        TextComponent ret = new TextComponent(baseText);
        ret.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        ret.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hovertext)));
        return ret;
    }

}
