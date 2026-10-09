package com.university.modules.assistant;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntUnaryOperator;

/** In-memory room source used only for automatic schedule generation. */
public final class MockRoomProvider {

    public static final List<String> DEFAULT_ROOMS = List.of(
            "A1-101", "A1-201", "A2-301", "A2-401", "B1-202", "B2-302");

    private final List<String> rooms;
    private final IntUnaryOperator indexSelector;

    public MockRoomProvider() {
        this(DEFAULT_ROOMS, bound -> ThreadLocalRandom.current().nextInt(bound));
    }

    public MockRoomProvider(List<String> rooms, IntUnaryOperator indexSelector) {
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Danh sách phòng không được rỗng");
        }
        if (rooms.stream().anyMatch(room -> room == null || room.isBlank())) {
            throw new IllegalArgumentException("Mã phòng không được rỗng");
        }
        this.rooms = List.copyOf(rooms);
        this.indexSelector = Objects.requireNonNull(indexSelector, "indexSelector");
    }

    public String chooseRoom() {
        int index = indexSelector.applyAsInt(rooms.size());
        if (index < 0 || index >= rooms.size()) {
            throw new IllegalStateException("Nguồn random trả index ngoài danh sách phòng");
        }
        return rooms.get(index);
    }

    public List<String> getRooms() {
        return rooms;
    }
}
