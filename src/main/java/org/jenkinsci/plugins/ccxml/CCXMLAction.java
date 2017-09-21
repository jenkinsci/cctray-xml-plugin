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

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.model.View;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.Stapler;

@Restricted(NoExternalUse.class)
public class CCXMLAction implements Action {

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
        return "cc.xml2";
    }

    public View getView() {
        return this.view;
    }

    public Map<String, Collection<TopLevelItem>> getItems() {
        String recursive = Stapler.getCurrentRequest().getParameter("recursive");
        if (StringUtils.isNotEmpty(recursive)) {
            return Collections.unmodifiableMap(getItemsRecursive("", view.getItems()));
        } else {
            return Collections.singletonMap("", view.getItems());
        }
    }

    public Map<String, Collection<TopLevelItem>> getItemsRecursive(String namePrefix, Collection<TopLevelItem> items) {
        Map<String, Collection<TopLevelItem>> result = new HashMap<>();
        List<TopLevelItem> currentLevelItems = new ArrayList<>();
        for (TopLevelItem i : items) {
            if (i instanceof Folder) {
                Folder f = (Folder) i;
                result.putAll(getItemsRecursive(namePrefix + f.getDisplayName() + "/", f.getItems()));
            } else {
                currentLevelItems.add(i);
            }
        }
        result.put(namePrefix, currentLevelItems);
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
