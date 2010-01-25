/*
 * SafeOnline project.
 *
 * Copyright 2006-2008 Lin.k N.V. All rights reserved.
 * Lin.k N.V. proprietary/confidential. Use is subject to license terms.
 */
package com.lyndir.lhunath.snaplog.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.link.safeonline.sdk.auth.filter.LoginManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.*;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;


/**
 * <h2>{@link WicketUtils}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * [description / usage].
 * </p>
 *
 * <p>
 * <i>Sep 17, 2008</i>
 * </p>
 *
 * @author lhunath
 */
public abstract class WicketUtils {

    static final Log LOG = LogFactory.getLog( WicketUtils.class );

    // %[argument_index$][flags][width][.precision][t]conversion
    private static final String formatSpecifier = "%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
    private static final Pattern fsPattern = Pattern.compile( formatSpecifier );


    /**
     * @param locale The locale according to which to format the date.
     *
     * @return A formatter according to the given locale in short form.
     */
    public static DateFormat getDateFormat(Locale locale) {

        return DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, locale );
    }

    /**
     * @param locale The locale according to which to format the date.
     * @param date   The date to format.
     *
     * @return A string that is the formatted representation of the given date according to the given locale in short
     *         form.
     */
    public static String format(Locale locale, Date date) {

        return getDateFormat( locale ).format( date );
    }

    /**
     * @param locale The locale according to which to format the currency.
     *
     * @return A formatter according to the given locale's currency.
     */
    public static NumberFormat getCurrencyFormat(Locale locale) {

        return NumberFormat.getCurrencyInstance( locale );
    }

    /**
     * @param locale The locale according to which to format the currency.
     * @param number The currency number that needs to be formatted.
     *
     * @return A string that is the formatted representation of the given amount of currency according to the given
     *         locale.
     */
    public static String format(Locale locale, Number number) {

        return getCurrencyFormat( locale ).format( number );
    }

    /**
     * @return The {@link HttpServletRequest} contained in the active Wicket {@link Request}.
     */
    public static HttpServletRequest getServletRequest() {

        return ((WebRequest) RequestCycle.get().getRequest()).getHttpServletRequest();
    }

    /**
     * @return The {@link HttpServletResponse} contained in the active Wicket {@link Response}.
     */
    public static HttpServletResponse getServletResponse() {

        Response response = RequestCycle.get().getResponse();
        if (response instanceof WebResponse)
            return ((WebResponse) RequestCycle.get().getResponse()).getHttpServletResponse();
        return null;
    }

    /**
     * @return The {@link HttpSession} contained in the active Wicket {@link Request}.
     */
    public static HttpSession getHttpSession() {

        return getServletRequest().getSession();
    }

    /**
     * @return <code>true</code> if the user is authenticated by the linkID SDK framework.
     */
    public static boolean isLinkIDAuthenticated() {

        return LoginManager.isAuthenticated( getServletRequest() );
    }

    /**
     * @return The linkID userId that the current user has authenticated himself with or <code>null</code> if the user
     *         isn't authenticated yet (through linkID).
     */
    public static String findLinkID() {

        return LoginManager.findUserId( getServletRequest() );
    }

    /**
     * Uses the application's localizer and the active session's locale.
     *
     * Note: You can use this method with a single argument, too. This will cause the first argument (format) to be
     * evaluated as a localization key.
     *
     * @param component The component in whose context to resolve localization keys.
     * @param format    The format specification for the arguments. See
     *                  {@link String#format(Locale, String, Object...)}. To that list, add the 'l' conversion
     *                  parameter. This parameter first looks the arg data up as a localization key, then processes the result
     *                  as though it was given with the 's' conversion parameter.
     * @param args      The arguments that contain the data to fill into the format specifications.
     *
     * @return The localized string.
     */
    public static String localize(Component component, String format, Object... args) {

        return localize( Application.get().getResourceSettings().getLocalizer(), component, Session.get().getLocale(),
                         format, args );
    }

    /**
     * Note: You can use this method with a single argument, too. This will cause the first argument (format) to be
     * evaluated as a localization key.
     *
     * @param localizer The localization provider.
     * @param component The component in whose context to resolve localization keys.
     * @param locale    The locale for which to resolve localization keys.
     * @param format    The format specification for the arguments. See
     *                  {@link String#format(Locale, String, Object...)}. To that list, add the 'l' conversion
     *                  parameter. This parameter first looks the arg data up as a localization key, then processes the result
     *                  as though it was given with the 's' conversion parameter.
     * @param args      The arguments that contain the data to fill into the format specifications.
     *
     * @return The localized string.
     */
    public static String localize(Localizer localizer, Component component, Locale locale, String format,
                                  Object... args) {

        if (args.length == 0)
            // Single argument invocation: format is localization key.
            return localizer.getString( format, component );

        List<Object> localizationData = new ArrayList<Object>( args.length );
        StringBuilder newFormat = new StringBuilder( format );
        Matcher specifiers = fsPattern.matcher( format );

        int pos = 0, num = 0;
        while (specifiers.find( pos )) {
            if ("l".equalsIgnoreCase( specifiers.group( 6 ) )) {
                if ("L".equals( specifiers.group( 6 ) ))
                    newFormat.setCharAt( specifiers.end( 6 ) - 1, 'S' );
                else
                    newFormat.setCharAt( specifiers.end( 6 ) - 1, 's' );

                if (args[num] == null)
                    throw new NullPointerException(
                            String.format( "Key for localization must be String, got %s (arg: %d)", "null", num ) );
                if (!(args[num] instanceof String))
                    throw new IllegalArgumentException(
                            String.format( "Key for localization must be String, got %s (arg: %d)",
                                           args[num].getClass(), num ) );

                localizationData.add( localizer.getString( (String) args[num], component ) );
            } else
                localizationData.add( args[num] );

            ++num;
            pos = specifiers.end();
        }

        return String.format( locale, newFormat.toString(), localizationData.toArray() );
    }
}
