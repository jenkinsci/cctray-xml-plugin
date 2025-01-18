/*
 * The MIT License
 *
 * Copyright (c) 2016 Daniel Beck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.cctrayxml;

import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.model.View;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.Stapler;

import java.util.Collection;

@Restricted(NoExternalUse.class)
public class CCTrayXmlAction implements Action {

    private final transient View view;

    CCTrayXmlAction(View view) {
        this.view = view;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return "cc.xml";
    }

    public View getView() {
        return this.view;
    }

    /**
     * Converts the Hudson build status to CruiseControl build status,
     * which is either Success, Failure, Exception, or Unknown.
     */
    public String toCCStatus(Item i) {
        if (i instanceof Job j) {
            return switch (j.getIconColor()) {
                case ABORTED, ABORTED_ANIME, RED, RED_ANIME, YELLOW, YELLOW_ANIME -> "Failure";
                case BLUE, BLUE_ANIME -> "Success";
                case DISABLED, DISABLED_ANIME, GREY, GREY_ANIME, NOTBUILT, NOTBUILT_ANIME -> "Unknown";
            };
        }
        return "Unknown";
    }

    @Restricted(NoExternalUse.class) // Jelly
    public Collection<TopLevelItem> getCCItems() {
        if (Stapler.getCurrentRequest2().getParameter("recursive") != null) {
            return view.getOwner().getItemGroup().getAllItems(TopLevelItem.class);
        } else {
            return view.getItems();
        }
    }
}
