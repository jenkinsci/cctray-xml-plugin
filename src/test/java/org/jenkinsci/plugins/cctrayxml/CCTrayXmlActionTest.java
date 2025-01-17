package org.jenkinsci.plugins.cctrayxml;

import hudson.model.BallColor;
import hudson.model.Item;
import hudson.model.Job;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CCTrayXmlActionTest {
    @Mock private Item i;
    @Mock private Job j;

    @Test
    void testToCCStatusForItem() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertEquals("Unknown", cCTrayXmlAction.toCCStatus(i));
    }

    @ParameterizedTest
    @EnumSource(names = {
            "BLUE", "BLUE_ANIME"
    })
    void testToCCStatusForSuccess(BallColor color) {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        when(j.getIconColor()).thenReturn(color);
        assertEquals("Success", cCTrayXmlAction.toCCStatus(j));
    }

    @ParameterizedTest
    @EnumSource(names = {
            "ABORTED", "ABORTED_ANIME", "RED", "RED_ANIME", "YELLOW", "YELLOW_ANIME"
    })
    void testToCCStatusForFailure(BallColor color) {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        when(j.getIconColor()).thenReturn(color);
        assertEquals("Failure", cCTrayXmlAction.toCCStatus(j));
    }

    @ParameterizedTest
    @EnumSource(names = {
            "DISABLED", "DISABLED_ANIME", "GREY", "GREY_ANIME", "NOTBUILT", "NOTBUILT_ANIME"
    })
    void testToCCStatusForUnknown(BallColor color) {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        when(j.getIconColor()).thenReturn(color);
        assertEquals("Unknown", cCTrayXmlAction.toCCStatus(j));
    }
}