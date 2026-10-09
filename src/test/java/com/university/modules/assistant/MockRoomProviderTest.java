package com.university.modules.assistant;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockRoomProviderTest {

    @Test
    void choosesRoomFromImmutableHardCodedSourceWithTestableSelector() {
        List<String> source = new ArrayList<>(List.of("A1-101", "B2-302"));
        MockRoomProvider provider = new MockRoomProvider(source, bound -> bound - 1);
        source.set(1, "CHANGED");

        assertEquals("B2-302", provider.chooseRoom());
        assertThrows(UnsupportedOperationException.class,
                () -> provider.getRooms().add("C1-101"));
    }

    @Test
    void rejectsEmptyRoomSourceAndInvalidSelectorResult() {
        assertThrows(IllegalArgumentException.class,
                () -> new MockRoomProvider(List.of(), bound -> 0));
        MockRoomProvider provider = new MockRoomProvider(List.of("A1-101"), bound -> bound);
        assertThrows(IllegalStateException.class, provider::chooseRoom);
    }
}
