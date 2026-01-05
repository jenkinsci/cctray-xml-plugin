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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
            assertThat(cCTrayXmlAction.toCCStatus(i)).isEqualTo("Unknown");
        }

        @ParameterizedTest
        @EnumSource(names = {
                "BLUE", "BLUE_ANIME"
        })
        void testToCCStatusForSuccess(BallColor color) {
            CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
            when(j.getIconColor()).thenReturn(color);
            assertThat(cCTrayXmlAction.toCCStatus(j)).isEqualTo("Success");
        }

        @ParameterizedTest
        @EnumSource(names = {
                "ABORTED", "ABORTED_ANIME", "RED", "RED_ANIME", "YELLOW", "YELLOW_ANIME"
        })
        void testToCCStatusForFailure(BallColor color) {
            CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
            when(j.getIconColor()).thenReturn(color);
            assertThat(cCTrayXmlAction.toCCStatus(j)).isEqualTo("Failure");
        }

        @ParameterizedTest
        @EnumSource(names = {
                "DISABLED", "DISABLED_ANIME", "GREY", "GREY_ANIME", "NOTBUILT", "NOTBUILT_ANIME"
        })
        void testToCCStatusForUnknown(BallColor color) {
            CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
            when(j.getIconColor()).thenReturn(color);
            assertThat(cCTrayXmlAction.toCCStatus(j)).isEqualTo("Unknown");
        }
    }

    @Test
    void testGetUrlName() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertThat(cCTrayXmlAction.getUrlName()).isEqualTo("cc.xml");
    }

    @Test
    void testGetViewNull() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertThat(cCTrayXmlAction.getView()).isNull();
    }

    @Test
    void testGetView() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(view);
        assertThat(cCTrayXmlAction.getView()).isEqualTo(view);
    }

    @Test
    void testGetDisplayName() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertThat(cCTrayXmlAction.getDisplayName()).isNull();
    }

    @Test
    void testGetIconFileName() {
        CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(null);
        assertThat(cCTrayXmlAction.getIconFileName()).isNull();
    }

    @Nested
    class GetCCItems {
        @Test
        void testGetCCItemsNonRecursiveEmpty() {
            try (MockedStatic<Stapler> utilities = Mockito.mockStatic(Stapler.class)) {
                CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(view);
                utilities.when(Stapler::getCurrentRequest2).thenReturn(request);
                when(request.getParameter("recursive")).thenReturn(null);

                when(view.getItems()).thenReturn(Collections.emptyList());
                Collection<TopLevelItem> result = cCTrayXmlAction.getCCItems();
                assertThat(result).isEmpty();
                verify(view).getItems();
            }
        }

        @Test
        void testGetCCItemsNonRecursiveNonEmpty() {
            try (MockedStatic<Stapler> utilities = Mockito.mockStatic(Stapler.class)) {
                CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(view);
                utilities.when(Stapler::getCurrentRequest2).thenReturn(request);
                when(request.getParameter("recursive")).thenReturn(null);

                List<TopLevelItem> items = List.of(mock(TopLevelItem.class));
                when(view.getItems()).thenReturn(items);
                Collection<TopLevelItem> result = cCTrayXmlAction.getCCItems();
                assertThat(result).hasSize(1);
                assertThat(result).isEqualTo(items);
            }
        }

        @Test
        void testGetCCItemsRecursiveEmpty() {
            try (MockedStatic<Stapler> utilities = Mockito.mockStatic(Stapler.class)) {
                CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(view);
                utilities.when(Stapler::getCurrentRequest2).thenReturn(request);
                when(request.getParameter("recursive")).thenReturn("true");
                when(view.getOwner()).thenReturn(owner);
                when(owner.getItemGroup()).thenReturn(itemGroup);

                when(itemGroup.getAllItems(TopLevelItem.class)).thenReturn(Collections.emptyList());
                Collection<TopLevelItem> result = cCTrayXmlAction.getCCItems();
                assertThat(result).isEmpty();
                verify(itemGroup).getAllItems(TopLevelItem.class);
            }
        }

        @Test
        void testGetCCItemsRecursiveNonEmpty() {
            try (MockedStatic<Stapler> utilities = Mockito.mockStatic(Stapler.class)) {
                CCTrayXmlAction cCTrayXmlAction = new CCTrayXmlAction(view);
                utilities.when(Stapler::getCurrentRequest2).thenReturn(request);
                when(request.getParameter("recursive")).thenReturn("true");
                when(view.getOwner()).thenReturn(owner);
                when(owner.getItemGroup()).thenReturn(itemGroup);

                List<TopLevelItem> items = List.of(mock(TopLevelItem.class));
                when(itemGroup.getAllItems(TopLevelItem.class)).thenReturn(items);
                Collection<TopLevelItem> result = cCTrayXmlAction.getCCItems();
                assertThat(result).hasSize(1);
                assertThat(result).isEqualTo(items);
            }
        }
    }
}
