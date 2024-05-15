package com.todolist.todo.dtos;

import jakarta.annotation.Nullable;
import com.todolist.todo.models.ToDoModel.Priority;

public record UpdateToDoRequestDto(
                @Nullable String name,
                @Nullable String description,
                @Nullable boolean accomplished,
                @Nullable Priority priority) {
}
