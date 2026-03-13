package org.jenkinsci.plugins.cctrayxml;

import hudson.Extension;
import hudson.model.PageDecorator;
import jenkins.YesNoMaybe;

@Extension(dynamicLoadable = YesNoMaybe.YES)
public class CCTrayXmlPageDecorator extends PageDecorator {
    // footer.jelly
}
