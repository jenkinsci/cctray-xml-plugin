/*
 * The MIT License
 *
 * Copyright 2017 CloudBees, Inc.
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

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.MockFolder;

public class CCXMLActionTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testGetItemsNonRecursive() throws Exception {
        FreeStyleProject p1 = j.createFreeStyleProject("project1");
        j.buildAndAssertSuccess(p1);

        XmlPage xml = getPrimaryViewCCXMLPage();
        assertXPathNodeCount(xml, getXPathForItem(p1), 1);

        MockFolder f1 = j.createFolder("folder1");
        FreeStyleProject p2 = f1.createProject(FreeStyleProject.class, "project2");
        j.buildAndAssertSuccess(p2);
        xml = getPrimaryViewCCXMLPage();
        assertXPathNodeCount(xml, getXPathForItem(p1), 1);
        assertXPathNodeCount(xml, getXPathForItem(p2), 0);
    }

    @Test
    public void testGetItemsRecursive() throws Exception {
        FreeStyleProject p1 = j.createFreeStyleProject("project1");
        j.buildAndAssertSuccess(p1);

        XmlPage xml = getPrimaryViewCCXMLPage("recursive");
        assertXPathNodeCount(xml, getXPathForItem(p1), 1);

        MockFolder f1 = j.createFolder("folder1");
        FreeStyleProject p2 = f1.createProject(FreeStyleProject.class, "project2");
        j.buildAndAssertSuccess(p2);
        xml = getPrimaryViewCCXMLPage("recursive");
        assertXPathNodeCount(xml, getXPathForItem(p1), 1);
        assertXPathNodeCount(xml, getXPathForItem(p2), 1);

        MockFolder f2 = f1.createProject(MockFolder.class, "folder2");
        FreeStyleProject p3 = f2.createProject(FreeStyleProject.class, "project3");
        j.buildAndAssertSuccess(p3);
        xml = getPrimaryViewCCXMLPage("recursive");
        assertXPathNodeCount(xml, getXPathForItem(p1), 1);
        assertXPathNodeCount(xml, getXPathForItem(p2), 1);
        assertXPathNodeCount(xml, getXPathForItem(p3), 1);
    }

    private XmlPage getPrimaryViewCCXMLPage(String... queryParameters) throws Exception {
        return goToXml(j.createWebClient(), "view/all/" + CCXMLAction.URL_NAME + "/?" + String.join("&", queryParameters));
    }

    private void assertXPathNodeCount(XmlPage xml, String xPath, int expectedNodes) {
        List<?> nodes = xml.getByXPath(xPath);
        assertEquals("incorrect number of nodes in xpath", expectedNodes, nodes.size());
    }

    private String getXPathForItem(Item item) {
        return "/Projects/Project[@name='" + item.getFullDisplayName() + "']";
    }

    private XmlPage goToXml(WebClient wc, String path) throws Exception {
        Page page = wc.goTo(path, "text/xml");
        assertTrue(page instanceof XmlPage);
        return (XmlPage) page;
    }

}
