package net.sf.ehcache.constructs.web.filter;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * RFC 2616 - HTTP/1.1 Protocol
 * <p/>
 * Section 3.3.1 defines the preferred full date and time as:
 * <pre>
 * HTTP-date    = rfc1123-date
 * rfc1123-date = wkday "," SP date1 SP time SP "GMT"
 * date1        = 2DIGIT SP month SP 4DIGIT
 * ; day month year (e.g., 02 Jun 1982)
 * time         = 2DIGIT ":" 2DIGIT ":" 2DIGIT
 * ; 00:00:00 - 23:59:59
 * wkday        = "Mon" | "Tue" | "Wed"
 * | "Thu" | "Fri" | "Sat" | "Sun"
 * month        = "Jan" | "Feb" | "Mar" | "Apr"
 * | "May" | "Jun" | "Jul" | "Aug"
 * | "Sep" | "Oct" | "Nov" | "Dec"
 * </pre>
 * <p/>
 * An example is <code>Sun, 06 Nov 1994 08:49:37 GMT</code>
 * <p/>
 * These are used in request and response headers.
 * <p/>
 */
public class HttpDateFormatter {

    private static final Logger LOG = Logger.getLogger(HttpDateFormatter.class.getName());

    private final SimpleDateFormat httpDateFormat;



    /**
     * Constructs a new formatter.
     * <p/>
     * Note that this class is not thread-safe for use by multiple threads, as SimpleDateFormat is not.
     * Each thread should create their own instance of this class.
     */
    public HttpDateFormatter() {
        httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    /**
     *
     * @param date
     * @return A date formatted in accordance with Section 3.3.1 of RFC 2616
     */
    public synchronized String formatHttpDate(Date date) {
        return httpDateFormat.format(date);
    }

    /**
     * Parses dates supplied in accordance with Section 3.3.1 of RFC 2616
     * @param date a date formatted in accordance with Section 3.3.1 of RFC 2616
     * @return the parsed Date. If the date cannot be parsed, 1/1/1970 (0 for Unix systems) is returned which will
     * have the effect of expiring the content.
     */
    public synchronized Date parseDateFromHttpDate(String date) {
        try {
            return httpDateFormat.parse(date);
        } catch (ParseException e) {
            LOG.log(Level.FINE, "ParseException on date {0}. 1/1/1970 will be returned", date);
            return new Date(0);
        }
    }
}