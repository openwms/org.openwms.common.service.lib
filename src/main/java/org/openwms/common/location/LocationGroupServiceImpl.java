/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.location;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.openwms.common.CommonMessageCodes;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.core.util.TreeNode;
import org.openwms.core.util.TreeNodeImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A LocationGroupServiceImpl is a Spring managed transactional Service that operates on
 * {@link LocationGroup} entities and spans the tx boundary.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@TxService
class LocationGroupServiceImpl implements LocationGroupService {

    private final LocationGroupRepository locationGroupRepository;
    private final ApplicationContext ctx;
    private final Translator translator;

    LocationGroupServiceImpl(LocationGroupRepository locationGroupRepository, ApplicationContext ctx, Translator translator) {
        this.locationGroupRepository = locationGroupRepository;
        this.ctx = ctx;
        this.translator = translator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeGroupState(String id, LocationGroupState stateIn, LocationGroupState stateOut) {
        LocationGroup locationGroup = locationGroupRepository.findById(Long.valueOf(id)).orElseThrow(NotFoundException::new);
        locationGroup.changeState(stateIn, stateOut);
        ctx.publishEvent(LocationGroupEvent.of(locationGroup, LocationGroupEvent.LocationGroupEventType.STATE_CHANGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void changeGroupStates(String locationGroupName, LocationGroupState stateIn, LocationGroupState stateOut) {
        LocationGroup locationGroup = locationGroupRepository.findByName(locationGroupName).orElseThrow(() -> new NotFoundException(translator, CommonMessageCodes.LOCATION_GROUP_NOT_FOUND, new String[]{locationGroupName}, locationGroupName));
        locationGroup.changeGroupStateIn(stateIn);
        locationGroup.changeGroupStateOut(stateOut);
        ctx.publishEvent(LocationGroupEvent.of(locationGroup, LocationGroupEvent.LocationGroupEventType.STATE_CHANGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<LocationGroup> findByName(String name) {
        return locationGroupRepository.findByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public List<LocationGroup> findAll() {
        return locationGroupRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public List<LocationGroup> findByNames(List<String> names) {
        List<LocationGroup> result = locationGroupRepository.findByNameIn(names);
        return result == null ? Collections.emptyList() : result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    //@Override
    public TreeNode<LocationGroup> getLocationGroupsAsTree() {
        return createTree(new TreeNodeImpl<>(), getLocationGroupsAsList());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    //@Override
    public List<LocationGroup> getLocationGroupsAsList() {
        return locationGroupRepository.findAll();
    }

    private TreeNode<LocationGroup> createTree(TreeNode<LocationGroup> root, List<LocationGroup> locationGroups) {
        for (LocationGroup l : locationGroups) {
            searchForNode(l, root);
        }
        return root;
    }

    private TreeNode<LocationGroup> searchForNode(LocationGroup lg, TreeNode<LocationGroup> root) {
        TreeNode<LocationGroup> node;
        if (lg.getParent() == null) {
            node = root.getChild(lg);
            if (node == null) {
                TreeNode<LocationGroup> n1 = new TreeNodeImpl<>();
                n1.setData(lg);
                n1.setParent(root);
                root.addChild(n1.getData(), n1);
                return n1;
            }
            return node;
        } else {
            node = searchForNode(lg.getParent(), root);
            TreeNode<LocationGroup> child = node.getChild(lg);
            if (child == null) {
                child = new TreeNodeImpl<>();
                child.setData(lg);
                child.setParent(node);
                node.addChild(lg, child);
            }
            return child;
        }
    }
}