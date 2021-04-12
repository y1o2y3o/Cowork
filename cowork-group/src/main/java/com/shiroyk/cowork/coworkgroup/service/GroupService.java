package com.shiroyk.cowork.coworkgroup.service;

import com.shiroyk.cowork.coworkgroup.model.Group;
import com.shiroyk.cowork.coworkgroup.repository.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public Optional<Group> findGroupByNameEquals(String name) {
        return groupRepository.findGroupByNameEquals(name);
    }

    public Optional<Group> findById(String id) {
        return groupRepository.findById(id);
    }

    public List<Group> findGroupsByNameContains(String name) {
        return groupRepository.findGroupsByNameContains(name);
    }

    public Page<Group> findAll(Pageable pageable) {
        return groupRepository.findAll(pageable);
    }

    public Long count() {
        return groupRepository.count();
    }

    public Group save(Group group) {
        group.setUpdateTime();
        return groupRepository.save(group);
    }
}