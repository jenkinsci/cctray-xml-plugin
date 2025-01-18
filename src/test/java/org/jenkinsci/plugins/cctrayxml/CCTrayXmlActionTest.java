package org.jenkinsci.plugins.cctrayxml;

import hudson.model.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CCTrayXmlActionTest {
    @Mock private Item i;
    @Mock private Job j;
    @Mock private View view;
    @Mock private StaplerRequest2 request;
    @Mock private ViewGroup owner;
    @Mock private ItemGroup itemGroup;

    @Nested
    class ToCcStatus {
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

    @Test
    void testGetUrlName() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertEquals("cc.xml", cCTrayXmlAction.getUrlName());
    }

    @Test
    void testGetViewNull() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertNull(cCTrayXmlAction.getView());
    }

    @Test
    void testGetView() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(view);
        assertEquals(view, cCTrayXmlAction.getView());
    }

    @Test
    void testGetDisplayName() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertNull(cCTrayXmlAction.getDisplayName());
    }

    @Test
    void testGetIconFileName() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertNull(cCTrayXmlAction.getIconFileName());
    }

    @Nested
    class GetCCItems {
        @Test
        void testGetCCItemsNonRecursive() {
            try (MockedStatic<Stapler> utilities = Mockito.mockStatic(Stapler.class)) {
                CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(view);
                utilities.when(Stapler::getCurrentRequest2).thenReturn(request);
                when(request.getParameter("recursive")).thenReturn(null);
                cCTrayXmlAction.getCCItems();
                verify(view).getItems();
            }
        }

        @Test
        void testGetCCItemsRecursive() {
            try (MockedStatic<Stapler> utilities = Mockito.mockStatic(Stapler.class)) {
                CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(view);
                utilities.when(Stapler::getCurrentRequest2).thenReturn(request);
                when(request.getParameter("recursive")).thenReturn("true");
                when(view.getOwner()).thenReturn(owner);
                when(owner.getItemGroup()).thenReturn(itemGroup);
                cCTrayXmlAction.getCCItems();
                verify(itemGroup).getAllItems(TopLevelItem.class);
            }
        }
    }


}