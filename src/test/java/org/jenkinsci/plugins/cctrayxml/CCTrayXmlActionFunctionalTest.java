package org.jenkinsci.plugins.cctrayxml;

import hudson.model.FreeStyleProject;
import hudson.model.Result;
import org.htmlunit.Page;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.xml.XmlPage;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.w3c.dom.Node;

import static org.assertj.core.api.Assertions.assertThat;

@WithJenkins
class CCTrayXmlActionFunctionalTest {

    @Test
    void testCCXmlLinkInView(JenkinsRule j) throws Exception {
        j.createFreeStyleProject("dummy-project");

        try (JenkinsRule.WebClient client = j.createWebClient()) {
            HtmlPage page = client.goTo("view/all/");
            client.waitForBackgroundJavaScript(2000);

            HtmlAnchor link = page.getAnchorByText("cc.xml");
            assertThat(link)
                    .as("Link to cc.xml should exist in the view")
                    .isNotNull();
        }
    }

    @Test
    void testCCXmlContentSuccess(JenkinsRule j) throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("test-project-success");
        j.buildAndAssertSuccess(p);

        try (JenkinsRule.WebClient client = j.createWebClient()) {
            Page page = client.goTo("view/all/cc.xml", "text/xml");

            assertThat(page)
                    .as("Page should be an XmlPage")
                    .isInstanceOf(XmlPage.class);
            XmlPage xmlPage = (XmlPage) page;

            Node projectNode = (Node) xmlPage.getFirstByXPath("//Project[@name='test-project-success']");
            assertThat(projectNode)
                    .as("Project node should exist")
                    .isNotNull();

            String status = projectNode.getAttributes().getNamedItem("lastBuildStatus").getNodeValue();
            assertThat(status).isEqualTo("Success");
        }
    }

    @Test
    void testCCXmlContentFailure(JenkinsRule j) throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("test-project-failure");
        // Create a failure
        p.getBuildersList().add(new org.jvnet.hudson.test.FailureBuilder());
        j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0));

        try (JenkinsRule.WebClient client = j.createWebClient()) {
            Page page = client.goTo("view/all/cc.xml", "text/xml");

            assertThat(page)
                    .as("Page should be an XmlPage")
                    .isInstanceOf(XmlPage.class);
            XmlPage xmlPage = (XmlPage) page;

            Node projectNode = (Node) xmlPage.getFirstByXPath("//Project[@name='test-project-failure']");
            assertThat(projectNode)
                    .as("Project node should exist")
                    .isNotNull();

            String status = projectNode.getAttributes().getNamedItem("lastBuildStatus").getNodeValue();
            assertThat(status).isEqualTo("Failure");
        }
    }
}