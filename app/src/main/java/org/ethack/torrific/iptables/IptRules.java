package org.ethack.torrific.iptables;

import android.content.Context;
import android.util.Log;

import org.ethack.torrific.lib.Shell;

/**
 * Wrapper for IPTables calls.
 */
public class IptRules {

    private final static String IPTABLES = "/system/bin/iptables";
    private String RULE;

    /**
     * Apply an IPTables rule
     * @param rule
     * @return true if success
     */
    private boolean applyRule(final String rule) {
        Shell shell = new Shell();
        if (shell.suExec(rule)) {
            return true;
        } else {
            Log.e(IptRules.class.getName(), "FAILED to apply");
            return false;
        }
    }

    /**
     * Build an iptables call in order to either create or remove NAT rule
     * @param appUID
     * @param action
     * @param appName
     * @return true if success
     */
    public boolean natApp(final long appUID, final char action, final String appName) {
        RULE = "%s -t nat -%c OUTPUT ! -o lo -p tcp -m tcp --tcp-flags FIN,SYN,RST,ACK SYN -m owner --uid-owner %d -j REDIRECT --to-ports 9040 -m comment --comment \"Force %s through TransPort\"";

        return applyRule(String.format(RULE, IPTABLES, action, appUID, appName));
    }

    public boolean LanNoNat(final String lan) {
        RULE = "%s -t nat -A OUTPUT -d %s -j RETURN";
        if (applyRule(String.format(RULE, IPTABLES, lan))) {
            RULE = "%s -A OUTPUT -d %s -j LAN";
            return applyRule(String.format(RULE, IPTABLES, lan));
        }
        return false;
    }

    public boolean genericRule(final String rule) {
        return applyRule(String.format("%s %s", IPTABLES, rule));
    }
}
