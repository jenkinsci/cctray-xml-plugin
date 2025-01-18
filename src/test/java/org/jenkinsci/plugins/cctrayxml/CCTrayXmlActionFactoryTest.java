package org.jenkinsci.plugins.cctrayxml;

import hudson.model.Action;
import hudson.model.View;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CCTrayXmlActionFactoryTest {
    private final CCTrayXmlActionFactory factory = new CCTrayXmlActionFactory();

    public static Stream<Arguments> views() {
        View view = Mockito.mock(View.class);
        return Stream.of(
            Arguments.of((View) null),
            Arguments.of(view)
        );
    }

    @ParameterizedTest
    @MethodSource("views")
    void testCreateForNull(View v) {
        List<Action> actions = factory.createFor(v);
        assertThat(actions)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);
        Action theAction = actions.get(0);
        assertThat(theAction)
            .isNotNull()
            .isInstanceOf(CCTrayXmlAction.class);
        assertThat(((CCTrayXmlAction) theAction).getView())
            .isEqualTo(v);
    }

}