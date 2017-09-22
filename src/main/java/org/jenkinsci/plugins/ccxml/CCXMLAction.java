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
package org.jenkinsci.plugins.ccxml;

import hudson.model.Action;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.model.View;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.Stapler;

@Restricted(NoExternalUse.class)
public class CCXMLAction implements Action {

    public static final String URL_NAME = "cc.xml2";

    private transient View view;

    CCXMLAction(View view) {
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
        return URL_NAME;
    }

    public View getView() {
        return this.view;
    }

    /**
     * @return A map containing the items in the view object. If the request
     * contains a query parameter named "recursive", then folders in the view
     * are traversed recursively and all items in those folders are returned
     * as well. The map is keyed by the folder the item is in, with top-level
     * items having an empty key.
     */
    @Restricted(NoExternalUse.class)
    public Collection<TopLevelItem> getItems() {
        String recursive = Stapler.getCurrentRequest().getParameter("recursive");
        if (recursive == null) {
            return view.getItems();
        } else {
            return Collections.unmodifiableCollection(getItemsRecursive(view.getItems()));
        }
    }

    private Collection<TopLevelItem> getItemsRecursive(Collection<TopLevelItem> items) {
        List<TopLevelItem> result = new ArrayList<>();
        for (TopLevelItem i : items) {
            if (i instanceof ItemGroup) {
                ItemGroup g = (ItemGroup) i;
                result.addAll(getItemsRecursive(g.getItems()));
            } else {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * Converts the Hudson build status to CruiseControl build status,
     * which is either Success, Failure, Exception, or Unknown.
     */
    public String toCCStatus(Item i) {
        if (i instanceof Job) {
            Job j = (Job) i;
            switch (j.getIconColor()) {
                case ABORTED:
                case ABORTED_ANIME:
                case RED:
                case RED_ANIME:
                case YELLOW:
                case YELLOW_ANIME:
                    return "Failure";
                case BLUE:
                case BLUE_ANIME:
                    return "Success";
                case DISABLED:
                case DISABLED_ANIME:
                case GREY:
                case GREY_ANIME:
                case NOTBUILT:
                case NOTBUILT_ANIME:
                    return "Unknown";
            }
        }
        return "Unknown";
    }
}
