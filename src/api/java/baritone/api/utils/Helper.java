/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.api.utils;

import baritone.api.BaritoneAPI;
import baritone.api.utils.gui.BaritoneToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Stream;

/**
 * An ease-of-access interface to provide the {@link MinecraftClient} game instance,
 * chat and console logging mechanisms, and the Baritone chat prefix.
 *
 * @author Brady
 * @since 8/1/2018
 */
public interface Helper {

    /**
     * Instance of {@link Helper}. Used for static-context reference.
     */
    Helper HELPER = new Helper() {};

    /**
     * Instance of the game
     */
    MinecraftClient mc = MinecraftClient.getInstance();

    static Text getPrefix() {
        // Inner text component
        final Calendar now = Calendar.getInstance();
        final boolean xd = now.get(Calendar.MONTH) == Calendar.APRIL && now.get(Calendar.DAY_OF_MONTH) <= 3;
        MutableText baritone = new LiteralText(xd ? "Fabritoe" : BaritoneAPI.getSettings().shortBaritonePrefix.value ? "F" : "Fabritone");
        baritone.setStyle(baritone.getStyle().withFormatting(Formatting.GREEN));

        // Outer brackets
        MutableText prefix = new LiteralText("");
        prefix.setStyle(baritone.getStyle().withFormatting(Formatting.DARK_GREEN));
        prefix.append("[");
        prefix.append(baritone);
        prefix.append("]");

        return prefix;
    }

    /**
     * Send a message to display as a toast popup
     *
     * @param title The title to display in the popup
     * @param message The message to display in the popup
     */
    default void logToast(Text title, Text message) {
        BaritoneToast.addOrUpdate(mc.getToastManager(), title, message, BaritoneAPI.getSettings().toastTimer.value);
    }

    /**
     * Send a message to display as a toast popup
     *
     * @param title The title to display in the popup
     * @param message The message to display in the popup
     */
    default void logToast(String title, String message) {
        logToast(new LiteralText(title), new LiteralText(message));
    }

    /**
     * Send a message to display as a toast popup
     *
     * @param message The message to display in the popup
     */
    default void logToast(String message) {
        logToast(Helper.getPrefix(), new LiteralText(message));
    }

    /**
     * Send a message to chat only if chatDebug is on
     *
     * @param message The message to display in chat
     */
    default void logDebug(String message) {
        if (!BaritoneAPI.getSettings().chatDebug.value) {
            //System.out.println("Suppressed debug message:");
            //System.out.println(message);
            return;
        }
        // We won't log debug chat into toasts
        // Because only a madman would want that extreme spam -_-
        logDirect(message, false);
    }

    /**
     * Send components to chat with the [Fabritone] prefix
     *
     * @param logAsToast Whether to log as a toast notification
     * @param components The components to send
     */
    default void logDirect(boolean logAsToast, Text... components) {
        MutableText component = new LiteralText("");
        if (!logAsToast) {
            // If we are not logging as a Toast
            // Append the Prefix to the base component line
            component.append(getPrefix());
            component.append(new LiteralText(" "));
        }
        Arrays.asList(components).forEach(component::append);
        if (logAsToast) {
            logToast(getPrefix(), component);
        } else {
            mc.execute(() -> BaritoneAPI.getSettings().logger.value.accept(component));
        }
    }

    /**
     * Send components to chat with the [Fabritone] prefix
     *
     * @param components The components to send
     */
    default void logDirect(Text... components) {
        logDirect(BaritoneAPI.getSettings().logAsToast.value, components);
    }

    /**
     * Send a message to chat regardless of chatDebug (should only be used for critically important messages, or as a
     * direct response to a chat command)
     *
     * @param message The message to display in chat
     * @param color   The color to print that message in
     * @param logAsToast Whether to log as a toast notification
     */
    default void logDirect(String message, Formatting color, boolean logAsToast) {
        Stream.of(message.split("\n")).forEach(line -> {
            MutableText component = new LiteralText(line.replace("\t", "    "));
            component.setStyle(component.getStyle().withFormatting(color));
            logDirect(logAsToast, component);
        });
    }

    /**
     * Send a message to chat regardless of chatDebug (should only be used for critically important messages, or as a
     * direct response to a chat command)
     *
     * @param message The message to display in chat
     * @param color   The color to print that message in
     */
    default void logDirect(String message, Formatting color) {
        logDirect(message, color, BaritoneAPI.getSettings().logAsToast.value);
    }

    /**
     * Send a message to chat regardless of chatDebug (should only be used for critically important messages, or as a
     * direct response to a chat command)
     *
     * @param message The message to display in chat
     * @param logAsToast Whether to log as a toast notification
     */
    default void logDirect(String message, boolean logAsToast) {
        logDirect(message, Formatting.GRAY, logAsToast);
    }

    /**
     * Send a message to chat regardless of chatDebug (should only be used for critically important messages, or as a
     * direct response to a chat command)
     *
     * @param message The message to display in chat
     */
    default void logDirect(String message) {
        logDirect(message, BaritoneAPI.getSettings().logAsToast.value);
    }
}
