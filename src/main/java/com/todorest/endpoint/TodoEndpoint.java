package com.todorest.endpoint;

import com.todorest.dto.CreateTodoRequestDto;
import com.todorest.dto.TodoDto;
import com.todorest.entity.Category;
import com.todorest.entity.Todo;
import com.todorest.entity.Type.StatusType;
import com.todorest.entity.User;
import com.todorest.mapper.CategoryMapper;
import com.todorest.mapper.TodoMapper;
import com.todorest.repository.CategoryRepository;
import com.todorest.repository.TodoRepository;
import com.todorest.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * /todo {POST} - ստեղծել կատեգորիա, յուզերին ստեղ չենք տա, այլ @AuthenticationPrinciple- ով կվերցնենք - status էլ չենք տա, սկզբից կդնենք NOT_STARTED
 * /todo {GET} - վերադարձնում ենք էն յուզերի բոլոր թուդուները որը լոգին է էղել(թոկենով, էլի @AuthenticationPrinciple)
 * /todo/byStatus {GET} - requestParam-ով ստատուս կստանա ու լոգին էղած յուզերի մենակ էդ ստատուսով թուդուները կբերե
 * /todo/byCategory {GET} - requestParam-ով category կստանա ու լոգին էղած յուզերի մենակ էդ category-ներով թուդուները կբերե
 * /todo/{id} {PUT} - փոխում ենք թուդույի ստատուսը
 * /todo/{id} {DELETE} - ջնջում ենք թուդուն, եթե էդ այդի ով թուդուն լոգին էղած մարդն է սարքել։
 */
@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoEndpoint {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final TodoMapper todoMapper;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<TodoDto> createToDo(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody CreateTodoRequestDto createTodoRequestDto
    ) {
        int categoryId = createTodoRequestDto.getCategoryId();
        Optional<Category> byId = categoryRepository.findById(categoryId);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Todo saved = new Todo();
        saved.setUser(currentUser.getUser());
        saved.setCategory(byId.get());
        saved.setStatus(StatusType.NOT_STARTED);
        saved.setTitle(createTodoRequestDto.getTitle());

        todoRepository.save(saved);
        return ResponseEntity.ok(todoMapper.mapToDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<TodoDto>> findAllByUser(@AuthenticationPrincipal CurrentUser currentUser) {
        User getCurrent = currentUser.getUser();
        List<Todo> allByUserId = todoRepository.findAllByUser_Id(getCurrent.getId());

        if (allByUserId.isEmpty()) {
            return null;
        }

        List<TodoDto> todoDtos = new ArrayList<>();
        for (Todo todo : allByUserId) {
            todoDtos.add(todoMapper.mapToDto(todo));
        }

        return ResponseEntity.ok(todoDtos);
    }

    @GetMapping("/byStatus")
    public ResponseEntity<List<TodoDto>> findAllByStatus(@AuthenticationPrincipal CurrentUser currentUser,
                                                         @RequestParam("status") StatusType status) {
        ResponseEntity<List<TodoDto>> allByUser = findAllByUser(currentUser);

        if (allByUser.getBody() == null) {
            return ResponseEntity.notFound().build();
        }

        List<TodoDto> responseEntityBody = allByUser.getBody();

        List<TodoDto> finalFilteredList = new ArrayList<>();
        for (TodoDto todoDto : responseEntityBody) {
            if (todoDto.getStatusType().equals(status)) {
                finalFilteredList.add(todoDto);
            }
        }

        return ResponseEntity.ok(finalFilteredList);
    }

    @GetMapping("/byCategory")
    public ResponseEntity<List<TodoDto>> findAllByCategory(@AuthenticationPrincipal CurrentUser currentUser,
                                                           @RequestParam("category") Category category) {
        ResponseEntity<List<TodoDto>> allByUser = findAllByUser(currentUser);

        if (allByUser.getBody() == null) {
            return ResponseEntity.notFound().build();
        }

        List<TodoDto> responseEntityBody = allByUser.getBody();

        List<TodoDto> finalFilteredList = new ArrayList<>();
        for (TodoDto todoDto : responseEntityBody) {
            if (todoDto.getCategory().equals(categoryMapper.mapToDto(category))) {
                finalFilteredList.add(todoDto);
            }
        }

        return ResponseEntity.ok(finalFilteredList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> changeToDoStatus(@AuthenticationPrincipal CurrentUser currentUser,
                                                    @PathVariable("id") int id,
                                                    @RequestParam("status") StatusType status
    ) {
        User current = currentUser.getUser();
        Optional<Todo> byId = todoRepository.findById(id);

        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (byId.get().getUser().getId() != current.getId()) {
            return ResponseEntity.notFound().build();
        }

        Todo todo = byId.get();
        todo.setStatus(status);

        todoRepository.save(todo);
        return ResponseEntity.ok(todoMapper.mapToDto(todo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteToDoById(@AuthenticationPrincipal CurrentUser currentUser,
                                            @PathVariable("id") int id
    ) {
        User current = currentUser.getUser();
        Optional<Todo> byId = todoRepository.findById(id);

        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (byId.get().getUser().getId() != current.getId()) {
            return ResponseEntity.notFound().build();
        }

        todoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
